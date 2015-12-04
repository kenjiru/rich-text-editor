package ro.kenjiru.ui.widgets.richtexteditor;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import ro.kenjiru.ui.widgets.R;
import ro.kenjiru.ui.widgets.richtexteditor.actionmode.ActionModeCallback;
import ro.kenjiru.ui.widgets.richtexteditor.actionmode.ActionModeListener;
import ro.kenjiru.ui.widgets.richtexteditor.experiment.ComplexDecoration;

public class RichTextEditor extends EditText implements ActionModeListener {
    UnderlineDecoration underlineDecoration = new UnderlineDecoration();
    StyleDecoration boldDecoration = new StyleDecoration(Typeface.BOLD);
    StyleDecoration italicDecoration = new StyleDecoration(Typeface.ITALIC);

    public RichTextEditor(Context context) {
        super(context);
    }

    public RichTextEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RichTextEditor(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Alternative method to add actions to the context action group.
     * Unlike enableActionModes, it doesn't support nested groups.
     */
    public void enableContextOptions() {
        setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // hide the copy/paste menu
                menu.setGroupVisible(0, false);

                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.context_menu_effects, menu);

                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                int i = item.getItemId();
                if (i == R.id.action_underline) {
                    makeUnderline();
                    return true;
                } else if (i == R.id.action_bold) {
                    makeBold();
                    return true;
                } else if (i == R.id.action_italic) {
                    makeItalic();
                    return true;
                }

                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });
    }

    /**
     * Adds extra actions to the main context action group of the EditText.
     */
    public void enableActionModes() {
        ActionModeCallback effectsMode = new ActionModeCallback(
                (Activity) getContext(),
                R.menu.context_menu_effects,
                this, this);

        ActionModeCallback styleMode = new ActionModeCallback(
                (Activity) getContext(),
                R.menu.context_menu_style,
                this, this);

        ActionModeCallback mainMode = new ActionModeCallback(
                (Activity) getContext(),
                R.menu.context_menu_main,
                this, this);

        mainMode.addChain(R.id.action_effects, effectsMode);
        mainMode.addChain(R.id.action_style, styleMode);

        setCustomSelectionActionModeCallback(mainMode);
    }

    public void makeUnderline() {
        underlineDecoration.applyToSelection(this, true);
    }

    public void makeBold() {
        boldDecoration.applyToSelection(this, true);
    }

    public void makeItalic() {
        italicDecoration.applyToSelection(this, true);
    }

    @Override
    public boolean doAction(int itemId) {
        if (itemId == R.id.action_underline) {
            makeUnderline();
            return true;
        } else if (itemId == R.id.action_bold) {
            makeBold();
            return true;
        } else if (itemId == R.id.action_italic) {
            makeItalic();
            return true;
        }

        return false;
    }

    @Override
    public void setIsShowing(boolean isShowing) {

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