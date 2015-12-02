package ro.kenjiru.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import ro.kenjiru.ui.widget.richtexteditor.R;
import java.util.Map;

public class CheckBoxGroup extends LinearLayout {
    private String mFontType = null;

    private CheckedStateTracker mChildOnCheckedChangeListener;
    private OnCheckedChangeListener mOnCheckedChangeListener;
    private PassThroughHierarchyChangeListener mPassThroughListener;

    public CheckBoxGroup(Context context) {
        this(context, null);
    }

    public CheckBoxGroup(Context context, AttributeSet attrs) {
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

        TypedArray styledAttributes = getContext().obtainStyledAttributes(attrs, R.styleable.CheckBoxGroup);
        String fontTypeAttribute = styledAttributes.getString(R.styleable.CheckBoxGroup_fontType);

        mFontType = fontTypeAttribute;

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
        void onCheckedChanged(CheckBoxGroup group, int checkBoxId, boolean checked);
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
            if (parent == CheckBoxGroup.this && child instanceof CheckBox) {
                int id = child.getId();
                // generates an id if it's missing
                if (id == View.NO_ID) {
                    id = View.generateViewId();
                    child.setId(id);
                }

                ((CheckBox) child).setFontType(mFontType);
                ((CheckBox) child).setOnCheckedChangeListener(mChildOnCheckedChangeListener);
            }

            if (mOnHierarchyChangeListener != null) {
                mOnHierarchyChangeListener.onChildViewAdded(parent, child);
            }
        }

        /**
         * {@inheritDoc}
         */
        public void onChildViewRemoved(View parent, View child) {
            if (parent == CheckBoxGroup.this && child instanceof CheckBox) {
                ((CheckBox) child).setOnCheckedChangeListener(null);
            }

            if (mOnHierarchyChangeListener != null) {
                mOnHierarchyChangeListener.onChildViewRemoved(parent, child);
            }
        }
    }
}