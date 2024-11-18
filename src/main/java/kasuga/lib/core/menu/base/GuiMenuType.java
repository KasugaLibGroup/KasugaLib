package kasuga.lib.core.menu.base;

import java.util.function.Function;
import java.util.function.Supplier;

public class GuiMenuType<T extends GuiMenu> {
    private Function<GuiMenuType<?>, T> factory;

    public GuiMenuType(Function<GuiMenuType<?>, T> factory) {
        this.factory = factory;
    }

    public T create() {
        return factory.apply(this);
    }

    public static <T extends GuiMenu> GuiMenuType<T> createType(
            Function<GuiMenuType<?>, T> factory
    ) {
        return new GuiMenuType<>(factory);
    }

    public static <T extends GuiMenu> GuiMenuType<T> createType(
            Supplier<T> factory
    ) {
        return new GuiMenuType<>((t)->factory.get());
    }
}
