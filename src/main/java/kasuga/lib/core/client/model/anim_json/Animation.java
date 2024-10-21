package kasuga.lib.core.client.model.anim_json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.client.model.anim_instance.AnimCacheManager;
import kasuga.lib.core.client.model.anim_instance.AnimationInstance;
import kasuga.lib.core.client.model.anim_model.AnimModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;

@OnlyIn(Dist.CLIENT)
public class Animation {

    public final String name;
    public final AnimationFile file;
    private final HashMap<String, KeyFrame> frames;
    public final float animationLength;
    private final LoopMode loop;
    public Animation(String name, JsonObject json, AnimationFile file) {
        this.file = file;
        this.frames = new HashMap<>();
        this.name = name;

        if (!json.has("loop"))
            loop = LoopMode.NONE;
        else {
            JsonElement element = json.get("loop");
            if (element.getAsString().equals("hold_on_last_frame"))
                loop = LoopMode.HOLD_ON_LAST_FRAME;
            else if (element.getAsString().equals("true"))
                loop = LoopMode.LOOP;
            else loop = LoopMode.NONE;
        }

        animationLength = json.has("animation_length") ?
                json.get("animation_length").getAsFloat() : 0f;

        if (json.has("bones"))
            deserializeKeyframes(json.getAsJsonObject("bones"));
    }

    private void deserializeKeyframes(JsonObject json) {
        json.entrySet().forEach(
                entry -> {
                    String boneName = entry.getKey();
                    if (!(entry.getValue() instanceof JsonObject object)) return;
                    KeyFrame frame = new KeyFrame(boneName, object);
                    frames.put(boneName, frame);
                }
        );
    }

    public KeyFrame getFrame(String name) {
        return frames.getOrDefault(name, null);
    }

    public boolean hasFrame(String name) {
        return frames.containsKey(name);
    }

    public HashMap<String, KeyFrame> getFrames() {
        return frames;
    }

    public LoopMode getLoop() {
        return loop;
    }

    public float getAnimationLength() {
        return animationLength;
    }

    public String getName() {
        return name;
    }

    public AnimationInstance getInstance(AnimModel model, int frameRate) {
        String pureFileName = AnimCacheManager.getPureFileName(this, model, frameRate);
        if (!AnimCacheManager.systemEnabled()) return new AnimationInstance(this, model, frameRate);
        if (AnimCacheManager.INSTANCE.hasCache(pureFileName)) {
            try {
                ByteArrayInputStream bais = new ByteArrayInputStream(AnimCacheManager.INSTANCE.getCache(pureFileName));
                AnimationInstance fromCache = new AnimationInstance(model, this, bais);
                return fromCache;
            } catch (IOException e) {
                KasugaLib.MAIN_LOGGER.error("InValid file: " + pureFileName, e);
                return buildNewInstance(model, frameRate);
            }
        } else {
            return buildNewInstance(model, frameRate);
        }
    }

    private AnimationInstance buildNewInstance(AnimModel model, int frameRate) {
        AnimationInstance instance = new AnimationInstance(this, model, frameRate);
        try {
            if (AnimCacheManager.systemEnabled()) AnimCacheManager.INSTANCE.save(instance);
        } catch (IOException e) {
            KasugaLib.MAIN_LOGGER.error("Failed to save animation cache file: " + AnimCacheManager.getPureFileName(instance), e);
        }
        return instance;
    }
}
