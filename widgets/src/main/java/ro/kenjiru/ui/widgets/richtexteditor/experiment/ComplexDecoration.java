package ro.kenjiru.ui.widgets.richtexteditor.experiment;

import android.text.Editable;
import android.text.Spannable;
import android.util.Log;

import java.util.List;

import ro.kenjiru.ui.widgets.richtexteditor.RichTextEditor;
import ro.kenjiru.ui.widgets.richtexteditor.Selection;
import ro.kenjiru.ui.widgets.richtexteditor.spans.BulletSpan;

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

    public boolean areAllLinesWrapped(RichTextEditor editor) {
        Spannable spannable = editor.getText();
        Selection selection = new Selection(editor).extendToFullLines(spannable);

        S[] spansInSelection = getSpans(spannable, selection);
        List<Selection> paragraphs = selection.getParagraphsInSelection(spannable);

        if (spansInSelection.length != paragraphs.size()) {
            return false;
        }

        int i = 0;
        for (Selection paragraph : paragraphs) {
            if (spannable.getSpanStart(spansInSelection[i]) != paragraph.start ||
                    spannable.getSpanEnd(spansInSelection[i]) != paragraph.end) {
                return false;
            }

            i++;
        }

        return true;
    }

    private boolean existsInArray(S[] spans) {
        for (S span : spans) {
            if (hasSameValue(span)) {
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

    public void applyToAllLinesInSelection(RichTextEditor editor, Boolean add) {
        Spannable str = editor.getText();
        Selection selection = new Selection(editor);

        List<Selection> paragraphs = selection.getParagraphsInSelection(str);
        Log.i("RichTextEditor", "paragraphs" + paragraphs.toString());

        for (S span : getSpans(str, selection)) {
            str.removeSpan(span);
        }

        if (add) {
            for (Selection paragraph : paragraphs) {
                str.setSpan(newSpanInstance(), paragraph.start, paragraph.end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            }
        }
    }

    public void applyToSelection(RichTextEditor editor, Boolean add) {
        applyToSelection(editor, add, this.value);
    }

    public void applyToSelection(RichTextEditor editor, Boolean add, V value) {
        Spannable str = editor.getText();
        Selection selection = new Selection(editor);

        int startOfFirstSpan = Integer.MAX_VALUE;
        int endOfLastSpan = -1;
        S[] spans = getSpans(str, selection);

        for (S span : spans) {
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


        if (add) {
            str.setSpan(newSpanInstance(value), selection.start, selection.end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            if (startOfFirstSpan < Integer.MAX_VALUE) {
                str.setSpan(spans[0], startOfFirstSpan, selection.start, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            if (endOfLastSpan > -1) {
                str.setSpan(spans[spans.length - 1], selection.end, endOfLastSpan, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    public void fixParagraphs(RichTextEditor editor) {
        Spannable spannable = editor.getText();
        Selection selection = new Selection(editor);

        this.fixSpansOnMultipleParagraphs(spannable, selection);
        this.fixMultipleSpansOnSingleParagraph(spannable, selection);
    }

    private void fixSpansOnMultipleParagraphs(Spannable spannable, Selection selection) {
        S[] spansInSelection = getSpans(spannable, selection);

        if (spansInSelection.length < 1) {
            return;
        }

        int spanStart = spannable.getSpanStart(spansInSelection[0]);
        int spanEnd = spannable.getSpanEnd(spansInSelection[spansInSelection.length-1]);
        Selection selectionForSpan = new Selection(spanStart, spanEnd);

        List<Selection> paragraphs = selectionForSpan.getParagraphsInSelection(spannable);

        if (paragraphs.size() < 2) {
            return;
        }

        for (S span : spansInSelection) {
            spannable.removeSpan(span);
        }

        for (Selection paragraph : paragraphs) {
            spannable.setSpan(newSpanInstance(), paragraph.start, paragraph.end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }
    }

    private void fixMultipleSpansOnSingleParagraph(Spannable spannable, Selection selection) {
        List<Selection> paragraphs = selection.getParagraphsInSelection(spannable);

        if (paragraphs.size() != 1) {
            return;
        }

        Selection firstParagraph = paragraphs.get(0);
        S[] spansInParagraph = getSpans(spannable, firstParagraph);

        if (spansInParagraph.length < 2) {
            return;
        }

        for (S span: spansInParagraph) {
            spannable.removeSpan(span);
        }

        spannable.setSpan(newSpanInstance(), firstParagraph.start, firstParagraph.end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
    }

    public boolean isSpanningTwoParagraphs(RichTextEditor editor) {
        Spannable spannable = editor.getText();
        Selection selection = new Selection(editor);

        S[] spansInSelection = getSpans(spannable, selection);
        if (spansInSelection.length != 1) {
            return false;
        }

        int spanStart = spannable.getSpanStart(spansInSelection[0]);
        int spanEnd = spannable.getSpanEnd(spansInSelection[0]);
        Selection selectionForSpan = new Selection(spanStart, spanEnd);

        List<Selection> paragraphs = selectionForSpan.getParagraphsInSelection(spannable);

        return paragraphs.size() == 2;
    }

    public void exitEmptySpan(Editable str, int start) {
        str.delete(start, start + 1);

        Selection selection = new Selection(start, start);
        S[] spansInSelection = getSpans(str, selection);

        if (spansInSelection.length != 1) {
            return;
        }

        str.removeSpan(spansInSelection[0]);
    }

    public void indent(RichTextEditor editor) {
        updateIndentation(editor, true);
    }

    public void outdent(RichTextEditor editor) {
        updateIndentation(editor, false);
    }

    private void updateIndentation(RichTextEditor editor, boolean indent) {
        Spannable spannable = editor.getText();
        Selection selection = new Selection(editor);
        S[] spansInSelection = getSpans(spannable, selection);

        for (S span: spansInSelection) {
            // FIXME This method will only handle BulletSpans, so it should be extracted
            BulletSpan bulletSpan = (BulletSpan) span;

            int spanStart = spannable.getSpanStart(bulletSpan);
            int spanEnd = spannable.getSpanEnd(bulletSpan);

            if (indent) {
                bulletSpan.indent();
            } else {
                bulletSpan.outdent();
            }

            // We set the same span again in order to force the EditText to redraw the span.
            // This does not reflow the text.
            // There might be better ways achieve the same goal.
            spannable.setSpan(bulletSpan, spanStart, spanEnd, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }
    }

    private S newSpanInstance() {
        return newSpanInstance(this.value);
    }

    private S newSpanInstance(V value) {
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

    private boolean hasSameValue(S span) {
        return value == null || value.equals(getSpanValue(span));
    }

    public abstract V getSpanValue(S span);
}
