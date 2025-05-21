package kasuga.lib.core.client.block_bench_model.anim_model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import kasuga.lib.core.client.block_bench_model.model.BlockBenchGroup;
import kasuga.lib.core.client.render.SimpleColor;
import lombok.Getter;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
@Getter
public class AnimBlockBenchGroup extends AnimElement implements ElementCollection, Renderable {

    @Nullable
    private final BlockBenchGroup group;

    private final HashMap<UUID, AnimElement> children;

    public AnimBlockBenchGroup(AnimBlockBenchModel model,
                               AnimBlockBenchGroup parent,
                               BlockBenchGroup group) {
        super(model, parent, group.getId());
        this.group = group;
        this.children = new HashMap<>();
        super.pivot = group.getPivot();
    }

    protected AnimBlockBenchGroup(AnimBlockBenchModel model) {
        super(model, null, UUID.randomUUID());
        this.group = null;
        this.children = new HashMap<>();
        super.pivot = new Vector3f();
    }

    @Override
    public HashMap<UUID, AnimElement> getChildren() {
        return children;
    }

    @Override
    public void render(PoseStack pose, VertexConsumer consumer, SimpleColor color, int light, int overlay) {
        if (group != null && !group.isRender()) return;
        pose.pushPose();
        if (group != null) {
            translateToPivot(pose);
        }
        pose.translate(transform.getOffset().x(), transform.getOffset().y(), transform.getOffset().z());
        if (group != null) {
            baseRotation(pose, this.group.getRotation());
        }
        // transform.transform(pose);
        pose.mulPose(transform.getQuaternion());
        pose.scale(transform.getScale().x(), transform.getScale().y(), transform.getScale().z());
        children.forEach((id, element) -> {
            ((Renderable) element).render(pose, consumer, color, light, overlay);
        });
        pose.popPose();
    }
}
