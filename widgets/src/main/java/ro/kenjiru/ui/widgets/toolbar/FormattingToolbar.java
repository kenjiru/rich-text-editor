package ro.kenjiru.ui.widgets.toolbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.Map;

import ro.kenjiru.ui.widgets.R;

public class FormattingToolbar extends LinearLayout {
    private String fontType = null;
    private float textSizeAttribute;
    private int disabledColor;
    private int uncheckedColor;
    private int checkedColor;

    // OnClickListener used to track the child click events
    private OnClickListener mOnClickListener;
    // Custom listener to track which button was clicked
    private OnChildClickListener mOnChildClickListener;

    // used to track the state of the child views
    private CheckBox.OnCheckedChangeListener mChildOnCheckedChangeListener;
    // used to notify external views when a child check changed
    private OnChildCheckedChangeListener mOnChildCheckedChangeListener;
    private PassThroughHierarchyChangeListener mPassThroughListener;

    public FormattingToolbar(Context context) {
        this(context, null);
    }

    public FormattingToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);

        mChildOnCheckedChangeListener = new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CheckBox checkBoxView, boolean isChecked) {
                notifyChecked(checkBoxView.getId(), isChecked);
            }
        };

        mPassThroughListener = new PassThroughHierarchyChangeListener();
        super.setOnHierarchyChangeListener(mPassThroughListener);

        mOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnChildClickListener != null) {
                    mOnChildClickListener.onChildClick(v);
                }
            }
        };
    }

    private void init(AttributeSet attrs) {
        if (attrs == null) {
            return;
        }

        TypedArray styledAttributes = getContext().obtainStyledAttributes(attrs, R.styleable.FormattingToolbar);

        textSizeAttribute = styledAttributes.getDimensionPixelSize(R.styleable.FormattingToolbar_android_textSize, 0);
        fontType = styledAttributes.getString(R.styleable.FormattingToolbar_fontType);
        uncheckedColor = styledAttributes.getColor(R.styleable.FormattingToolbar_uncheckedColor, 0);
        checkedColor = styledAttributes.getColor(R.styleable.FormattingToolbar_checkedColor, 0);
        disabledColor = styledAttributes.getColor(R.styleable.FormattingToolbar_disabledColor, 0);

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
            notifyChecked(button.getId(), button.isChecked());
        }

        super.addView(child, index, params);
    }

    public void disableAll(Map<Integer, Boolean> stateMap) {
        for (Map.Entry<Integer, Boolean> entry : stateMap.entrySet()) {
            this.disable(entry.getKey(), entry.getValue());
        }
    }

    public void disable(int viewId, boolean disabled) {
        View view = findViewById(viewId);

        if (view != null && view instanceof ToolbarWidget) {
            ((ToolbarWidget) view).setDisabled(disabled);
        }
    }

    public void checkAll(Map<Integer, Boolean> stateMap) {
        for (Map.Entry<Integer, Boolean> entry : stateMap.entrySet()) {
            this.check(entry.getKey(), entry.getValue());
        }
    }

    public void check(int checkBoxId, boolean checked) {
        setCheckedStateForView(checkBoxId, checked);
        notifyChecked(checkBoxId, checked);
    }

    private void setCheckedStateForView(int viewId, boolean checked) {
        View checkedView = findViewById(viewId);

        if (checkedView != null && checkedView instanceof CheckBox) {
            ((CheckBox) checkedView).setChecked(checked);
        }
    }

    private void notifyChecked(int checkBoxId, boolean checked) {
        if (mOnChildCheckedChangeListener != null) {
            mOnChildCheckedChangeListener.onCheckedChanged(this, checkBoxId, checked);
        }
    }

    public void setOnCheckedChangeListener(OnChildCheckedChangeListener listener) {
        mOnChildCheckedChangeListener = listener;
    }

    public void setOnChildClickListener(OnChildClickListener listener) {
        mOnChildClickListener = listener;
    }

    public interface OnChildClickListener {
        void onChildClick(View v);
    }

    public interface OnChildCheckedChangeListener {
        void onCheckedChanged(FormattingToolbar group, int checkBoxId, boolean checked);
    }

    private class PassThroughHierarchyChangeListener implements
            ViewGroup.OnHierarchyChangeListener {
        private ViewGroup.OnHierarchyChangeListener mOnHierarchyChangeListener;

        /**
         * {@inheritDoc}
         */
        public void onChildViewAdded(View parent, View child) {
            if (parent != FormattingToolbar.this) {
                return;
            }

            int id = child.getId();
            // generates an id if it's missing
            if (id == View.NO_ID) {
                id = View.generateViewId();
                child.setId(id);
            }

            if (child instanceof ToolbarWidget) {
                ToolbarWidget toolbarWidget = (ToolbarWidget) child;

                if (fontType != null && toolbarWidget.getFontType() == null) {
                    toolbarWidget.setFontType(fontType);
                }

                if (textSizeAttribute != 0 && toolbarWidget.getTextSizeAttribute() == 0) {
                    toolbarWidget.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeAttribute);
                }

                if (disabledColor != 0 && toolbarWidget.getDisabledColor() == 0) {
                    toolbarWidget.setDisabledColor(disabledColor);
                }
            }

            if (child instanceof Button) {
                child.setOnClickListener(mOnClickListener);
            }

            if (child instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) child;

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
            if (parent == FormattingToolbar.this && child instanceof Button) {
                child.setOnClickListener(null);
            }

            if (parent == FormattingToolbar.this && child instanceof CheckBox) {
                ((CheckBox) child).setOnCheckedChangeListener(null);
            }

            if (mOnHierarchyChangeListener != null) {
                mOnHierarchyChangeListener.onChildViewRemoved(parent, child);
            }
        }
    }
}