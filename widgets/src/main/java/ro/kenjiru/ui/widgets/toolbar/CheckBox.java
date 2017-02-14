package ro.kenjiru.ui.widgets.toolbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.Checkable;

import ro.kenjiru.ui.widgets.R;

public class CheckBox extends ToolbarWidget implements Checkable {
    private int defaultCheckedColor = Color.CYAN;

    private int previousColor = 0;
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

        previousColor = getCurrentTextColor();
        mChecked = styledAttributes.getBoolean(R.styleable.CheckBox_checked, false);
        checkedColor = styledAttributes.getColor(R.styleable.CheckBox_checkedColor, defaultCheckedColor);
        updateCheckedTextColor();

        styledAttributes.recycle();
    }

    public int getCheckedColor() {
        return checkedColor;
    }

    public void setCheckedColor(int checkedColor) {
        this.checkedColor = checkedColor;
        updateCheckedTextColor();
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

            this.updateCheckedTextColor();

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

    protected void updateCheckedTextColor() {
        if (mChecked) {
            previousColor = getCurrentTextColor();

            setTextColor(checkedColor);
        } else {
            setTextColor(previousColor);
        }
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(CheckBox checkBoxView, boolean isChecked);
    }
}
