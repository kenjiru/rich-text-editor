package ro.kenjiru.ui.widget.richtexteditor.original;

import ro.kenjiru.ui.widget.richtexteditor.RichTextEditor;

public interface Decoration<T> {
    boolean existsInSelection(RichTextEditor editor);
    T valueInSelection(RichTextEditor editor);
    void applyToSelection(RichTextEditor editor, T add);
}
