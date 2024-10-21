package kasuga.lib.core.client.model;

import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.world.item.ItemDisplayContext;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class ItemTransformMapping {
    public static final HashMap<String, ItemDisplayContext> TYPES = new HashMap<>();

    static {
        TYPES.put("none", ItemDisplayContext.NONE);
        TYPES.put("thirdperson_lefthand", ItemDisplayContext.THIRD_PERSON_LEFT_HAND);
        TYPES.put("thirdperson_righthand", ItemDisplayContext.THIRD_PERSON_RIGHT_HAND);
        TYPES.put("firstperson_lefthand", ItemDisplayContext.FIRST_PERSON_LEFT_HAND);
        TYPES.put("firstperson_righthand", ItemDisplayContext.FIRST_PERSON_RIGHT_HAND);
        TYPES.put("head", ItemDisplayContext.HEAD);
        TYPES.put("gui", ItemDisplayContext.GUI);
        TYPES.put("ground", ItemDisplayContext.GROUND);
        TYPES.put("fixed", ItemDisplayContext.FIXED);
    }

    public static @Nullable ItemDisplayContext getType(String string) {
        return TYPES.getOrDefault(string, null);
    }

    public static @Nullable String getName(ItemDisplayContext type) {
        for (Map.Entry<String, ItemDisplayContext> t : TYPES.entrySet()) {
            if (t.getValue() == type) return t.getKey();
        }
        return null;
    }
}
