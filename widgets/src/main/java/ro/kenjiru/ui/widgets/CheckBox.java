package ro.kenjiru.ui.widgets;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.Checkable;
import android.widget.TextView;

public class CheckBox extends TextView implements Checkable {
    private float defaultBoxTextSize = 18;
    private int defaultUncheckedColor;
    private int defaultCheckedColor = Color.CYAN;

    private String fontType = null;
    private float boxTextSize = 0;
    private int uncheckedColor = 0;
    private int checkedColor = 0;

    private boolean mChecked;
    private OnCheckedChangeListener mOnCheckedChangeListener;
    private boolean mBroadcasting;

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

        configureDefaultAttributes();
        updateAttributes(attrs);
    }

    private void configureDefaultAttributes() {
        setClickable(true);
        setFocusable(true);
        setGravity(Gravity.CENTER);
        setBackground(getResources().getDrawable(R.drawable.rte_button_background));
    }

    private void updateAttributes(AttributeSet attrs) {
        TypedArray styledAttributes = getContext().obtainStyledAttributes(attrs, R.styleable.CheckBox);

        fontType = styledAttributes.getString(R.styleable.CheckBox_fontType);
        updateFontType();

        boxTextSize = styledAttributes.getDimension(R.styleable.CheckBox_boxTextSize, defaultBoxTextSize);
        updateTextSize();

        defaultUncheckedColor = getCurrentTextColor();
        mChecked = styledAttributes.getBoolean(R.styleable.CheckBox_checked, false);
        uncheckedColor = styledAttributes.getColor(R.styleable.CheckBox_uncheckedColor, defaultUncheckedColor);
        checkedColor = styledAttributes.getColor(R.styleable.CheckBox_checkedColor, defaultCheckedColor);
        updateTextColor();

        styledAttributes.recycle();
    }

    public String getFontType() {
        return fontType;
    }

    public float getBoxTextSize() {
        return boxTextSize;
    }

    public int getUncheckedColor() {
        return uncheckedColor;
    }

    public int getCheckedColor() {
        return checkedColor;
    }

    public void setFontType(String fontType) {
        this.fontType = fontType;
        updateFontType();
    }

    public void setBoxTextSize(float textSize) {
        this.boxTextSize = textSize;
        updateTextSize();
    }

    public void setUncheckedColor(int uncheckedColor) {
        this.uncheckedColor = uncheckedColor;
        updateTextColor();
    }

    public void setCheckedColor(int checkedColor) {
        this.checkedColor = checkedColor;
        updateTextColor();
    }

    private void updateFontType() {
        if (fontType == null) {
            return;
        }

        AssetManager assets = getContext().getAssets();
        Typeface typeface = Typeface.createFromAsset(assets, fontType);

        setTypeface(typeface);
    }

    private void updateTextSize() {
        setTextSize(boxTextSize);
    }

    private void updateTextColor() {
        int textColor = mChecked ? checkedColor : uncheckedColor;
        setTextColor(textColor);
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        mOnCheckedChangeListener = listener;
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

            this.updateTextColor();

            // Avoid infinite recursions if setChecked() is called from a listener
            if (mBroadcasting) {
                return;
            }

            mBroadcasting = true;
            if (mOnCheckedChangeListener != null) {
                mOnCheckedChangeListener.onCheckedChanged(this, mChecked);
            }
            mBroadcasting = false;
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

    public static interface OnCheckedChangeListener {
        void onCheckedChanged(CheckBox checkBoxView, boolean isChecked);
    }
}
