package kasuga.lib.core.menu.base;

import kasuga.lib.core.menu.targets.Target;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class GuiBinding {

    UUID localUUID;
    ResourceLocation sourceCodeLocation;

    private static HashMap<ResourceLocation, Target> REGISTRY = new HashMap<>();

    public GuiBinding(UUID id) {
        this.localUUID = id;
    }

    public static void register(ResourceLocation location, Target target){
        GuiBinding.REGISTRY.put(location, target);
    }

    private HashSet<GuiBindingTarget> targets = new HashSet<>();

    public static GuiBinding create(UUID id){
        return new GuiBinding(id);
    }

    public GuiBinding execute(ResourceLocation location){
        sourceCodeLocation = location;
        return this;
    }

    public GuiBinding with(GuiBindingTarget target){
        targets.add(target);
        return this;
    }

    public <U> U apply(GuiBindingTarget<U> binding){
        return DistExecutor.unsafeCallWhenOn(
                Dist.CLIENT,
                ()->()-> BindingClient.applyBinding(binding, localUUID)
        );
    }
}
