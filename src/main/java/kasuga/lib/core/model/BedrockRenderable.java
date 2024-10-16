package kasuga.lib.core.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import kasuga.lib.core.client.render.SimpleColor;
import net.minecraft.client.renderer.MultiBufferSource;

import java.util.Map;

public interface BedrockRenderable {
    Map<String, BedrockRenderable> getChildrens();
    BedrockRenderable getChild(String name);
    void applyTranslationAndRotation(PoseStack pose);
    void render(PoseStack pose, VertexConsumer consumer, SimpleColor color, int light, int overlay);
    default Vector3f vonvertPivot(Vector3f myPosition, Vector3f parentPosition) {
        Vector3f vector3f = myPosition.copy();
        vector3f.sub(parentPosition);
        return vector3f;
    }
}
