package kasuga.lib.core.client.render.texture;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import kasuga.lib.core.annos.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.nbt.CompoundTag;

/**
 * Use this to do operations for your image.
 * Nine Sliced means your image would be cut into shapes like
 * symbol "#", and would be rendered separately.
 */
public class NineSlicedImageMask extends ImageMask {
    float left, right, top, bottom, scalingFactor = 1f;
    private final Matrix<Vec2f> matrix;
    private final Matrix<Vector3f> positionMatrix;

    public NineSlicedImageMask(StaticImage image) {
        super(image);
        matrix = new Matrix<>(4, 4);
        positionMatrix = new Matrix<>(4, 4);
        updateMatrix();
    }

    public NineSlicedImageMask(NineSlicedImageMask mask) {
        super(mask);
        this.left = mask.left;
        this.right = mask.right;
        this.top = mask.top;
        this.bottom = mask.bottom;
        matrix = new Matrix<>(4, 4);
        positionMatrix = new Matrix<>(4, 4);
        updateMatrix();
    }

    public NineSlicedImageMask(String name, CompoundTag nbt) {
        super(name, nbt);
        CompoundTag tag = nbt.getCompound(name);
        this.left = tag.getFloat("left");
        this.right = tag.getFloat("right");
        this.top = tag.getFloat("top");
        this.bottom = tag.getFloat("bottom");
        matrix = new Matrix<>(4, 4);
        positionMatrix = new Matrix<>(4, 4);
        updateMatrix();
    }

    @Override
    public void serialize(String name, CompoundTag nbt) {
        super.serialize(name, nbt);
        CompoundTag tag = nbt.getCompound(name);
        tag.putFloat("left", left);
        tag.putFloat("right", right);
        tag.putFloat("top", top);
        tag.putFloat("bottom", bottom);
    }

    public void initialize() {
        super.initialize();
        this.left = .1f;
        this.right = .9f;
        this.top = .1f;
        this.bottom = .9f;
    }

    public void setScalingFactor(float factor) {
        this.scalingFactor = factor;
    }

    public float getScalingFactor() {
        return scalingFactor;
    }

    public void updateMatrix() {
        matrix.set(1, 1, getUvLeftTop());
        matrix.set(1, 4, getUvRightTop());
        matrix.set(4, 1, getUvLeftDown());
        matrix.set(4, 4, getUvRightDown());

        matrix.set(1, 2, Vec2f.sampling(getUvLeftTop(), getUvRightTop(), left));
        matrix.set(1, 3, Vec2f.sampling(getUvLeftTop(), getUvRightTop(), right));
        matrix.set(2, 1, Vec2f.sampling(getUvLeftTop(), getUvLeftDown(), top));
        matrix.set(3, 1, Vec2f.sampling(getUvLeftTop(), getUvLeftDown(), bottom));

        matrix.set(4, 2, Vec2f.sampling(getUvLeftDown(), getUvRightDown(), left));
        matrix.set(4, 3, Vec2f.sampling(getUvLeftDown(), getUvRightDown(), right));
        matrix.set(2, 4, Vec2f.sampling(getUvRightTop(), getUvRightDown(), top));
        matrix.set(3, 4, Vec2f.sampling(getUvRightTop(), getUvRightDown(), bottom));

        matrix.set(2, 2, Vec2f.intersection(matrix.get(2, 1), matrix.get(2, 4), matrix.get(1, 2), matrix.get(4, 2)));
        matrix.set(2, 3, Vec2f.intersection(matrix.get(2, 1), matrix.get(2, 4), matrix.get(1, 3), matrix.get(4, 3)));
        matrix.set(3, 2, Vec2f.intersection(matrix.get(3, 1), matrix.get(3, 4), matrix.get(1, 2), matrix.get(4, 2)));
        matrix.set(3, 3, Vec2f.intersection(matrix.get(3, 1), matrix.get(3, 4), matrix.get(1, 3), matrix.get(4, 3)));

        updatePositionMatrix();
    }

    public void updatePositionMatrix() {
        positionMatrix.set(1, 1, getLeftTop());
        positionMatrix.set(1, 4, getRightTop());
        positionMatrix.set(4, 1, getLeftDown());
        positionMatrix.set(4, 4, getRightDown());

        positionMatrix.set(1, 2, cutVector3f(getLeftTop(), getRightTop(), matrix.get(1, 1).distance(matrix.get(1, 2)) * scalingFactor * (float) getImage().width()));
        positionMatrix.set(1, 3, cutVector3f(getRightTop(), getLeftTop(), matrix.get(1, 3).distance(matrix.get(1, 4)) * scalingFactor * (float) getImage().width()));
        positionMatrix.set(2, 1, cutVector3f(getLeftTop(), getLeftDown(), matrix.get(1, 1).distance(matrix.get(2, 1)) * scalingFactor * (float) getImage().height()));
        positionMatrix.set(3, 1, cutVector3f(getLeftDown(), getLeftTop(), matrix.get(3, 1).distance(matrix.get(4, 1)) * scalingFactor * (float) getImage().height()));

        positionMatrix.set(4, 2, cutVector3f(getLeftDown(), getRightDown(), matrix.get(4, 1).distance(matrix.get(4, 2)) * scalingFactor * (float) getImage().width()));
        positionMatrix.set(4, 3, cutVector3f(getRightDown(), getLeftDown(), matrix.get(4, 3).distance(matrix.get(4, 4)) * scalingFactor * (float) getImage().width()));
        positionMatrix.set(2, 4, cutVector3f(getRightTop(), getRightDown(), matrix.get(1, 4).distance(matrix.get(2, 4)) * scalingFactor * (float) getImage().height()));
        positionMatrix.set(3, 4, cutVector3f(getRightDown(), getRightTop(), matrix.get(3, 4).distance(matrix.get(4, 4)) * scalingFactor * (float) getImage().height()));

        positionMatrix.set(2, 2, intersectionVector3f(positionMatrix.get(2, 1), positionMatrix.get(2, 4),
                positionMatrix.get(1, 2), positionMatrix.get(4, 2)));
        positionMatrix.set(2, 3, intersectionVector3f(positionMatrix.get(2, 4), positionMatrix.get(2, 1),
                positionMatrix.get(1, 3), positionMatrix.get(4, 3)));
        positionMatrix.set(3, 2, intersectionVector3f(positionMatrix.get(3, 1), positionMatrix.get(3, 4),
                positionMatrix.get(4, 2), positionMatrix.get(1, 2)));
        positionMatrix.set(3, 3, intersectionVector3f(positionMatrix.get(3, 4), positionMatrix.get(3, 1),
                positionMatrix.get(4, 3), positionMatrix.get(4, 1)));
    }

