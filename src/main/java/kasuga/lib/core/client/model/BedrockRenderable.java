package kasuga.lib.core.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import kasuga.lib.core.client.render.SimpleColor;
import org.joml.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public interface BedrockRenderable {
    Map<String, BedrockRenderable> getChildrens();
    BedrockRenderable getChild(String name);
    void applyTranslationAndRotation(PoseStack pose);
    void render(PoseStack pose, VertexConsumer consumer, SimpleColor color, int light, int overlay);
    default Vector3f vonvertPivot(Vector3f myPosition, Vector3f parentPosition) {
        Vector3f vector3f = new Vector3f(myPosition);
        vector3f.sub(parentPosition);
        return vector3f;
    }
}
