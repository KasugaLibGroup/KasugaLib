package kasuga.lib.core.client.render.texture;

import com.mojang.blaze3d.vertex.PoseStack;
import kasuga.lib.core.annos.Util;
import kasuga.lib.core.client.animation.neo_neo.VectorUtil;
import kasuga.lib.core.client.render.SimpleColor;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * ImageMask holds all operations (UV or size) of your image.
 * Use {@link NineSlicedImageMask} for nine sliced operations.
 * See {@link StaticImage} for the image itself.
 */
public class ImageMask {
    private Vector3f leftTop, rightTop, leftDown, rightDown, pivot;
    private Vec2f uvLeftTop, uvRightTop, uvLeftDown, uvRightDown;
    private SimpleColor color;
    public final StaticImage image;
    public ImageMask(StaticImage image) {
        this.image = image;
        initialize();
    }

    public void initialize() {
        this.leftTop = new Vector3f(0, 0, 0);
        this.rightTop = new Vector3f(image.width(), 0, 0);
        this.leftDown = new Vector3f(0, image.height(), 0);
        this.rightDown = new Vector3f(image.width(), image.height(), 0);
        this.pivot = new Vector3f(((float) image.width()) / 2, ((float) image.height()) / 2, 0);
        this.uvLeftTop = Vec2f.ZERO;
        this.uvRightTop = Vec2f.ZERO.add(1, 0);
        this.uvLeftDown = Vec2f.ZERO.add(0, 1);
        this.uvRightDown = Vec2f.ZERO.add(1, 1);
        this.color = SimpleColor.fromRGBA(255, 255, 255, 255);
    }

    public ImageMask(ImageMask mask) {
        this.image = mask.image;
        this.leftTop = new Vector3f(mask.leftTop);
        this.rightTop = new Vector3f(mask.rightTop);
        this.leftDown = new Vector3f(mask.leftDown);
        this.rightDown = new Vector3f(mask.rightDown);
        this.uvLeftTop = mask.uvLeftTop.copy();
        this.uvRightTop = mask.uvRightTop.copy();
        this.uvLeftDown = mask.uvLeftDown.copy();
        this.uvRightDown = mask.uvRightDown.copy();
        this.pivot = new Vector3f(mask.pivot);
        this.color = mask.color.copy();
    }

    public ImageMask(String name, CompoundTag nbt) {
        CompoundTag tag = nbt.getCompound(name);
        image = StaticImage.STACK.get(new ResourceLocation(tag.getString("id"))).get();
        this.leftTop = deserializeVector3f(tag.getCompound("leftTop"));
        this.leftDown = deserializeVector3f(tag.getCompound("leftDown"));
        this.rightTop = deserializeVector3f(tag.getCompound("rightTop"));
        this.rightDown = deserializeVector3f(tag.getCompound("rightDown"));
        this.uvLeftTop = new Vec2f(tag.getCompound("uvLeftTop"));
        this.uvRightTop = new Vec2f(tag.getCompound("uvRightTop"));
        this.uvLeftDown = new Vec2f(tag.getCompound("uvLeftDown"));
        this.uvRightDown = new Vec2f(tag.getCompound("uvRightDown"));
        this.color = SimpleColor.fromRGBA(tag.getInt("color"), tag.getInt("alpha"));
    }

    public ImageMask copyWithOp(ImageMaskOp operation) {
        ImageMask mask = new ImageMask(this);
        return operation.operate(mask);
    }

    public void serialize(String name, CompoundTag nbt) {
        CompoundTag tag = new CompoundTag();
        tag.put("leftTop", serializeVector3f(leftTop));
        tag.put("leftDown", serializeVector3f(leftDown));
        tag.put("rightTop", serializeVector3f(rightTop));
        tag.put("rightDown", serializeVector3f(rightDown));
        tag.put("uvLeftTop", uvLeftTop.serialize());
        tag.put("uvLeftDown", uvLeftDown.serialize());
        tag.put("uvRightTop", uvRightTop.serialize());
        tag.put("uvRightDown", uvRightDown.serialize());
        tag.putInt("color", color.getRGB());
        tag.putFloat("alpha", color.getA());
        tag.putString("id", image.id.toString());
        nbt.put(name, tag);
    }

    public void renderToGui() {
        image.renderToGui(this);
    }

    public void renderToWorld(PoseStack pose, MultiBufferSource buffer, RenderType type, boolean revert, int light) {
        image.renderToWorld(pose, buffer, type, this, revert, light);
    }

    public void renderToGui(PoseStack.Pose pose) {
        image.renderToGui(this, pose);
    }

