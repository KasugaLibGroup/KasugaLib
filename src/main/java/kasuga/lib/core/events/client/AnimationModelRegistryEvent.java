package kasuga.lib.core.events.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.model.BedrockModelLoader;
import kasuga.lib.core.model.UnbakedBedrockModel;
import kasuga.lib.core.model.anim_json.AnimationFile;
import kasuga.lib.core.util.Resources;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.io.IOException;

public class AnimationModelRegistryEvent {

    @SubscribeEvent
    public static void registerAnimations(ModelEvent.RegisterAdditional event) {
        AnimationFile.filesLoaded = true;
        for (ResourceLocation location : AnimationFile.UNREGISTERED) {
            try {
                Resource resource = Resources.getResource(location);
                JsonObject object = JsonParser.parseReader(resource.openAsReader()).getAsJsonObject();
                AnimationFile file = new AnimationFile(object);
                AnimationFile.FILES.put(location, file);
            } catch (IOException e) {
                KasugaLib.MAIN_LOGGER.error("Failed to open animation file " + location, e);
            }
        }
    }

    @SubscribeEvent
    public static void registerBedrockModels(ModelEvent.RegisterAdditional event) {
        BedrockModelLoader.registerFired = true;
        event.register(BedrockModelLoader.MISSING_MODEL_LOCATION);
        for (ResourceLocation location : BedrockModelLoader.UNREGISTERED)
            event.register(location);
    }
}
