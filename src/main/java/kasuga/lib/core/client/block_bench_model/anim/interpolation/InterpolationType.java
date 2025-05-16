package kasuga.lib.core.client.block_bench_model.anim.interpolation;

import lombok.Getter;

@Getter
public enum InterpolationType {

    LINEAR(50, new Linear()),
    BEZIER(25, new Bezier()),
    CRS(0, new CatmullRom()),
    STEP(75, new Step());

    private final Interpolation interpolation;
    private final String name;
    private final int priority;

    InterpolationType(int priority, Interpolation interpolation) {
        this.interpolation = interpolation;
        this.name = interpolation.getName();
        this.priority = priority;
    }

    public static InterpolationType get(String name) {
        for (InterpolationType type : InterpolationType.values()) {
            if (type.name.equals(name)) {
                return type;
            }
        }
        return STEP;
    }

    public static InterpolationType getMinPriority(InterpolationType left, InterpolationType right) {
        return left.getPriority() <= right.getPriority() ? left : right;
    }
}
