package ro.kenjiru.app.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.HashMap;

import ro.kenjiru.ui.widgets.CheckBoxGroup;
import ro.kenjiru.ui.widgets.richtexteditor.RichTextEditor;

public class RichTextEditorActivity extends AppCompatActivity
        implements RichTextEditor.OnSelectionChangedListener, CheckBoxGroup.OnCheckedChangeListener {
    private static final String TAG = "RichTextEditorActivity";

    private RichTextEditor editor;
    private CheckBoxGroup checkBoxGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rich_text_editor);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        CheckBoxGroup checkBoxGroup = (CheckBoxGroup) findViewById(R.id.format_checkbox_group);
        checkBoxGroup.setOnCheckedChangeListener(this);
        this.checkBoxGroup = checkBoxGroup;

        RichTextEditor editor = (RichTextEditor) findViewById(R.id.editor);
        editor.addOnSelectionChangedListener(this);
        this.editor = editor;
    }

    @Override
    public void onCheckedChanged(CheckBoxGroup group, int checkBoxId, boolean checked) {
        switch (checkBoxId) {
            case R.id.bold:
                editor.toggleBold();
                break;
            case R.id.italic:
                editor.toggleItalic();
                break;
            case R.id.underline:
                editor.toggleUnderline();
                break;
        }
    }

    @Override
    public void onSelectionChanged(final RichTextEditor richTextEditor, int selStart, int selEnd) {
        Log.i(TAG, String.format("Selection start: %d, end: %d", selStart, selEnd));

        if (selStart != selEnd) {
            return;
        }

        checkBoxGroup.setOnCheckedChangeListener(null);

        checkBoxGroup.checkAll(new HashMap<Integer, Boolean>() {{
            put(R.id.bold, richTextEditor.isBold());
            put(R.id.italic, richTextEditor.isItalic());
            put(R.id.underline, richTextEditor.isUnderline());
        }});
        checkBoxGroup.setOnCheckedChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.rich_text_editor_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_toggle_format_toolbar) {
            toggleFormattingToolbar();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void toggleFormattingToolbar() {
        View toolbar = findViewById(R.id.format_toolbar);

        if (toolbar.getVisibility() == View.VISIBLE) {
            toolbar.setVisibility(View.GONE);
        } else {
            toolbar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public ActionMode startActionMode(final ActionMode.Callback callback) {
        // Fix for bug https://code.google.com/p/android/issues/detail?id=159527
        final ActionMode mode = super.startActionMode(callback);

        if (mode != null) {
            mode.invalidate();
        }

        return mode;
    }
}
