package ro.kenjiru.richtexteditor.original;

import android.text.Spannable;
import android.text.style.StyleSpan;

import ro.kenjiru.richtexteditor.RichTextEditor;
import ro.kenjiru.richtexteditor.Selection;

public class StyleDecoration implements Decoration<Boolean> {
    private int style;

    StyleDecoration(int style) {
        this.style = style;
    }

    @Override
    public boolean existsInSelection(RichTextEditor editor) {
        Selection selection = new Selection(editor);
        Spannable str = editor.getText();
        boolean result = false;

        if (selection.start != selection.end) {
            for (StyleSpan span : getStyleSpans(str, selection)) {
                if (span.getStyle() == style) {
                    result = true;
                    break;
                }
            }
        } else {
            StyleSpan[] spansBefore = str.getSpans(selection.start - 1, selection.end, StyleSpan.class);
            StyleSpan[] spansAfter = str.getSpans(selection.start, selection.end + 1, StyleSpan.class);

            for (StyleSpan span : spansBefore) {
                if (span.getStyle() == style) {
                    result = true;
                    break;
                }
            }

            if (result) {
                result = false;

                for (StyleSpan span : spansAfter) {
                    if (span.getStyle() == style) {
                        result = true;
                        break;
                    }
                }
            }
        }

        return (result);
    }

    @Override
    public Boolean valueInSelection(RichTextEditor editor) {
        return (existsInSelection(editor));
    }

    @Override
    public void applyToSelection(RichTextEditor editor, Boolean add) {
        applyToSpannable(editor.getText(), new Selection(editor), add);
    }

    void applyToSpannable(Spannable str, Selection selection, Boolean add) {
        int startOfFirstSpan = Integer.MAX_VALUE;
        int endOfLastSpan = -1;

        for (StyleSpan span : getStyleSpans(str, selection)) {
            if (span.getStyle() == style) {
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
            str.setSpan(new StyleSpan(style), selection.start, selection.end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            if (startOfFirstSpan < Integer.MAX_VALUE) {
                str.setSpan(new StyleSpan(style), startOfFirstSpan, selection.start, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            if (endOfLastSpan > -1) {
                str.setSpan(new StyleSpan(style), selection.end, endOfLastSpan, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    private StyleSpan[] getStyleSpans(Spannable str, Selection selection) {
        return (str.getSpans(selection.start, selection.end, StyleSpan.class));
    }
}

