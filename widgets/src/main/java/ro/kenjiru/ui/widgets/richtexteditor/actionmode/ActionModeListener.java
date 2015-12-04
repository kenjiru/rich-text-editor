package ro.kenjiru.ui.widgets.richtexteditor.actionmode;

public interface ActionModeListener {
    boolean doAction(int itemId);
    void setIsShowing(boolean isShowing);
}
