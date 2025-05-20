package kasuga.lib.core.client.block_bench_model.anim;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import kasuga.lib.core.client.block_bench_model.anim.interpolation.InterpolationType;
import kasuga.lib.core.util.data_type.Pair;
import lombok.Getter;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
@Getter
public class Animator {

    private final Animation animation;
    private final UUID id;
    private final String name;
    private final String type;

    private final HashMap<Channel, ArrayList<KeyFrame>> keyFrames;
    private final HashMap<Channel, ArrayList<Pair<Float, Vector3f>>> cachedFrames;

    public Animator(Animation animation, UUID id, String name, String type) {
        this.animation = animation;
        this.id = id;
        this.name = name;
        this.type = type;
        this.keyFrames = new HashMap<>();
        cachedFrames = new HashMap<>();
    }

    public void compileKeyFrames(JsonArray keyFrameArray) {
        for (JsonElement keyFrameEle : keyFrameArray) {
            JsonObject keyFrameObj = keyFrameEle.getAsJsonObject();
            Channel channel = Channel.get(keyFrameObj.get("channel").getAsString());
            UUID id = UUID.fromString(keyFrameObj.get("uuid").getAsString());
            float time = keyFrameObj.get("time").getAsFloat();
            InterpolationType interpolation = InterpolationType.get(keyFrameObj.get("interpolation").getAsString());
            KeyFrame keyFrame = new KeyFrame(this.animation, this,
                    channel, id, interpolation, time);
            keyFrame.compileDataPoints(keyFrameObj.get("data_points").getAsJsonArray());
            keyFrame.compileBezierPoints(keyFrameObj);
            keyFrame.setBezierLinked(
                    keyFrameObj.has("bezier_linked") &&
                    keyFrameObj.get("bezier_linked").getAsBoolean()
            );
            if (!this.keyFrames.containsKey(channel)) {
                ArrayList<KeyFrame> keyFrames = new ArrayList<>();
                this.keyFrames.put(channel, keyFrames);
                keyFrames.add(keyFrame);
            } else {
                ArrayList<KeyFrame> frames = this.keyFrames.get(channel);
                if (frames.size() == 1) {
                    KeyFrame f = frames.get(0);
                    frames.add(keyFrame.getTime() >= f.getTime() ? 1 : 0, keyFrame);
                } else {
                    int index = -1;
                    for (int i = 0; i < frames.size() - 1; i++) {
                        KeyFrame frame = frames.get(i);
                        KeyFrame nextFrame = frames.get(i + 1);
                        if (frame.getTime() <= time &&
                                nextFrame.getTime() >= time) {
                            index = i;
                            break;
                        }
                    }
                    if (index < 0) {
                        frames.add(keyFrame);
                    } else {
                        frames.add(index, keyFrame);
                    }
                }
            }
        }
    }
}