    public StaticImage getImage() {
        return image;
    }

    public SimpleColor getColor() {
        return color;
    }

    public void setColor(SimpleColor color) {
        this.color = color;
    }

    public ImageMask setPosition(float xLeftTop, float yLeftTop, float zLeftTop,
                                 float xRightTop, float yRightTop, float zRightTop,
                                 float xLeftDown, float yLeftDown, float zLeftDown,
                                 float xRightDown, float yRightDown, float zRightDown) {
        return setPosition(new Vector3f(xLeftTop, yLeftTop, zLeftTop), new Vector3f(xRightTop, yRightTop, zRightTop),
                new Vector3f(xLeftDown, yLeftDown, zLeftDown), new Vector3f(xRightDown, yRightDown, zRightDown));
    }

    public ImageMask setPosition(Vector3f leftTop, Vector3f rightTop, Vector3f leftDown, Vector3f rightDown) {
        this.leftTop = leftTop;
        this.rightTop = rightTop;
        this.leftDown = leftDown;
        this.rightDown = rightDown;
        updateMapping();
        return this;
    }

    public ImageMask quadrilateral(Vector3f leftTop, Vector3f xDirection, Vector3f yDirection, float width, float height) {
        this.leftTop = leftTop;
        this.rightTop = new Vector3f(leftTop);
        Vector3f w = new Vector3f(xDirection);
        w.normalize();
        w.mul(width);
        Vector3f h = new Vector3f(yDirection);
        h.normalize();
        h.mul(height);
        rightTop.add(w);
        leftDown = new Vector3f(leftTop);
        leftDown.add(h);
        rightDown = new Vector3f(leftDown);
        rightDown.add(w);
        updateMapping();
        return this;
    }

    public ImageMask rectangle(Vector3f leftTop, Axis xAxis, Axis yAxis, boolean xPositive, boolean yPositive, float width, float height) {
        if (xAxis.equals(yAxis)) return this;
        Vector3f xDirection = getDirectionVector(xAxis, xPositive);
        Vector3f yDirection = getDirectionVector(yAxis, yPositive);
        return quadrilateral(leftTop, xDirection, yDirection, width, height);
    }

    private Vector3f getDirectionVector(Axis axis, boolean positive) {
        return switch (axis) {
            case X -> positive ? new Vector3f(1, 0, 0) : new Vector3f(-1, 0, 0);
            case Y -> positive ? new Vector3f(0, 1, 0) : new Vector3f(0, -1, 0);
            case Z -> positive ? new Vector3f(0, 0, 1) : new Vector3f(0, 0, -1);
        };
    }

    public ImageMask offset(float x, float y, float z) {
        this.leftDown.add(x, y, z);
        this.rightDown.add(x, y, z);
        this.leftTop.add(x, y, z);
        this.rightTop.add(x, y, z);
        updateMapping();
        return this;
    }

    public ImageMask offset(Vector3f offset) {
        this.leftDown.add(offset);
        this.rightDown.add(offset);
        this.leftTop.add(offset);
        this.rightTop.add(offset);
        updateMapping();
        return this;
    }

    public ImageMask offsetPivot(Vector3f offset) {
        this.pivot.add(offset);
        return this;
    }

    public ImageMask offsetPivot(float x, float y, float z) {
        this.pivot.add(x, y, z);
        return this;
    }

    public ImageMask offsetWithPivot(Vector3f offset) {
        offset(offset);
        offsetPivot(offset);
        return this;
    }

    public ImageMask offsetWithPivot(float x, float y, float z) {
        offset(x, y, z);
        offsetPivot(x, y, z);
        return this;
    }

    public ImageMask rotateX(float rad) {
        return rotateByPivot(rad, 0, 0);
    }

    public ImageMask rotateY(float rad) {
        return rotateByPivot(0, rad, 0);
    }

    public ImageMask rotateZ(float rad) {
        return rotateByPivot(0, 0, rad);
    }

    public ImageMask rotateByPivot(float radX, float radY, float radZ) {
        return rotateByPivot(new Vector3f(radX, radY, radZ));
    }

    public ImageMask rotateByPivot(Vector3f rotationRad) {
        return rotateByPivot(pivot, rotationRad);
    }

    public ImageMask rotateByPivot(Vector3f pivot, Vector3f rotationRad) {
        Quaternionf quaternion = VectorUtil.fromXYZ(rotationRad);
        leftTop = rotatePoint(leftTop, pivot, quaternion);
        rightTop = rotatePoint(rightTop, pivot, quaternion);
        leftDown = rotatePoint(leftDown, pivot, quaternion);
        rightDown = rotatePoint(rightDown, pivot, quaternion);
        updateMapping();
        return this;
    }

