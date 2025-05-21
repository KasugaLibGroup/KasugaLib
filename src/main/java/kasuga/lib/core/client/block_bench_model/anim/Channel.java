package kasuga.lib.core.client.block_bench_model.anim;

import org.jetbrains.annotations.Nullable;

public enum Channel {

    ROTATION("rotation"),
    POSITION("position"),
    SCALE("scale");

    public final String name;

    Channel(String name) {
        this.name = name;
    }

    public static @Nullable Channel get(String name) {
        for (Channel c : Channel.values()) {
            if (c.name.equals(name))
                return c;
        }
        return null;
    }
}
