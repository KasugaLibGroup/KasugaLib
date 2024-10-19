package kasuga.lib.core.menu;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.client.frontend.common.event.Event;
import kasuga.lib.core.client.frontend.gui.GuiInstance;
import kasuga.lib.core.util.Envs;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public class BindingClient {
    public static <U> U applyBinding(GuiBindingTarget<U> binding, UUID targetUUID){
        if(Envs.isClient()){
            Optional<GuiInstance> instance = KasugaLib.STACKS.GUI.orElseThrow().getInstanceById(targetUUID);
            if(instance.isPresent()){
                return BindingClient.getBindingTarget(binding).apply(instance.get());
            }
        }
        return null;
    }

    public static <U> Function<GuiInstance, U> getBindingTarget(GuiBindingTarget<U> binding) {
        return (Function<GuiInstance, U>) bindings.get(binding);
    }

    public static HashMap<GuiBindingTarget<?>, Function<GuiInstance, ?>> bindings = new HashMap<>();

    public static <U> void registerBinding(GuiBindingTarget<U> binding, Function<GuiInstance, U> executor){
        bindings.put(binding, executor);
    }

    public static void createInstance(UUID id, ResourceLocation location){
        KasugaLib.STACKS.GUI.orElseThrow().create(id, location);
    }

    public static void dispatchGuiEvent(UUID id, Event event){
        KasugaLib.STACKS.GUI.orElseThrow().getInstanceById(id).ifPresent((instance)->{
            instance.getContext().ifPresent((c)->{
                c.appendTask(()->c.dispatchEvent(event.getType(), event));
            });
        });
    }
}
