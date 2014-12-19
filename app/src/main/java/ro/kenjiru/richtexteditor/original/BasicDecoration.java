package ro.kenjiru.richtexteditor.original;

import android.text.Spannable;
import android.util.Log;

import ro.kenjiru.richtexteditor.RichTextEditor;
import ro.kenjiru.richtexteditor.Selection;

public class BasicDecoration<T> implements Decoration<Boolean> {
    private Class<T> spanClass;

    BasicDecoration(Class<T> spanClass) {
        this.spanClass = spanClass;
    }

    @Override
    public boolean existsInSelection(RichTextEditor editor) {
        Selection selection = new Selection(editor);
        Spannable str = editor.getText();
        boolean result = false;

        if (selection.start != selection.end) {
            T[] spans = str.getSpans(selection.start, selection.end, spanClass);

            result = (spans.length > 0);
        } else {
            T[] spansBefore = str.getSpans(selection.start - 1, selection.end, spanClass);
            T[] spansAfter = str.getSpans(selection.start, selection.end + 1, spanClass);

            result = (spansBefore.length > 0 && spansAfter.length > 0);
        }

        return result;
    }

    @Override
    public Boolean valueInSelection(RichTextEditor editor) {
        return (existsInSelection(editor));
    }

    @Override
    public void applyToSelection(RichTextEditor editor, Boolean add) {
        applyToSpannable(editor.getText(), new Selection(editor), add);
    }

    void applyToSpannable(Spannable spannable, Selection selection, Boolean add) {
        T[] spans = spannable.getSpans(selection.start, selection.end, spanClass);
        int startOfFirstSpan = Integer.MAX_VALUE;
        int endOfLastSpan = -1;

        for (T span : spans) {
            int spanStart = spannable.getSpanStart(span);

            if (spanStart < selection.start) {
                startOfFirstSpan = Math.min(startOfFirstSpan, spanStart);
            }

            int spanEnd = spannable.getSpanEnd(span);

            if (spanEnd > selection.end) {
                endOfLastSpan = Math.max(endOfLastSpan, spanEnd);
            }

            spannable.removeSpan(span);
        }

        try {
            if (add) {
                spannable.setSpan(spanClass.newInstance(), selection.start,
                        selection.end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                if (startOfFirstSpan < Integer.MAX_VALUE) {
                    spannable.setSpan(spanClass.newInstance(), startOfFirstSpan,
                            selection.start, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

                if (endOfLastSpan > -1) {
                    spannable.setSpan(spanClass.newInstance(), selection.end,
                            endOfLastSpan, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        } catch (IllegalAccessException e) {
            Log.e("RichTextEditor", "Exception instantiating " + spanClass.toString(), e);
        } catch (InstantiationException e) {
            Log.e("RichTextEditor", "Exception instantiating " + spanClass.toString(), e);
        }
    }
}
