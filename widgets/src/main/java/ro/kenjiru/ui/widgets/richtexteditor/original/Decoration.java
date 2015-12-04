package ro.kenjiru.ui.widgets.richtexteditor.original;

import ro.kenjiru.ui.widgets.richtexteditor.RichTextEditor;

public interface Decoration<T> {
    boolean existsInSelection(RichTextEditor editor);
    T valueInSelection(RichTextEditor editor);
    void applyToSelection(RichTextEditor editor, T add);
}
