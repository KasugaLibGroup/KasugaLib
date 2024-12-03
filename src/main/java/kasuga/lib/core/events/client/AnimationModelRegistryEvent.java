package kasuga.lib.core.events.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.client.model.BedrockModelLoader;
import kasuga.lib.core.client.model.ModelPreloadManager;
import kasuga.lib.core.client.model.anim_instance.AnimCacheManager;
import kasuga.lib.core.client.model.anim_json.AnimationFile;
import kasuga.lib.core.util.Resources;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.io.IOException;
import java.io.InputStreamReader;

public class AnimationModelRegistryEvent {

    @SubscribeEvent
    public static void registerAnimations(ModelRegistryEvent event) {
        AnimationFile.filesLoaded = true;
        ModelPreloadManager.INSTANCE.scan();
        ModelPreloadManager.INSTANCE.registerPreloadedModel(event);
        ModelPreloadManager.INSTANCE.applyAnimPreload();
        for (ResourceLocation location : AnimationFile.UNREGISTERED) {
            try {
                Resource resource = Resources.getResource(location);
                InputStreamReader reader = new InputStreamReader(resource.getInputStream());
                JsonObject object = JsonParser.parseReader(reader).getAsJsonObject();
                reader.close();
                AnimationFile file = new AnimationFile(location, object);
                AnimationFile.FILES.put(location, file);
            } catch (IOException e) {
                KasugaLib.MAIN_LOGGER.error("Failed to open animation file " + location, e);
            }
        }
        AnimCacheManager.INSTANCE.scanFolder();
        ForgeModelBakery.addSpecialModel(BedrockModelLoader.MISSING_MODEL_LOCATION);
    }
}
