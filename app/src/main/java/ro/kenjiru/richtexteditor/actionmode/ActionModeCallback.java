package ro.kenjiru.richtexteditor.actionmode;

import android.app.Activity;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.HashMap;

import ro.kenjiru.richtexteditor.RichTextEditor;
import ro.kenjiru.richtexteditor.Selection;

public class ActionModeCallback implements ActionMode.Callback {
    Activity host = null;
    int menuResource = 0;
    RichTextEditor editor = null;
    Selection selection = null;
    ActionModeListener listener = null;
    HashMap<Integer, ActionModeCallback> chains = new HashMap<Integer, ActionModeCallback>();

    public ActionModeCallback(Activity host, int menuResource, RichTextEditor editor,
                              ActionModeListener listener) {
        this.menuResource = menuResource;
        this.editor = editor;
        this.listener = listener;
        this.host = host;
    }

    public void setSelection(Selection selection) {
        this.selection = selection;
    }

    public void addChain(int menuItemId, ActionModeCallback toChainTo) {
        chains.put(menuItemId, toChainTo);
    }


    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();

        MenuItem item = menu.findItem(android.R.id.selectAll);

        if (item != null) {
            item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM
                    | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        }

        inflater.inflate(menuResource, menu);
        listener.setIsShowing(true);

        return (true);
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        if (selection != null) {
            selection.apply(editor);
        }

        return (false);
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        listener.setIsShowing(false);
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        ActionModeCallback next = chains.get(item.getItemId());

        if (next != null) {
            next.setSelection(new Selection(editor));
            host.startActionMode(next);
            mode.finish();

            return (true);
        }
        //
        // mode.finish();
        //
        return (listener.doAction(item.getItemId()));
    }
}