    public ImageMask rotateDegX(float deg) {
        return rotateByPivotDeg(deg, 0, 0);
    }

    public ImageMask rotateDegY(float deg) {
        return rotateByPivotDeg(0, deg, 0);
    }

    public ImageMask rotateDegZ(float deg) {
        return rotateByPivotDeg(0, 0, deg);
    }

    public ImageMask rotateByPivotDeg(float degX, float degY, float degZ) {
        return rotateByPivotDeg(new Vector3f(degX, degY, degZ));
    }

    public ImageMask rotateByPivotDeg(Vector3f pivot, float degX, float degY, float degZ) {
        return rotateByPivotDeg(pivot, new Vector3f(degX, degY, degZ));
    }

    public ImageMask rotateByPivotDeg(Vector3f rotationDeg) {
        return rotateByPivotDeg(pivot, rotationDeg);
    }

    public ImageMask rotateByPivotDeg(Vector3f pivot, Vector3f rotationDeg) {
        Vector3f rad = new Vector3f(rotationDeg);
        rad.mul((float) Math.PI / 180);
        return rotateByPivot(pivot, rad);
    }

    public ImageMask flip(Vector3f pivot, Axis axis) {
        this.leftTop = flipPoint(this.leftTop, pivot, axis);
        this.leftDown = flipPoint(this.leftDown, pivot, axis);
        this.rightTop = flipPoint(this.rightTop, pivot, axis);
        this.rightDown = flipPoint(this.rightDown, pivot, axis);
        updateMapping();
        return this;
    }

    public ImageMask flipByGeometricCenter(Axis axis) {
        Vector3f geometricCenter = getGeometricCenter();
        return flip(geometricCenter, axis);
    }

    public ImageMask flipByPivot(Axis axis) {
        return flip(pivot, axis);
    }

    public ImageMask flipByLeftTop(Axis axis) {return flip(leftTop, axis);}

    public ImageMask flipByZero(Axis axis) {
        return flip(new Vector3f(), axis);
    }

    public ImageMask scale(Vector3f pivot, float factor) {
        this.leftTop = scalePoint(this.leftTop, pivot, factor);
        this.leftDown = scalePoint(this.leftDown, pivot, factor);
        this.rightDown = scalePoint(this.rightDown, pivot, factor);
        this.rightTop = scalePoint(this.rightTop, pivot, factor);
        updateMapping();
        return this;
    }

    public ImageMask scaleByPivot(float factor) {
        return scale(pivot, factor);
    }

    public ImageMask scaleByGeometricCenter(float factor) {
        return scale(getGeometricCenter(), factor);
    }

    public ImageMask scaleByLeftTop(float factor) {
        return scale(leftTop, factor);
    }

    public ImageMask scaleByZero(float factor) {
        return scale(new Vector3f(), factor);
    }

    public void setLeftTop(float x, float y, float z) {
        this.leftTop = new Vector3f(x, y, z);
    }

    public void setRightTop(float x, float y, float z) {
        this.rightTop = new Vector3f(x, y, z);
    }

    public void setLeftDown(float x, float y, float z) {
        this.leftDown = new Vector3f(x, y, z);
    }

    public void setRightDown(float x, float y, float z) {
        this.rightDown = new Vector3f(x, y, z);
    }

    public void setLeftTop(Vector3f leftTop) {
        this.leftTop = leftTop;
    }

    public void setRightTop(Vector3f rightTop) {
        this.rightTop = rightTop;
    }

    public void setLeftDown(Vector3f leftDown) {
        this.leftDown = leftDown;
    }

    public void setRightDown(Vector3f rightDown) {
        this.rightDown = rightDown;
    }

    public Vector3f getLeftTop() {
        return leftTop;
    }

    public Vector3f getRightTop() {
        return rightTop;
    }

    public Vector3f getLeftDown() {
        return leftDown;
    }

    public Vector3f getRightDown() {
        return rightDown;
    }

    public ImageMask setUV(Vec2f leftTop, Vec2f rightTop, Vec2f leftDown, Vec2f rightDown) {
        this.uvLeftTop = leftTop;
        this.uvRightTop = rightTop;
        this.uvLeftDown = leftDown;
        this.uvRightDown = rightDown;
        updateMapping();
        return this;
    }

    public ImageMask setUV(float leftTopX, float leftTopY, float rightTopX, float rightTopY,
                           float leftDownX, float leftDownY, float rightDownX, float rightDownY) {
        return setUV(new Vec2f(leftTopX, leftTopY), new Vec2f(rightTopX, rightTopY),
                new Vec2f(leftDownX, leftDownY), new Vec2f(rightDownX, rightDownY));
    }

