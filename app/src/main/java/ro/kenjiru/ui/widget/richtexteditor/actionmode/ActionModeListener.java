package ro.kenjiru.ui.widget.richtexteditor.actionmode;

public interface ActionModeListener {
    boolean doAction(int itemId);
    void setIsShowing(boolean isShowing);
}
