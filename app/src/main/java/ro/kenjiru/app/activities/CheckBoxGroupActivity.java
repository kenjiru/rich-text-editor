package ro.kenjiru.app.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.HashMap;
import java.util.Map;

import ro.kenjiru.ui.widgets.CheckBoxGroup;
import ro.kenjiru.ui.widget.richtexteditor.R;

public class CheckBoxGroupActivity extends AppCompatActivity {
    private static final String TAG = "CheckBoxGroupActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkbox_group);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        CheckBoxGroup checkBoxGroup = (CheckBoxGroup) findViewById(R.id.checkbox_group);

        checkBoxGroup.setOnCheckedChangeListener(new CheckBoxGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CheckBoxGroup group, int checkBoxId, boolean checked) {
                Log.i(TAG, "Button checked");
            }
        });

        // test the bulk check
        Map<Integer, Boolean> state = new HashMap<Integer, Boolean>();
        state.put(R.id.indent, true);
        state.put(R.id.outdent, true);
        checkBoxGroup.checkAll(state);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.checkbox_group_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
