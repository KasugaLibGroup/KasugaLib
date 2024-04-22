package kasuga.lib.core.client.gui.components;

import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.HashMap;

public class ComponentRegistry {
    public static HashMap<String,ComponentType<?>> nativeComponents = new HashMap<>();

    public static ComponentType<?> getComponent(String name){
        return nativeComponents.get(name);
    }

    public static <T extends ComponentType<?>> T registerNative(String type, T componentType){
        nativeComponents.put(type,componentType);
        return componentType;
    }
}
