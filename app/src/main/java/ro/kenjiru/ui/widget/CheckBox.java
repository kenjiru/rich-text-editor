package ro.kenjiru.ui.widget;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.Checkable;
import android.widget.TextView;

import ro.kenjiru.ui.widget.richtexteditor.R;

public class CheckBox extends TextView implements Checkable {
    private int checkedColor = Color.CYAN;
    private int uncheckedColor = Color.BLACK;

    private boolean mChecked;

    public CheckBox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    public CheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);

    }

    public CheckBox(Context context) {
        super(context);
        init(null);
    }

    private void init(AttributeSet attrs) {
        if (attrs == null) {
            return;
        }

        setClickable(true);
        setFocusable(true);
        setGravity(Gravity.CENTER);
        setBackground(getResources().getDrawable(R.drawable.rte_button_background));

        uncheckedColor = getCurrentTextColor();

        TypedArray styledAttributes = getContext().obtainStyledAttributes(attrs, R.styleable.CheckBox);
        String fontTypeAttribute = styledAttributes.getString(R.styleable.CheckBox_fontType);

        if (fontTypeAttribute != null) {
            AssetManager assets = getContext().getAssets();
            Typeface typeface = Typeface.createFromAsset(assets, fontTypeAttribute);

            setTypeface(typeface);
        }
        styledAttributes.recycle();
    }

    @Override
    public boolean performClick() {
        toggle();

        return super.performClick();
    }

    @Override
    public void setChecked(boolean checked) {
        if (mChecked != checked) {
            mChecked = checked;

            int textColor = checked ? checkedColor : uncheckedColor;
            setTextColor(textColor);
        }
    }

    @Override
    public boolean isChecked() {
        return false;
    }

    @Override
    public void toggle() {
        setChecked(!mChecked);
    }
}
