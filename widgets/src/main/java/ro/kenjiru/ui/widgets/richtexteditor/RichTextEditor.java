package ro.kenjiru.ui.widgets.richtexteditor;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.widget.EditText;

import ro.kenjiru.ui.widgets.richtexteditor.experiment.ComplexDecoration;
import ro.kenjiru.ui.widgets.richtexteditor.spans.BulletSpan;

public class RichTextEditor extends EditText {
    UnderlineDecoration underlineDecoration = new UnderlineDecoration();
    StyleDecoration boldDecoration = new StyleDecoration(Typeface.BOLD);
    StyleDecoration italicDecoration = new StyleDecoration(Typeface.ITALIC);
    ListDecoration listDecoration = new ListDecoration();
    StrikeThroughDecoration strikeThroughDecoration = new StrikeThroughDecoration();

    private OnSelectionChangedListener mOnSelectionChangedListener;
    private boolean isProcessing = false;
    private boolean isCurrentParagraphEmpty = false;
    private boolean shouldExitSpan = false;
    private int textChangedStart;

    public RichTextEditor(Context context) {
        super(context);
    }

    public RichTextEditor(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.attachTextWatcher();
    }

    public RichTextEditor(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void attachTextWatcher() {
        final RichTextEditor editor = this;

        this.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence str, int start, int count, int after) {
                Selection currentSelection = new Selection(editor);

                isCurrentParagraphEmpty = currentSelection.isCurrentParagraphEmpty(str);
            }

            @Override
            public void onTextChanged(CharSequence str, int start, int before, int count) {
                if (isCurrentParagraphEmpty) {
                    shouldExitSpan = listDecoration.isSpanningTwoParagraphs(editor);
                    textChangedStart = start;

                    isCurrentParagraphEmpty = false;
                }
            }

            @Override
            public void afterTextChanged(Editable str) {
                if (isProcessing == false) {
                    isProcessing = true;

                    if (shouldExitSpan) {
                        listDecoration.exitEmptySpan(str, textChangedStart);
                        shouldExitSpan = false;
                    } else {
                        listDecoration.fixParagraphs(editor);
                    }

                    isProcessing = false;
                }
            }
        });
    }

    public void toggleUnderline() {
        boolean add = !isUnderline();
        underlineDecoration.applyToSelection(this, add);
    }

    public void toggleBold() {
        boolean add = !isBold();
        boldDecoration.applyToSelection(this, add);
    }

    public void toggleItalic() {
        boolean add = !isItalic();
        italicDecoration.applyToSelection(this, add);
    }

    public void toggleStrikeThrough() {
        boolean add = !isStrikeThrough();
        strikeThroughDecoration.applyToSelection(this, add);
    }

    public void toggleList() {
        boolean add = !isList();
        listDecoration.applyToAllLinesInSelection(this, add);
    }

    public void indentList() {
        listDecoration.indent(this);
        this.invalidate();
    }

    public void outdentList() {
        listDecoration.outdent(this);
    }

    public boolean isUnderline() {
        return underlineDecoration.existsInSelection(this);
    }

    public boolean isBold() {
        return boldDecoration.existsInSelection(this);
    }

    public boolean isItalic() {
        return italicDecoration.existsInSelection(this);
    }

    public boolean isStrikeThrough() {
        return strikeThroughDecoration.existsInSelection(this);
    }

    public boolean isList() {
        return listDecoration.areAllLinesWrapped(this);
    }

    public void addOnSelectionChangedListener(OnSelectionChangedListener onSelectionChangedListener) {
        mOnSelectionChangedListener = onSelectionChangedListener;
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        if (mOnSelectionChangedListener != null) {
            mOnSelectionChangedListener.onSelectionChanged(this, selStart, selEnd);
        }
    }

    public interface OnSelectionChangedListener {
        void onSelectionChanged(RichTextEditor richTextEditor, int selStart, int selEnd);
    }

    class StrikeThroughDecoration extends ComplexDecoration<StrikethroughSpan, Boolean> {

        public StrikeThroughDecoration() {
            super(StrikethroughSpan.class);
        }

        @Override
        public Boolean getSpanValue(StrikethroughSpan span) {
            return true;
        }
    }

    class UnderlineDecoration extends ComplexDecoration<UnderlineSpan, Boolean> {

        public UnderlineDecoration() {
            super(UnderlineSpan.class);
        }

        @Override
        public Boolean getSpanValue(UnderlineSpan span) {
            return true;
        }
    }

    class StyleDecoration extends ComplexDecoration<StyleSpan, Integer> {

        public StyleDecoration(int style) {
            super(StyleSpan.class, int.class, style);
        }

        @Override
        public Integer getSpanValue(StyleSpan span) {
            return span.getStyle();
        }
    }

    class ListDecoration extends ComplexDecoration<BulletSpan, Integer> {
        public ListDecoration() {
            super(BulletSpan.class, int.class, 50);
        }

        @Override
        public Integer getSpanValue(BulletSpan span) {
            return 50;
        }
    }
}