    public void setBorders(float left, float right, float top, float bottom) {
        this.left = left / image.width();
        this.right = (image.width() - right) / image.width();
        this.top = top / image.height();
        this.bottom = (image.height() - bottom) / image.height();
        updateMatrix();
    }

    public void setBordersDirectly(float left, float right, float top, float bottom) {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
        updateMatrix();
    }

    @Override
    public NineSlicedImageMask rectangle(Vector3f leftTop, Axis xAxis, Axis yAxis, boolean xPositive, boolean yPositive, float width, float height) {
        return (NineSlicedImageMask) super.rectangle(leftTop, xAxis, yAxis, xPositive, yPositive, width, height);
    }

    public void updateMapping() {
        if (!positionMatrix.get(1, 1).equals(getLeftTop()) ||
        !positionMatrix.get(1, 4).equals(getRightTop()) ||
        !positionMatrix.get(4, 1).equals(getLeftDown()) ||
        !positionMatrix.get(4, 4).equals(getRightDown()))
            updatePositionMatrix();
    }

    @Override
    public void renderToGui() {
        for(int y = 1; y < 4; y++) {
            for (int x = 1; x < 4; x++) {
                image.renderToGui(positionMatrix.get(y, x), positionMatrix.get(y, x + 1),
                        positionMatrix.get(y + 1, x), positionMatrix.get(y + 1, x + 1),
                        matrix.get(y, x), matrix.get(y, x + 1), matrix.get(y + 1, x), matrix.get(y + 1, x + 1),
                        getColor());
            }
        }
    }

    @Override
    public void renderToWorld(PoseStack pose, MultiBufferSource buffer, RenderType type, boolean revert, int light) {
        if (buffer == null) return;
        for (int y = 1; y < 4; y++) {
            for (int x = 1; x < 4; x++) {
                image.renderToWorld(pose, buffer, type,
                        positionMatrix.get(y, x), positionMatrix.get(y, x + 1),
                        positionMatrix.get(y + 1, x), positionMatrix.get(y + 1, x + 1),
                        matrix.get(y, x), matrix.get(y, x + 1), matrix.get(y + 1, x), matrix.get(y + 1, x + 1),
                        getColor(), revert, light);
            }
        }
    }

    @Util
    public static Vector3f samplingVector3f(Vector3f begin, Vector3f end, float percentage) {
        Vector3f offset = end.copy();
        offset.sub(begin);
        offset.mul(percentage);
        offset.add(begin);
        return offset;
    }

    @Util
    public static Vector3f cutVector3f(Vector3f begin, Vector3f end, float length) {
        Vector3f offset = end.copy();
        offset.sub(begin);
        offset.normalize();
        offset.mul(length);
        offset.add(begin);
        return offset;
    }

    /**
     * Algorithm from <a href="https://zhuanlan.zhihu.com/p/690258055">here</a>
     * @param begin1 begin of line 1
     * @param end1 end of line 1
     * @param begin2 begin of line 2
     * @param end2 end of line 2
     * @return intersection point
     */
    @Util
    public static Vector3f intersectionVector3f(Vector3f begin1, Vector3f end1, Vector3f begin2, Vector3f end2) {
        Vector3f a = end1.copy();
        a.sub(begin1);
        Vector3f b = end2.copy();
        b.sub(begin2);
        Vector3f t = begin2.copy();
        t.sub(begin1);

        float aSqr = a.dot(a);
        float bSqr = b.dot(b);
        float a_dot_b = a.dot(b);
        float a_dot_t = a.dot(t);
        float b_dot_t = b.dot(t);

        float denom = aSqr * bSqr - a_dot_b * a_dot_b;

        float u = (a_dot_t * bSqr - b_dot_t * a_dot_b) / denom;
        float v = (u * a_dot_b - b_dot_t) / bSqr;

        Vector3f px = a.copy();
        px.mul(u);
        Vector3f p_u = begin1.copy();
        p_u.add(px);
        Vector3f qx = b.copy();
        qx.mul(v);
        Vector3f q_v = begin2.copy();
        q_v.add(qx);
        Vector3f vec  = q_v.copy();
        vec.sub(p_u);

        float len = vec.dot(vec);
        if (len == 0) {
            return p_u;
        } else {
            return average(p_u, q_v);
        }
    }

    @Util
    public static Vector3f average(Vector3f... vector3fs) {
        Vector3f vector3f = Vector3f.ZERO.copy();
        for (Vector3f v : vector3fs) vector3f.add(v);
        vector3f.mul(1 / (float) vector3fs.length);
        return vector3f;
    }
}
