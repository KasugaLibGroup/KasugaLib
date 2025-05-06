package kasuga.lib.core.client.block_bench_model.model;

import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransformContext {

    public static final Vector3f ZERO = new Vector3f(0, 0, 0);
    public static final Vector3f BASE_OFFSEt = new Vector3f(.5f, 0, .5f);

    private Quaternion quaternion;
    private Vector3f root;
    private Vector3f absoluteTreeRoot;

    public TransformContext() {
        this.quaternion = new Quaternion(Quaternion.ONE);
        this.root = new Vector3f();
        this.absoluteTreeRoot = new Vector3f();
    }

    public TransformContext(Quaternion quaternion, Vector3f absoluteTreeRoot,
                            Vector3f root) {
        this.quaternion = quaternion;
        this.root = root;
        this.absoluteTreeRoot = absoluteTreeRoot;
    }

    public Vector3f applyToGroup(Vector3f vector3f) {
        Vector3f result = new Vector3f(vector3f.x(), vector3f.y(), vector3f.z());
        result.sub(root);
        result.transform(quaternion);
        result.add(absoluteTreeRoot);
        return result;
    }

    public Vector3f applyToElement(Vector3f vector3f) {
        Vector3f result = new Vector3f(vector3f.x(), vector3f.y(), vector3f.z());
        result.transform(quaternion);
        result.add(absoluteTreeRoot);
        return result;
    }

    public TransformContext transform(Quaternion quaternion, Vector3f neoRoot) {
        Quaternion neoQuaternion = new Quaternion(this.quaternion);
        neoQuaternion.mul(quaternion);
        Vector3f neoTreeRoot = applyToGroup(neoRoot);
        return new TransformContext(neoQuaternion, neoTreeRoot, neoRoot);
    }

    public TransformContext transform(Vector3f rotation, Vector3f neoRoot) {
        Quaternion transform = Quaternion.fromXYZDegrees(rotation);
        return transform(transform, neoRoot);
    }
}
