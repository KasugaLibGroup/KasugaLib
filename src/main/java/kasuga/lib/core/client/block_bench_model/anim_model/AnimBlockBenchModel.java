package kasuga.lib.core.client.block_bench_model.anim_model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import kasuga.lib.core.client.block_bench_model.anim.instance.AnimationInstance;
import kasuga.lib.core.client.block_bench_model.anim.instance.Ticker;
import kasuga.lib.core.client.block_bench_model.model.BlockBenchElement;
import kasuga.lib.core.client.block_bench_model.model.BlockBenchGroup;
import kasuga.lib.core.client.block_bench_model.model.BlockBenchModel;
import kasuga.lib.core.client.block_bench_model.model.ModelElement;
import kasuga.lib.core.client.render.SimpleColor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class AnimBlockBenchModel implements Renderable {

    @Getter
    private final BlockBenchModel model;

    private final HashMap<UUID, AnimBlockBenchElement> elements;
    private final AnimBlockBenchGroup rootGroup;
    private final HashMap<UUID, AnimElement> bones;

    @Getter
    private final RenderType type;

    @NonNull
    @Setter
    @Getter
    private SimpleColor color;

    public AnimBlockBenchModel(BlockBenchModel model, RenderType type) {
        this.model = model;
        elements = new HashMap<>();
        bones = new HashMap<>();
        rootGroup = new AnimBlockBenchGroup(this);
        compile(this.rootGroup,
                model.getRootGroup().getChildren(),
                this.rootGroup.getChildren());
        this.type = type;
        this.color = SimpleColor.fromRGBAInt(-1);
    }

    public void applyAnimation(Ticker ticker) {
        AnimationInstance animationInstance = ticker.getAnimation();
        animationInstance.getAnimators().forEach(
                (id, animator) -> {
                    if (!bones.containsKey(id)) return;
                    AnimElement element = bones.get(id);
                    if (!(element instanceof AnimBlockBenchGroup group)) return;
                    group.transform = group.transform.merge(
                            animator.getCurrentTransform()
                    );
                }
        );
    }

    public void clearAnimation() {
        bones.forEach(
                (id, element) -> {
                    if (!(element instanceof AnimBlockBenchGroup group)) return;
                    group.transform = new ModelTransform();
                }
        );
    }

    protected void compile(AnimBlockBenchGroup parent,
                           HashMap<UUID, ModelElement> elements,
                           HashMap<UUID, AnimElement> result) {
        for (Map.Entry<UUID, ModelElement> entry : elements.entrySet()) {
            ModelElement element = entry.getValue();
            UUID id = entry.getKey();
            if (element instanceof BlockBenchElement abbe) {
                AnimBlockBenchElement elem = new AnimBlockBenchElement(this, parent, abbe);
                result.put(id, elem);
                this.elements.put(id, elem);
                continue;
            }
            if (element instanceof BlockBenchGroup group) {
                AnimBlockBenchGroup grp = new AnimBlockBenchGroup(this, parent, group);
                result.put(id, grp);
                this.bones.put(id, grp);
                compile(grp, group.getChildren(), grp.getChildren());
            }
        }
    }

    @Override
    public void render(PoseStack pose, VertexConsumer consumer, SimpleColor color, int light, int overlay) {
        pose.pushPose();
        pose.translate(0.5, 0, 0.5);
        rootGroup.render(pose, consumer, color, light, overlay);
        pose.popPose();
    }

    public void render(PoseStack pose, MultiBufferSource buffer, int light, int overlay) {
        render(pose, buffer.getBuffer(getType()), color, light, overlay);
    }
}