    public ImageMask rectangleUV(Vec2f leftTop, Vec2f rightDown) {
        this.uvLeftTop = leftTop;
        this.uvRightDown = rightDown;
        this.uvLeftDown = new Vec2f(leftTop.x(), rightDown.y());
        this.uvRightTop = new Vec2f(rightDown.x(), leftTop.y());
        updateMapping();
        return this;
    }

    public ImageMask rectangleUV(float leftTopX, float leftTopY, float rightDownX, float rightDownY) {
        return rectangleUV(new Vec2f(leftTopX, leftTopY), new Vec2f(rightDownX, rightDownY));
    }

    public ImageMask offsetUV(Vec2f offset) {
        Vec2f normalOffset = offset.scale(1 / (float)image.width(), 1/(float) image.height());
        this.uvLeftTop.add(normalOffset);
        this.uvLeftDown.add(normalOffset);
        this.uvRightTop.add(normalOffset);
        this.uvRightDown.add(normalOffset);
        updateMapping();
        return this;
    }

    public ImageMask scaleUV(Vec2f pivot, float factor) {
        this.uvLeftTop = scaleUVPoint(this.uvLeftTop, pivot, factor);
        this.uvLeftDown = scaleUVPoint(this.uvLeftDown, pivot, factor);
        this.uvRightTop = scaleUVPoint(this.uvRightTop, pivot, factor);
        this.uvRightDown = scaleUVPoint(this.uvRightDown, pivot, factor);
        updateMapping();
        return this;
    }

    public ImageMask scaleUVByGeometricCenter(float factor) {
        this.uvLeftTop = scaleUVPoint(this.uvLeftTop, this.getUvGeometricCenter(), factor);
        this.uvRightTop = scaleUVPoint(this.uvRightTop, this.getUvGeometricCenter(), factor);
        this.uvLeftDown = scaleUVPoint(this.uvLeftDown, this.getUvGeometricCenter(), factor);
        this.uvRightDown = scaleUVPoint(this.uvRightDown, this.getUvGeometricCenter(), factor);
        updateMapping();
        return this;
    }

    public ImageMask scaleUVByZero(float factor) {
        this.uvLeftTop = scaleUVPoint(this.uvLeftTop, Vec2f.ZERO, factor);
        this.uvLeftDown = scaleUVPoint(this.uvLeftDown, Vec2f.ZERO, factor);
        this.uvRightDown = scaleUVPoint(this.uvRightDown, Vec2f.ZERO, factor);
        this.uvRightTop = scaleUVPoint(this.uvRightTop, Vec2f.ZERO, factor);
        updateMapping();
        return this;
    }

    public ImageMask rotateUVByGeometricCenter(float rad) {
        return rotateUV(getUvGeometricCenter(), rad);
    }

    public ImageMask rotateUVByGeometricCenterDeg(float deg) {
        return rotateUVDeg(getUvGeometricCenter(), deg);
    }

    public ImageMask rotateUVByZero(float rad) {return rotateUV(Vec2f.ZERO, rad);}

    public ImageMask rotateUVByZeroDeg(float deg) {
        return rotateUVDeg(Vec2f.ZERO, deg);
    }

    public ImageMask rotateUVByLeftTop(float rad) {
        return rotateUV(uvLeftTop, rad);
    }

    public ImageMask rotateUVByLeftTopDeg(float deg) {
        return rotateUVDeg(uvLeftTop, deg);
    }

    public ImageMask rotateUV(Vec2f pivot, float rad) {
        uvLeftTop = this.uvLeftTop.rotate(pivot, rad);
        uvLeftDown = this.uvLeftDown.rotate(pivot, rad);
        uvRightDown = this.uvRightDown.rotate(pivot, rad);
        uvRightTop = this.uvRightTop.rotate(pivot, rad);
        updateMapping();
        return this;
    }

    public ImageMask rotateUVDeg(Vec2f pivot, float deg) {
        return rotateUV(pivot, deg * (float) Math.PI / 180f);
    }

    public ImageMask flipUVByGeometricCenter(Axis axis) {
        return flipUV(getUvGeometricCenter(), axis);
    }

    public ImageMask flipUVByZero(Axis axis) {
        return flipUV(Vec2f.ZERO, axis);
    }

    public ImageMask flipUVByLeftTop(Axis axis) {
        return flipUV(uvLeftTop, axis);
    }

