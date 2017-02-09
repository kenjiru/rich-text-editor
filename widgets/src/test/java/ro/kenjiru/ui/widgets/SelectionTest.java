package ro.kenjiru.ui.widgets;

import org.junit.Test;

import java.util.List;

import ro.kenjiru.ui.widgets.richtexteditor.Selection;

import static org.junit.Assert.*;

public class SelectionTest {
    @Test
    public void extendToFullLines() throws Exception {
        CharSequence str = "Lorem ipsum\n dolor sit amet,\nconsectetur adipiscing elit.";
        Selection selection = new Selection(20, 40);

        Selection fullLinesSelection = selection.extendToFullLines(str);
        assertEquals(12, fullLinesSelection.start);
        assertEquals(57, fullLinesSelection.end);
    }

    @Test
    public void extendToFullLinesCollapsed() throws Exception {
        CharSequence str = "Lorem ipsum\n dolor sit amet,\nconsectetur adipiscing elit.";
        Selection selection = new Selection(5, 5);

        Selection fullLinesSelection = selection.extendToFullLines(str);
        assertEquals(0, fullLinesSelection.start);
        assertEquals(11, fullLinesSelection.end);
    }

    @Test
    public void extendToFullLinesEmptyFirstLine() throws Exception {
        CharSequence str = "\nHellow World!";
        Selection selection = new Selection(0, 0);

        Selection fullLinesSelection = selection.extendToFullLines(str);
        assertEquals(0, fullLinesSelection.start);
        assertEquals(0, fullLinesSelection.end);
    }

    @Test
    public void extendToFullLinesMultipleEmptyLines() throws Exception {
        CharSequence str = "\n\nHellow World!";
        Selection selection = new Selection(1, 1);

        Selection fullLinesSelection = selection.extendToFullLines(str);
        assertEquals(1, fullLinesSelection.start);
        assertEquals(1, fullLinesSelection.end);
    }

    @Test
    public void extendToFullLinesMultipleEmptyLinesMiddle() throws Exception {
        CharSequence str = "Hellow\n\nWorld!";
        Selection selection = new Selection(7, 7);

        Selection fullLinesSelection = selection.extendToFullLines(str);
        assertEquals(7, fullLinesSelection.start);
        assertEquals(7, fullLinesSelection.end);
    }

    @Test
    public void getParagraphsMultiLineSelection() throws Exception {
        CharSequence str = "Lorem ipsum dolor sit amet,\nconsectetur adipiscing elit.\nEtiam justo velit, elementum";
        Selection selection = new Selection(40, 70);

        List<Selection> paragraphs = selection.getParagraphsInSelection(str);
        assertEquals(2, paragraphs.size());
    }

    @Test
    public void getParagraphsCollapsedSelection() throws Exception {
        CharSequence str = "Lorem ipsum dolor sit amet,\nconsectetur adipiscing elit.\nEtiam justo velit, elementum";
        Selection selection = new Selection(5, 5);

        List<Selection> paragraphs = selection.getParagraphsInSelection(str);
        assertEquals(1, paragraphs.size());
    }
}