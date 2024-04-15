package kasuga.lib.mixins.mixin.client;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.client.ModelMappings;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.CrashReport;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ModelBakery.class)
public abstract class MixinBlockModelBinding {

    @Redirect(method = "loadModel", at = @At(value = "NEW", target = "(Ljava/lang/String;Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;"))
    public ResourceLocation resourceLocation(String pNamespace, String pPath) {
        KasugaLib.MAIN_LOGGER.error("logging bakery");
        SimpleRegistry registry = KasugaLib.STACKS.getRegistries().getOrDefault(pNamespace, null);
        if(registry == null) return new ResourceLocation(pNamespace, pPath);
        ModelMappings mappings = registry.modelMappings();
        if(!mappings.isMapFinished()) {
            try {
                mappings.map();
            } catch (Exception e) {
                KasugaLib.MAIN_LOGGER.error("Encountered error while mapping Models!", e);
                Minecraft.crash(CrashReport.forThrowable(e, "Encountered error while mapping Models!"));
            }
        }
        if(pPath.startsWith("blockstates"))
            return mappings.getMappings(new ResourceLocation(pNamespace, pPath));
        ResourceLocation s2 = new ResourceLocation(pNamespace, pPath + ".json");
        if(!mappings.containsMapping(s2)) return new ResourceLocation(pNamespace, pPath);
        return mappings.getMappings(s2);
    }
}
