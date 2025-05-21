package kasuga.lib.core.client.block_bench_model.anim_model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import kasuga.lib.core.client.block_bench_model.anim.Channel;
import kasuga.lib.core.client.block_bench_model.model.TransformContext;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModelTransform {

    private Vector3f offset, rotation, scale;

    public ModelTransform() {
        offset = new Vector3f();
        rotation = new Vector3f();
        scale = new Vector3f(1, 1, 1);
    }

    public ModelTransform(final Vector3f offset, final Vector3f rotation, final Vector3f scale) {
        this.offset = offset;
        this.rotation = rotation;
        this.scale = scale;
    }

    public Quaternion getQuaternion() {
        Quaternion quaternion = Quaternion.ONE.copy();
        quaternion.mul(Vector3f.ZP.rotationDegrees(rotation.z()));
        quaternion.mul(Vector3f.YP.rotationDegrees(rotation.y()));
        quaternion.mul(Vector3f.XN.rotationDegrees(rotation.x()));
        return quaternion;
        // return Quaternion.fromXYZDegrees(rotation);
    }

    public void clear() {
        offset = new Vector3f();
        rotation = new Vector3f();
        scale = new Vector3f(1, 1, 1);
    }

    public ModelTransform merge(ModelTransform other) {
        ModelTransform result = new ModelTransform();
        result.offset.add(this.offset);
        result.offset.add(other.offset);
        result.rotation.add(this.rotation);
        result.rotation.add(other.rotation);
        result.scale.mul(this.scale.x(), this.scale.y(), this.scale.z());
        result.scale.mul(other.scale.x(), other.scale.y(), other.scale.z());
        return result;
    }

    public void transform(PoseStack pose) {
        pose.translate(offset.x(), offset.y(), offset.z());

        if (!rotation.equals(TransformContext.ZERO)) {
            Quaternion quaternion = getQuaternion();
            pose.mulPose(quaternion);
        }
        if (!scale.equals(new Vector3f(1, 1, 1))) {
            pose.scale(scale.x(), scale.y(), scale.z());
        }
    }

    public void setValue(Channel channel, Vector3f data) {
        switch (channel) {
            case SCALE -> this.scale = data;
            case ROTATION -> this.rotation = data;
            case POSITION -> this.offset = data;
        }
    }
}
