package kasuga.lib.core.menu.base;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class GuiMenuType<T extends GuiMenu> {
    private Function<GuiBinding, T> factory;
    private final Supplier<GuiBinding> bindingSupplier;

    public GuiMenuType(Function<GuiBinding, T> factory, Supplier<GuiBinding> bindingSupplier) {
        this.factory = factory;
        this.bindingSupplier = bindingSupplier;
    }

    public T create() {
        return factory.apply(bindingSupplier.get());
    }

    protected static <T extends GuiMenu> GuiMenuType<T> createType(
            Function<GuiBinding, T> factory,
            Supplier<GuiBinding> bindingSupplier
    ) {
        return new GuiMenuType<>(factory, bindingSupplier);
    }

    protected static <T extends GuiMenu> GuiMenuType<T> createType(
            BiFunction<GuiMenuType<T>, GuiBinding, T> factory,
            Supplier<GuiBinding> bindingSupplier
    ) {
        GuiMenuType<T> type = new GuiMenuType<>(null, bindingSupplier);
        type.factory = binding -> factory.apply(type, binding);
        return type;
    }
}
