package kasuga.lib.core.client.model;

import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class ItemTransformMapping {
    public static final HashMap<String, ItemTransforms.TransformType> TYPES = new HashMap<>();

    static {
        TYPES.put("none", ItemTransforms.TransformType.NONE);
        TYPES.put("thirdperson_lefthand", ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND);
        TYPES.put("thirdperson_righthand", ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND);
        TYPES.put("firstperson_lefthand", ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND);
        TYPES.put("firstperson_righthand", ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND);
        TYPES.put("head", ItemTransforms.TransformType.HEAD);
        TYPES.put("gui", ItemTransforms.TransformType.GUI);
        TYPES.put("ground", ItemTransforms.TransformType.GROUND);
        TYPES.put("fixed", ItemTransforms.TransformType.FIXED);
    }

    public static @Nullable ItemTransforms.TransformType getType(String string) {
        return TYPES.getOrDefault(string, null);
    }

    public static @Nullable String getName(ItemTransforms.TransformType type) {
        for (Map.Entry<String, ItemTransforms.TransformType> t : TYPES.entrySet()) {
            if (t.getValue() == type) return t.getKey();
        }
        return null;
    }
}
