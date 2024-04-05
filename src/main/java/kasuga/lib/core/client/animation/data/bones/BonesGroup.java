package kasuga.lib.core.client.animation.data.bones;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import interpreter.compute.data.Namespace;
import kasuga.lib.core.client.animation.data.Animation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.*;


@OnlyIn(Dist.CLIENT)
public class BonesGroup {
    private final HashMap<String, BoneMovement> movements;
    private final Namespace namespace;
    private final Animation animation;

    public BonesGroup(Animation animation, Namespace namespace) {
        movements = new HashMap<>();
        this.animation = animation;
        this.namespace = namespace;
    }

    public void decodeBones(JsonObject bones) {
        Set<Map.Entry<String, JsonElement>> entries = bones.entrySet();
        for(Map.Entry<String, JsonElement> entry : entries) {
            if(entry.getValue() instanceof JsonObject jsonObject) {
                movements.put(entry.getKey(), BoneMovement.decode(entry.getKey(), namespace, animation, jsonObject));
            }
        }
    }

    public void init() {
        movements.values().forEach(BoneMovement::invoke);
        movements.values().forEach(BoneMovement::init);
    }

    public @Nullable BoneMovement getMovement(String key) {
        return this.movements.getOrDefault(key, null);
    }

    public HashMap<String, BoneMovement> getMovements() {
        return this.movements;
    }

    public int movementCount() {
        return movements.size();
    }

    public boolean containsMovements(String key) {
        return movements.containsKey(key);
    }

    public Namespace getNamespace() {
        return namespace;
    }

    public void action(String boneName, PoseStack stack) {
        if (!containsMovements(boneName)) return;
        getMovement(boneName).action(stack);
    }

    public boolean shouldRender(String boneName) {
        if(!containsMovements(boneName)) return false;
        return getMovement(boneName).shouldDisplay();
    }
}
