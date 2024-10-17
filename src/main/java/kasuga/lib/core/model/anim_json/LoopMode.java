package kasuga.lib.core.model.anim_json;

public enum LoopMode {
    NONE(1),
    HOLD_ON_LAST_FRAME(2),
    LOOP(3);

    public final int index;
    LoopMode(int index){
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public static LoopMode fromIndex(int index) {
        return switch (index) {
            case 2 -> HOLD_ON_LAST_FRAME;
            case 3 -> LOOP;
            default -> NONE;
        };
    }
}
