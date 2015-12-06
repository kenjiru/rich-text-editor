package ro.kenjiru.ui.widgets.richtexteditor.experiment;

import android.text.Spannable;
import android.util.Log;

import ro.kenjiru.ui.widgets.richtexteditor.RichTextEditor;
import ro.kenjiru.ui.widgets.richtexteditor.Selection;

public abstract class ComplexDecoration<S, V> {
    private Class<S> spanClass;
    private Class<V> valueClass;
    private V value;

    public ComplexDecoration(Class<S> spanClass) {
        this.spanClass = spanClass;
    }

    public ComplexDecoration(Class<S> spanClass, Class<V> valueClass, V value) {
        this(spanClass);

        this.valueClass = valueClass;
        this.value = value;
    }

    public abstract V getSpanValue(S span);

    private boolean isSameSpan(S span) {
        return value == null || value.equals(getSpanValue(span));
    }

    public boolean existsInSelection(RichTextEditor editor) {
        Selection selection = new Selection(editor);
        Spannable str = editor.getText();
        boolean result;

        if (selection.start != selection.end) {
            S[] spansInSelection = getSpans(str, selection);

            result = existsInArray(spansInSelection);
        } else {
            S[] spansBefore = str.getSpans(selection.start - 1, selection.end, spanClass);
            S[] spansAfter = str.getSpans(selection.start, selection.end + 1, spanClass);

            result = existsInArray(spansBefore);
            if (result) {
                result = existsInArray(spansAfter);
            }
        }

        return result;
    }

    private boolean existsInArray(S[] spans) {
        for (S span : spans) {
            if (isSameSpan(span)) {
                return true;
            }
        }

        return false;
    }

    public V valueInSelection(RichTextEditor editor) {
        Selection selection = new Selection(editor);
        Spannable str = editor.getText();
        S[] spans = getSpans(str, selection);

        if (spans.length > 0) {
            return getSpanValue(spans[0]);
        }

        return null;
    }

    public void applyToSelection(RichTextEditor editor, Boolean add) {
        Spannable str = editor.getText();
        Selection selection = new Selection(editor);

        int startOfFirstSpan = Integer.MAX_VALUE;
        int endOfLastSpan = -1;
        S[] spans = getSpans(str, selection);

        for (S span : spans) {
            if (isSameSpan(span)) {
                int spanStart = str.getSpanStart(span);

                if (spanStart < selection.start) {
                    startOfFirstSpan = Math.min(startOfFirstSpan, spanStart);
                }

                int spanEnd = str.getSpanEnd(span);

                if (spanEnd > selection.end) {
                    endOfLastSpan = Math.max(endOfLastSpan, spanEnd);
                }

                str.removeSpan(span);
            }
        }


        if (add) {
            str.setSpan(newSpanInstance(), selection.start, selection.end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            if (startOfFirstSpan < Integer.MAX_VALUE) {
                str.setSpan(newSpanInstance(), startOfFirstSpan, selection.start, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            if (endOfLastSpan > -1) {
                str.setSpan(newSpanInstance(), selection.end, endOfLastSpan, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    private S newSpanInstance() {
        try {
            if (value == null) {
                return spanClass.newInstance();
            } else {
                return spanClass.getConstructor(valueClass).newInstance(value);
            }
        } catch (Exception e) {
            Log.e("RichTextEditor", "Exception instantiating " + spanClass.toString(), e);
        }
        return null;
    }

    private S[] getSpans(Spannable str, Selection selection) {
        return str.getSpans(selection.start, selection.end, spanClass);
    }
}