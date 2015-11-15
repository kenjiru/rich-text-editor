package ro.kenjiru.ui.widget;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import ro.kenjiru.ui.widget.richtexteditor.R;

public class CustomTextView extends TextView {

    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);

    }

    public CustomTextView(Context context) {
        super(context);
        init(null);
    }

    private void init(AttributeSet attrs) {
        if (attrs == null) {
            return;
        }

        TypedArray styledAttributes = getContext().obtainStyledAttributes(attrs, R.styleable.CustomTextView);
        String fontTypeAttribute = styledAttributes.getString(R.styleable.CustomTextView_fontType);

        if (fontTypeAttribute != null) {
            AssetManager assets = getContext().getAssets();
            Typeface typeface = Typeface.createFromAsset(assets, "fonts/" + fontTypeAttribute);

            setTypeface(typeface);
        }
        styledAttributes.recycle();
    }

}