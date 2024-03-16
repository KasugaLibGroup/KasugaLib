package kasuga.lib.core.client.animation.data.trigger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import interpreter.compute.data.Namespace;
import kasuga.lib.core.client.animation.data.timer.TimeLine;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@OnlyIn(Dist.CLIENT)
public class TriggerGroup {
    private final HashMap<String, Trigger> triggers;
    private final Namespace namespace;
    public TriggerGroup(Namespace namespace) {
        this.namespace = namespace;
        this.triggers = new HashMap<>();
    }

    public void decode(JsonObject object) {
        Set<Map.Entry<String, JsonElement>> entries = object.entrySet();
        for(Map.Entry<String, JsonElement> entry : entries) {
            if(entry.getValue() instanceof JsonObject jsonObject)
                triggers.put(entry.getKey(), Trigger.decode(entry.getKey(), namespace, jsonObject));
        }
    }

    public void init() {
        triggers.values().forEach(Trigger::init);
    }

    public void action() {
        for (Trigger trigger : triggers.values()) {
            trigger.action();
        }
    }

    public Namespace getNamespace() {
        return namespace;
    }

    public HashMap<String, Trigger> getTriggers() {
        return triggers;
    }
}
