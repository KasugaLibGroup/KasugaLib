package kasuga.lib.core.client.animation.data.timer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import kasuga.lib.codes.compute.data.Namespace;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@OnlyIn(Dist.CLIENT)
public class TimeLineGroup {
    private final HashMap<String, TimeLine> timeLines;
    private final Namespace namespace;

    public TimeLineGroup(Namespace namespace) {
        this.namespace = namespace;
        this.timeLines = new HashMap<>();
    }

    public void decode(JsonObject object) {
        Set<Map.Entry<String, JsonElement>> entries = object.entrySet();
        for(Map.Entry<String, JsonElement> entry : entries) {
            if(entry.getValue() instanceof JsonObject jsonObject)
                timeLines.put(entry.getKey(), TimeLine.decode(entry.getKey(), namespace, jsonObject));
        }
    }

    public @Nullable TimeLine getTimeLine(String key) {
        return timeLines.getOrDefault(key, null);
    }

    public boolean containsTimeLine(String key) {
        return timeLines.containsKey(key);
    }

    public void action() {
        timeLines.values().forEach(TimeLine::action);
    }

    public void tick() {
        timeLines.values().forEach(TimeLine::actionTicking);
    }

    public void init() {
        timeLines.values().forEach(TimeLine::init);
    }

    public void addTimeLine(TimeLine timeLine) {
        timeLines.put(timeLine.key(), timeLine);
    }

    public Namespace getNamespace() {
        return namespace;
    }
}
