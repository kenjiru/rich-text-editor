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

import ro.kenjiru.ui.widgets.toolbar.FormattingToolbar;
import ro.kenjiru.ui.widgets.richtexteditor.RichTextEditor;

public class RichTextEditorActivity extends AppCompatActivity
        implements RichTextEditor.OnSelectionChangedListener, FormattingToolbar.OnChildCheckedChangeListener,
        FormattingToolbar.OnChildClickListener {

    private static final String TAG = "RichTextEditorActivity";

    private RichTextEditor editor;
    private FormattingToolbar formattingToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rich_text_editor);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        FormattingToolbar formattingToolbar = (FormattingToolbar) findViewById(R.id.format_checkbox_group);
        formattingToolbar.setOnCheckedChangeListener(this);
        formattingToolbar.setOnChildClickListener(this);
        this.formattingToolbar = formattingToolbar;

        RichTextEditor editor = (RichTextEditor) findViewById(R.id.editor);
        editor.addOnSelectionChangedListener(this);
        this.editor = editor;
    }

    @Override
    public void onChildClick(View view) {
        Log.i(TAG, String.format("onChildButtonClicked > button clicked: %d", view.getId()));

        switch (view.getId()) {
            case R.id.indent:
                editor.indentList();
                break;

            case R.id.outdent:
                editor.outdentList();
                break;
        }
    }

    @Override
    public void onCheckedChanged(FormattingToolbar group, int checkBoxId, boolean checked) {
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

            case R.id.strike:
                editor.toggleStrikeThrough();
                break;

            case R.id.highlight:
                editor.toggleHighlight();
                break;

            case R.id.fixed_width:
                editor.toggleFixedWidth();
                break;

            case R.id.list:
                editor.toggleList();
                break;
        }
    }

    @Override
    public void onSelectionChanged(final RichTextEditor richTextEditor, int selStart, int selEnd) {
        Log.i(TAG, String.format("Selection start: %d, end: %d", selStart, selEnd));

        formattingToolbar.setOnCheckedChangeListener(null);

        formattingToolbar.checkAll(new HashMap<Integer, Boolean>() {{
            put(R.id.bold, richTextEditor.isBold());
            put(R.id.italic, richTextEditor.isItalic());
            put(R.id.underline, richTextEditor.isUnderline());
            put(R.id.strike, richTextEditor.isStrikeThrough());
            put(R.id.highlight, richTextEditor.isHighlight());
            put(R.id.fixed_width, richTextEditor.isFixedWidth());
            put(R.id.list, richTextEditor.isList());
        }});
        formattingToolbar.setOnCheckedChangeListener(this);

        formattingToolbar.disableAll(new HashMap<Integer, Boolean>() {{
            put(R.id.indent, !richTextEditor.isList());
            put(R.id.outdent, !richTextEditor.isList());
        }});
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
