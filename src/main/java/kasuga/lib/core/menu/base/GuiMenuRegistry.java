package kasuga.lib.core.menu.base;

import kasuga.lib.core.menu.base.GuiMenu;
import kasuga.lib.core.menu.base.GuiMenuType;
import net.minecraft.resources.ResourceLocation;
import java.util.HashMap;

public class GuiMenuRegistry<T extends GuiMenu> {
    protected HashMap<ResourceLocation, GuiMenuType<? extends T>> REGISTRY = new HashMap<>();
    protected HashMap<GuiMenuType<? extends T>, ResourceLocation> REVERSE_REGISTRY = new HashMap<>();

    public <M extends T> GuiMenuType<M> register(ResourceLocation name, GuiMenuType<M> type) {
        REGISTRY.put(name, type);
        REVERSE_REGISTRY.put(type, name);
        return type;
    }

    public GuiMenuType<? extends T> get(ResourceLocation name) {
        return REGISTRY.get(name);
    }

    public ResourceLocation getName(GuiMenuType<? extends T> type) {
        return REVERSE_REGISTRY.get(type);
    }

    public T create(ResourceLocation name) {
        GuiMenuType<? extends T> type = get(name);
        if (type == null) {
            throw new IllegalArgumentException("Unknown GuiMenu type: " + name);
        }
        return type.create();
    }
} 