package ro.kenjiru.ui.widgets.richtexteditor.util;

public enum TextSize {
    SMALL(0.8f), NORMAL(1f), BIG(1.5f), HUGE(1.8f);

    private final float ratio;

    TextSize(float ratio) {
        this.ratio = ratio;
    }

    public float getRatio() {
        return ratio;
    }

    private static TextSize[] allValues = values();

    public static TextSize fromOrdinal(int n) {
        return allValues[n];
    }

    public static String[] getValues() {
        String[] strings = new String[TextSize.values().length];
        int i = 0;

        for (TextSize p: TextSize.values()) {
            strings[i++] = p.toString().toLowerCase();
        }

        return strings;
    }
}