    public ImageMask flipUV(Vec2f pivot, Axis axis) {
        this.uvLeftTop = flipVUPoint(this.uvLeftTop, pivot, axis);
        this.uvRightTop = flipVUPoint(this.uvRightTop, pivot, axis);
        this.uvLeftDown = flipVUPoint(this.uvLeftDown, pivot, axis);
        this.uvRightDown = flipVUPoint(this.uvRightDown, pivot, axis);
        updateMapping();
        return this;
    }

    public Vec2f getUvLeftTop() {
        return uvLeftTop;
    }

    public Vec2f getUvLeftDown() {
        return uvLeftDown;
    }

    public Vec2f getUvRightDown() {
        return uvRightDown;
    }

    public Vec2f getUvRightTop() {
        return uvRightTop;
    }

    public Vector3f getPivot() {
        return pivot;
    }

    public void setPivot(Vector3f pivot) {
        this.pivot = pivot;
    }

    public void setUvLeftDown(Vec2f uvLeftDown) {
        this.uvLeftDown = uvLeftDown;
    }

    public void setUvLeftTop(Vec2f uvLeftTop) {
        this.uvLeftTop = uvLeftTop;
    }

    public void setUvRightDown(Vec2f uvRightDown) {
        this.uvRightDown = uvRightDown;
    }

    public void setUvRightTop(Vec2f uvRightTop) {
        this.uvRightTop = uvRightTop;
    }

    public Vector3f getGeometricCenter() {
        Vector3f result = new Vector3f(leftTop);
        result.add(leftDown);
        result.add(rightTop);
        result.add(rightDown);
        result.mul(.25f);
        return result;
    }

    public ImageMask setPivotAsGeometricCenter() {
        this.pivot = getGeometricCenter();
        return this;
    }

    public Vec2f getUvGeometricCenter() {
        return Vec2f.average(uvLeftTop, uvLeftDown, uvRightDown, uvRightTop);
    }


    public void updateMapping(){}

    @Util
    public static Vector3f rotatePoint(Vector3f point, Vector3f pivot, Quaternionf quaternion) {
        Vector3f offset = new Vector3f(point);
        offset.sub(pivot);
        quaternion.transform(offset);
        Vector3f result = new Vector3f(pivot);
        result.add(offset);
        return result;
    }

    @Util
    public static Vector3f flipPoint(Vector3f point, Vector3f pivot, Axis axis) {
        Vector3f offset = new Vector3f(point);
        offset.sub(pivot);
        switch (axis) {
            case X -> offset.x = (- offset.x());
            case Y -> offset.y = (- offset.y());
            case Z -> offset.z = (- offset.z());
        }
        offset.add(pivot);
        return offset;
    }

    @Util
    public static Vec2f flipVUPoint(Vec2f point, Vec2f pivot, Axis axis) {
        Vec2f offset = point.subtract(pivot);
        switch (axis) {
            case X -> offset.setX( - offset.x());
            case Y -> offset.setY( - offset.y());
        }
        return offset.add(pivot);
    }

    @Util
    public static Vector3f scalePoint(Vector3f point, Vector3f pivot, float factor) {
        Vector3f offset = new Vector3f(pivot);
        offset.sub(point);
        offset.mul(factor);
        offset.add(point);
        return offset;
    }

    @Util
    public static Vec2f scaleUVPoint(Vec2f point, Vec2f pivot, float factor) {
        return pivot.subtract(point).scale(factor).add(point);
    }

    @Util
    public static CompoundTag serializeVector3f(Vector3f vector3f) {
        CompoundTag nbt = new CompoundTag();
        nbt.putFloat("x", vector3f.x());
        nbt.putFloat("y", vector3f.y());
        nbt.putFloat("z", vector3f.z());
        return nbt;
    }

    @Util
    public static Vector3f deserializeVector3f(CompoundTag nbt) {
        return new Vector3f(nbt.getFloat("x"), nbt.getFloat("y"), nbt.getFloat("z"));
    }

    @Util
    @FunctionalInterface
    public interface ImageMaskOp {
        ImageMask operate(ImageMask mask);
    }

    @Util
    public enum Axis implements StringRepresentable {
        X, Y, Z;

        @Override
        public @NotNull String getSerializedName() {
            return switch (this) {
                case X -> "x";
                case Y -> "y";
                case Z -> "z";
            };
        }

        public static @NotNull Axis fromSerializedName(String input) {
            return switch (input) {
                case "x" -> X;
                case "y" -> Y;
                case "z" -> Z;
                default -> throw new IllegalArgumentException("Illegal input: " + input);
            };
        }
    }
}
