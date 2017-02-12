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

        styledAttributes.recycle();
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
}
