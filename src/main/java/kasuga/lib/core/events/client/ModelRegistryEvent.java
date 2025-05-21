package kasuga.lib.core.events.client;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.client.render.model.MultiPartModel;
import kasuga.lib.core.client.render.model.SimpleModel;
import kasuga.lib.core.resource.CustomResourceReloadListener;
import kasuga.lib.example_env.AllExampleElements;
import kasuga.lib.core.client.render.texture.StaticImage;
import kasuga.lib.core.client.render.texture.StaticImageHolder;
import kasuga.lib.registrations.registry.SimpleRegistry;
import kasuga.lib.registrations.client.ModelReg;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.io.IOException;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class ModelRegistryEvent {

    @SubscribeEvent
    public static void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
        for(String key : KasugaLib.STACKS.getRegistries().keySet()) {
            SimpleRegistry registry = KasugaLib.STACKS.getRegistries().get(key);
            for(ResourceLocation location : registry.model().UNBAKED.keySet()) {
                ModelReg reg = registry.model().UNBAKED.get(location);
                try {
                    reg.compileFile(registry);
                    if(reg.isMultiPart()) {
                        reg.rebuildAsMultiPart();
                        for(ResourceLocation locationx : reg.getMappings().values()) {
                            event.register(locationx);
                        }
                    } else {
                        event.register(location);
                    }
                } catch (IOException e) {
                    registry.logger().error("Encounter critical failure in loading model : " + reg.registrationKey, e);
                }
            }
        }
    }

    @SubscribeEvent
    public static void bakingCompleted(ModelEvent.BakingCompleted event) {
        Map<ResourceLocation, BakedModel> models = event.getModels();
        for(String key : KasugaLib.STACKS.getRegistries().keySet()) {
            SimpleRegistry registry = KasugaLib.STACKS.getRegistries().get(key);
            for(ModelReg reg : registry.model().UNBAKED.values()) {
                if(reg.isMultiPart()) {
                    MultiPartModel multi = new MultiPartModel(reg.registrationKey);
                    for(String k : reg.getMappings().keySet()) {
                        ResourceLocation location = reg.getMappings().get(k);
                        if(models.containsKey(location)) {
                            String name = k.lastIndexOf(".") == -1 ? k : k.substring(k.lastIndexOf(".") + 1);
                            multi.addBoneByPath(k, new SimpleModel(name, models.get(location)));
                        }
                    }
                    reg.putModelIn(multi);
                } else {
                    if (models.containsKey(reg.location()))
                        reg.putModelIn(new SimpleModel(reg.registrationKey, models.get(reg.location())));
                }
            }
            registry.onCustomItemRendererReg(models);
            registry.model().clearUnbaked();
            registry.onBlockRendererReg();
            registry.onEntityRendererReg();
            registry.onAnimationReg();
            registry.onBlockRendererReg();
        }
    }

    @SubscribeEvent
    public static void registerStaticImages(ModelEvent.RegisterAdditional event) {
        StaticImage.HOLDERS.forEach(holder -> {
            try {
                holder.getImage();
            } catch (IOException e) {
                KasugaLib.MAIN_LOGGER.error("Error in loading textures: ", e);
            }
        });
    }

    @SubscribeEvent
    public static void beforeTextureStitch(TextureStitchEvent.Pre event) {
        // CustomResourceReloadListener.INSTANCE.applyOnStitch();
    }
}
