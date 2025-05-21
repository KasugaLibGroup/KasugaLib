package kasuga.lib.core.client.block_bench_model.anim;

public enum LoopMode {

    ONCE("once", 0, false, false),
    HOLD("hold", 1, true, false),
    LOOP("loop", 2, false, true);

    public final String name;
    public final int index;
    public final boolean stayAtLast, continueAnimate;

    LoopMode(String name, int index, boolean stayAtLast, boolean continueAnimate) {
        this.name = name;
        this.index = index;
        this.stayAtLast = stayAtLast;
        this.continueAnimate = continueAnimate;
    }

    public static LoopMode get(String name) {
        for (LoopMode loopMode : LoopMode.values()) {
            if (loopMode.name.equals(name)) {
                return loopMode;
            }
        }
        return ONCE;
    }
}
