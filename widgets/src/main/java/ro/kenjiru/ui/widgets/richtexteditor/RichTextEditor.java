package ro.kenjiru.ui.widgets.richtexteditor;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;

import ro.kenjiru.ui.widgets.richtexteditor.experiment.ComplexDecoration;
import ro.kenjiru.ui.widgets.richtexteditor.spans.BulletSpan;

public class RichTextEditor extends EditText {
    UnderlineDecoration underlineDecoration = new UnderlineDecoration();
    StyleDecoration boldDecoration = new StyleDecoration(Typeface.BOLD);
    StyleDecoration italicDecoration = new StyleDecoration(Typeface.ITALIC);
    ListDecoration listDecoration = new ListDecoration();

    private OnSelectionChangedListener mOnSelectionChangedListener;
    private boolean isProcessing = false;

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
                Log.i("RichTextEditor", "beforeTextChanged");
            }

            @Override
            public void onTextChanged(CharSequence str, int start, int before, int count) {
                Log.i("RichTextEditor", "onTextChanged");
            }

            @Override
            public void afterTextChanged(Editable str) {
                if (isProcessing == false) {
                    isProcessing = true;
                    listDecoration.fixParagraphs(editor);
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

    public void toggleList() {
        boolean add = !isList();
        listDecoration.applyToAllLinesInSelection(this, add);
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