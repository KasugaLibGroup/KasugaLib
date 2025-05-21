package kasuga.lib.core.client.block_bench_model.anim_model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@OnlyIn(Dist.CLIENT)
@Getter
public abstract class AnimElement {
    
    @NotNull
    protected final UUID id;

    @Nullable
    protected final AnimElement parent;

    @Setter
    protected ModelTransform transform;

    protected Vector3f pivot;

    protected final AnimBlockBenchModel model;
    
    public AnimElement(AnimBlockBenchModel model,
                       @Nullable AnimElement parent,
                       @NotNull UUID id) {
        this.id = id;
        this.parent = parent;
        transform = new ModelTransform();
        pivot = new Vector3f();
        this.model = model;
    }

    public void translateToPivot(PoseStack pose) {
        AnimElement parent = getParent();
        if (parent != null) {
            Vector3f pivotOffset = new Vector3f();
            pivotOffset.add(this.pivot);
            pivotOffset.sub(parent.pivot);
            pose.translate(
                    pivotOffset.x() / 16f,
                    pivotOffset.y() / 16f,
                    pivotOffset.z() / 16f
            );
        }
    }

    public void baseRotation(PoseStack pose, Vector3f rotation) {
        Quaternion quaternion = Quaternion.fromXYZDegrees(rotation);
        pose.mulPose(quaternion);
    }

    public RenderType getRenderType() {
        return model.getType();
    }
}
