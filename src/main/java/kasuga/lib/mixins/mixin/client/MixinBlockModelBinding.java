package kasuga.lib.mixins.mixin.client;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.client.ModelMappings;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.CrashReport;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ModelBakery.class)
@Deprecated
public abstract class MixinBlockModelBinding {

    @Redirect(method = "loadModel", at = @At(value = "INVOKE", target = "Lnet/minecraft/resources/ResourceLocation;withPrefix(Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;"))
    public ResourceLocation doWithPrefix(ResourceLocation instance, String pPathPrefix) {
        String pNamespace = instance.getNamespace(), pPath = instance.getPath();
        SimpleRegistry registry = KasugaLib.STACKS.getRegistries().getOrDefault(pNamespace, null);
        if(registry == null) return instance.withPrefix(pPathPrefix);
        generateMapping(registry);
        ModelMappings mappings = registry.modelMappings();
        ResourceLocation s2 = new ResourceLocation(pNamespace, pPathPrefix + pPath + ".json");
        if(!mappings.containsMapping(s2)) return instance.withPrefix(pPathPrefix);
        return mappings.getMappings(s2);
    }

    @Redirect(method = "loadModel", at = @At(value = "INVOKE", target = "Lnet/minecraft/resources/FileToIdConverter;idToFile(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/resources/ResourceLocation;"))
    public ResourceLocation doIdToFile(FileToIdConverter instance, ResourceLocation pId) {
        String pNamespace = pId.getNamespace(), pPath = pId.getPath();
        SimpleRegistry registry = KasugaLib.STACKS.getRegistries().getOrDefault(pNamespace, null);
        if(registry == null) return instance.idToFile(pId);
        generateMapping(registry);
        ModelMappings mappings = registry.modelMappings();
        return mappings.getMappings(new ResourceLocation(pNamespace,"blockstates/" + pPath + ".json"));
    }

    @Unique
    private static void generateMapping(SimpleRegistry registry) {
        ModelMappings mappings = registry.modelMappings();
        if (!mappings.isMapFinished()) {
            try {
                mappings.map();
            } catch (Exception e) {
                KasugaLib.MAIN_LOGGER.error("Encountered error while mapping Models!", e);
                Minecraft.crash(CrashReport.forThrowable(e, "Encountered error while mapping Models!"));
            }
        }
    }
}
