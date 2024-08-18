package kasuga.lib.core.client.frontend.gui.layout.yoga.api;

public enum YogaMeasureMode {
    UNDEFINED(0),
    EXACTLY(1),
    AT_MOST(2);

    private final int mIntValue;

    YogaMeasureMode(int intValue) {
        mIntValue = intValue;
    }

    public int getValue() {
        return mIntValue;
    }

    public static YogaMeasureMode fromInt(int value) {
        switch (value) {
            case 0: return UNDEFINED;
            case 1: return EXACTLY;
            case 2: return AT_MOST;
            default: throw new IllegalArgumentException("Unknown enum value: " + value);
        }
    }
}