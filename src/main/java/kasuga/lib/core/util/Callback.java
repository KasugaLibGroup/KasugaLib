package kasuga.lib.core.util;

@FunctionalInterface
public interface Callback {
    static Callback nop() {
        return ()->{};
    }

    void execute();
}
