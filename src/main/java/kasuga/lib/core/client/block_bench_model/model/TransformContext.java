package kasuga.lib.core.client.block_bench_model.model;

import kasuga.lib.core.client.animation.neo_neo.VectorUtil;
import lombok.Getter;
import lombok.Setter;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Getter
@Setter
@OnlyIn(Dist.CLIENT)
public class TransformContext {

    public static final Vector3f ZERO = new Vector3f(0, 0, 0);
    public static final Vector3f BASE_OFFSET = new Vector3f(.5f, 0, .5f);

    private Quaternionf quaternion;
    private Vector3f root;
    private Vector3f absoluteTreeRoot;

    public TransformContext() {
        this.quaternion = new Quaternionf();
        this.root = new Vector3f();
        this.absoluteTreeRoot = new Vector3f();
    }

    public TransformContext(Quaternionf quaternion, Vector3f absoluteTreeRoot,
                            Vector3f root) {
        this.quaternion = quaternion;
        this.root = root;
        this.absoluteTreeRoot = absoluteTreeRoot;
    }

    public Vector3f applyToGroup(Vector3f vector3f) {
        Vector3f result = new Vector3f(vector3f.x(), vector3f.y(), vector3f.z());
        result.sub(root);
        quaternion.transform(result);
        result.rotate(quaternion);
        result.add(absoluteTreeRoot);
        return result;
    }

    public Vector3f applyToElement(Vector3f vector3f) {
        Vector3f result = new Vector3f(vector3f.x(), vector3f.y(), vector3f.z());
        result.rotate(quaternion);
        result.add(absoluteTreeRoot);
        return result;
    }

    public TransformContext transform(Quaternionf quaternion, Vector3f neoRoot) {
        Quaternionf neoQuaternion = new Quaternionf(this.quaternion);
        neoQuaternion.mul(quaternion);
        Vector3f neoTreeRoot = applyToGroup(neoRoot);
        return new TransformContext(neoQuaternion, neoTreeRoot, neoRoot);
    }

    public TransformContext transform(Vector3f rotation, Vector3f neoRoot) {
        Quaternionf transform = VectorUtil.fromXYZDegrees(rotation);
        return transform(transform, neoRoot);
    }
}
