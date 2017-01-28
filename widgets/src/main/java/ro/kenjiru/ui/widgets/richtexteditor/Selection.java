package ro.kenjiru.ui.widgets.richtexteditor;

import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class Selection {
    public int start;
    public int end;

    public Selection(int start, int end) {
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

    public List<Selection> getParagraphsInSelection(CharSequence str) {
        List<Selection> paragraphs = new ArrayList<>();

        Selection wholeLinesSelection = this.extendToFullLines(str);
        int startOfParagraph = wholeLinesSelection.start;
        int endOfParagraph = wholeLinesSelection.start;

        while(endOfParagraph < str.length()) {
            if (str.charAt(endOfParagraph) == '\n' || endOfParagraph == wholeLinesSelection.end) {
                paragraphs.add(new Selection(startOfParagraph, endOfParagraph));

                if (endOfParagraph < wholeLinesSelection.end) {
                    startOfParagraph = endOfParagraph + 1;
                } else {
                    break;
                }
            }
            ++endOfParagraph;
        }

        return paragraphs;
    }

    public Selection extendToFullLines(CharSequence str) {
        int startOfFirstParagraph = this.start;
        while (startOfFirstParagraph > 0 && str.charAt(startOfFirstParagraph - 1) != '\n') {
            --startOfFirstParagraph;
        }

        int endOfLastParagraph = this.end;
        while(endOfLastParagraph < str.length() - 1 && str.charAt(endOfLastParagraph) != '\n') {
            ++endOfLastParagraph;
        }

        return new Selection(startOfFirstParagraph, endOfLastParagraph);
    }

    public boolean isCollapsed() {
        return (start == end);
    }

    public void apply(EditText editor) {
        editor.setSelection(start, end);
    }

    public String toString() {
        return "start: " + start + ", end: " + end;
    }
}