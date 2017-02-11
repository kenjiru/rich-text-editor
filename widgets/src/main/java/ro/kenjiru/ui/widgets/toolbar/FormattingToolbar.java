package ro.kenjiru.ui.widgets.toolbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.Map;

import ro.kenjiru.ui.widgets.R;

public class FormattingToolbar extends LinearLayout {
    private String fontType = null;
    private float boxTextSize;
    private int uncheckedColor;
    private int checkedColor;

    private CheckedStateTracker mChildOnCheckedChangeListener;
    private OnCheckedChangeListener mOnCheckedChangeListener;
    private PassThroughHierarchyChangeListener mPassThroughListener;

    public FormattingToolbar(Context context) {
        this(context, null);
    }

    public FormattingToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);

        mChildOnCheckedChangeListener = new CheckedStateTracker();
        mPassThroughListener = new PassThroughHierarchyChangeListener();
        super.setOnHierarchyChangeListener(mPassThroughListener);
    }

    private void init(AttributeSet attrs) {
        if (attrs == null) {
            return;
        }

        TypedArray styledAttributes = getContext().obtainStyledAttributes(attrs, R.styleable.FormattingToolbar);

        fontType = styledAttributes.getString(R.styleable.FormattingToolbar_fontType);
        boxTextSize = styledAttributes.getDimension(R.styleable.FormattingToolbar_boxTextSize, 0);
        uncheckedColor = styledAttributes.getColor(R.styleable.FormattingToolbar_uncheckedColor, 0);
        checkedColor = styledAttributes.getColor(R.styleable.FormattingToolbar_checkedColor, 0);

        styledAttributes.recycle();
    }

    @Override
    public void setOnHierarchyChangeListener(OnHierarchyChangeListener listener) {
        // the user listener is delegated to our pass-through listener
        mPassThroughListener.mOnHierarchyChangeListener = listener;
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (child instanceof CheckBox) {
            CheckBox button = (CheckBox) child;
            setChecked(button.getId(), button.isChecked());
        }

        super.addView(child, index, params);
    }

    public void checkAll(Map<Integer, Boolean> stateMap) {
        for (Map.Entry<Integer, Boolean> entry : stateMap.entrySet()) {
            this.check(entry.getKey(), entry.getValue());
        }
    }

    public void check(int checkBoxId, boolean checked) {
        setCheckedStateForView(checkBoxId, checked);
        setChecked(checkBoxId, checked);
    }

    private void setCheckedStateForView(int viewId, boolean checked) {
        View checkedView = findViewById(viewId);

        if (checkedView != null && checkedView instanceof CheckBox) {
            ((CheckBox) checkedView).setChecked(checked);
        }
    }

    private void setChecked(int checkBoxId, boolean checked) {
        if (mOnCheckedChangeListener != null) {
            mOnCheckedChangeListener.onCheckedChanged(this, checkBoxId, checked);
        }
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        mOnCheckedChangeListener = listener;
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(FormattingToolbar group, int checkBoxId, boolean checked);
    }

    private class CheckedStateTracker implements CheckBox.OnCheckedChangeListener {
        public void onCheckedChanged(CheckBox checkBox, boolean isChecked) {
            int buttonId = checkBox.getId();

            setChecked(buttonId, isChecked);
        }
    }

    private class PassThroughHierarchyChangeListener implements
            ViewGroup.OnHierarchyChangeListener {
        private ViewGroup.OnHierarchyChangeListener mOnHierarchyChangeListener;

        /**
         * {@inheritDoc}
         */
        public void onChildViewAdded(View parent, View child) {
            if (parent == FormattingToolbar.this && child instanceof CheckBox) {
                int id = child.getId();
                // generates an id if it's missing
                if (id == View.NO_ID) {
                    id = View.generateViewId();
                    child.setId(id);
                }

                CheckBox checkBox = (CheckBox) child;

                if (fontType != null && checkBox.getFontType() == null) {
                    checkBox.setFontType(fontType);
                }

                if (boxTextSize != 0 && checkBox.getBoxTextSize() == 0) {
                    checkBox.setBoxTextSize(boxTextSize);
                }
                if (uncheckedColor != 0 && checkBox.getUncheckedColor() == 0) {
                    checkBox.setUncheckedColor(uncheckedColor);
                }
                if (checkedColor != 0 && checkBox.getCheckedColor() == 0) {
                    checkBox.setCheckedColor(checkedColor);
                }

                checkBox.setOnCheckedChangeListener(mChildOnCheckedChangeListener);
            }

            if (mOnHierarchyChangeListener != null) {
                mOnHierarchyChangeListener.onChildViewAdded(parent, child);
            }
        }

        /**
         * {@inheritDoc}
         */
        public void onChildViewRemoved(View parent, View child) {
            if (parent == FormattingToolbar.this && child instanceof CheckBox) {
                ((CheckBox) child).setOnCheckedChangeListener(null);
            }

            if (mOnHierarchyChangeListener != null) {
                mOnHierarchyChangeListener.onChildViewRemoved(parent, child);
            }
        }
    }
}