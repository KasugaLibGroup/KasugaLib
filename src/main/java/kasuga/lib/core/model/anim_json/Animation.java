package kasuga.lib.core.model.anim_json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;

public class Animation {

    public final String name;
    private final HashMap<String, KeyFrame> frames;
    public final float animationLength;
    private final LoopMode loop;
    public Animation(String name, JsonObject json) {
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
}
