package ro.kenjiru.richtexteditor.original;

import ro.kenjiru.richtexteditor.RichTextEditor;

public interface Decoration<T> {
    boolean existsInSelection(RichTextEditor editor);
    T valueInSelection(RichTextEditor editor);
    void applyToSelection(RichTextEditor editor, T add);
}
