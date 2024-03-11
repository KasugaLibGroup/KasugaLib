package kasuga.lib.core.client.animation.infrastructure;

import java.util.function.Predicate;

public abstract class AnimationElement {
    private final String key;
    public AnimationElement(String key) {
        this.key = key;
    }

    public String key(){return key;}
    public abstract boolean isAssignable();
    public abstract boolean isValid();
    public abstract void assign(String codec, float value);
    public abstract void init();
}
