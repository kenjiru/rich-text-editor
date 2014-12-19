package ro.kenjiru.richtexteditor;

import android.widget.EditText;

public class Selection {
    public int start;
    public int end;

    Selection(int start, int end) {
        this.start = start;
        this.end = end;

        if (this.start > this.end) {
            int temp = this.end;

            this.end = this.start;
            this.start = temp;
        }
    }

    public Selection(EditText editor) {
        this(editor.getSelectionStart(), editor.getSelectionEnd());
    }

    public boolean isCollapsed() {
        return (start == end);
    }

    public void apply(EditText editor) {
        editor.setSelection(start, end);
    }
}