package ro.kenjiru.ui.widgets.toolbar;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import ro.kenjiru.ui.widgets.R;

public class ToolbarWidget extends TextView {
    private String fontType = null;
    private float textSizeAttribute = 0;
    private int disabledColor = 0;
    private int previousColor = 0;
    private boolean disabled = false;

    public ToolbarWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    public ToolbarWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ToolbarWidget(Context context) {
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
        TypedArray styledAttributes = getContext().obtainStyledAttributes(attrs, R.styleable.ToolbarWidget);

        fontType = styledAttributes.getString(R.styleable.ToolbarWidget_fontType);
        updateFontType();
        // get the text size in pixels
        textSizeAttribute = styledAttributes.getDimensionPixelSize(R.styleable.ToolbarWidget_android_textSize, 0);

        previousColor = getCurrentTextColor();
        disabledColor = styledAttributes.getColor(R.styleable.ToolbarWidget_disabledColor, 0);
        updateDisabledTextColor();

        styledAttributes.recycle();
    }

    public int getDisabledColor() {
        return disabledColor;
    }

    public void setDisabledColor(int disabledColor) {
        this.disabledColor = disabledColor;
        updateDisabledTextColor();
    }

    public void setDisabled(boolean mDisabled) {
        if (this.disabled != mDisabled) {
            this.disabled = mDisabled;

            updateDisabledTextColor();
            setClickable(!mDisabled);
        }
    }

    public float getTextSizeAttribute() {
        return textSizeAttribute;
    }

    public String getFontType() {
        return fontType;
    }

    public void setFontType(String fontType) {
        this.fontType = fontType;
        updateFontType();
    }

    private void updateFontType() {
        if (fontType == null) {
            return;
        }

        AssetManager assets = getContext().getAssets();
        Typeface typeface = Typeface.createFromAsset(assets, fontType);

        setTypeface(typeface);
    }

    protected void updateDisabledTextColor() {
        if (disabledColor == 0) {
            return;
        }

        if (disabled) {
            previousColor = getCurrentTextColor();

            setTextColor(disabledColor);
        } else {
            setTextColor(previousColor);
        }
    }
}
