package kasuga.lib.core.client.render.texture;

import kasuga.lib.core.client.render.PoseContext;

import java.util.function.Consumer;

public abstract class GraphicsImage<T extends GraphicsImage> {
    protected float uOffset = 0F, vOffset = 0F, uWidth = 0F, vHeight = 0F, imageHeight = 0F, imageWidth = 0F; // original
    protected float uOffsetUVCache = 0F, vOffsetUVCache = 0F, uWidthUVCache = 0F, vHeightUVCache = 0F; // cache
    protected float pivotX = 0F, pivotY = 0F; // rotate
    PoseContext poseContext = PoseContext.of();

    protected GraphicsImage(
            float uOffset, float vOffset, float uWidth, float vHeight,
            float imageWidth, float imageHeight
    ) {
        this.uOffset = uOffset;
        this.vOffset = vOffset;
        this.uWidth = uWidth;
        this.vHeight = vHeight;
        this.imageHeight = imageHeight;
        this.imageWidth = imageWidth;
    }

    public void setUV(float uOffset, float vOffset, float uWidth, float vHeight) {
        if (!Float.isNaN(uOffset)) {
            this.uOffset = uOffset;
            this.uOffsetUVCache = uOffset / imageWidth;
        }

        if (!Float.isNaN(vOffset)) {
            this.vOffset = vOffset;
            this.vOffsetUVCache = vOffset / imageHeight;
        }
        if (!Float.isNaN(uWidth)) {
            this.uWidth = uWidth;
            this.uWidthUVCache = uWidth / imageWidth;
        }

        if (!Float.isNaN(vOffset)) {
            this.vOffset = vHeight;
            this.vHeightUVCache = vHeightUVCache / imageHeight;
        }
    }

    public void refreshUV() {
        this.uOffsetUVCache = uOffset / imageWidth;
        this.vOffsetUVCache = vOffset / imageHeight;
        this.uWidthUVCache = uWidth / imageWidth;
        this.vHeightUVCache = vOffset / imageHeight;
    }

    public abstract T cloneTexture();

    public T withClone(Consumer<T> modifier) {
        T newTexture = cloneTexture();
        modifier.accept(newTexture);
        return newTexture;
    }

    public T uv(float uOffset, float vOffset, float uWidth, float vHeight) {
        return withClone((t) -> t.setUV(uOffset, vOffset, uWidth, vHeight));
    }

    public void setFlipX() {
        setUV(uOffset + uWidth, Float.NaN, -uWidth, Float.NaN);
    }

    public void setFlipY() {
        setUV(Float.NaN, vOffset + vHeight, Float.NaN, -vHeight);
    }

    public T flipX() {
        return withClone((t) -> t.setFlipX());
    }

    public T flipY() {
        return withClone((t) -> t.setFlipY());
    }

    public void setCropping(int left, int top, int right, int down) {
        uOffset += left;
        vOffset += top;
        uWidth -= left + right;
        vHeight -= top + down;
        refreshUV();
    }

    public T crop(int left, int top, int right, int down) {
        return withClone((t) -> t.setCropping(left, top, right, down));
    }

    public void setSubImage(int left, int top, int width, int height) {
        uOffset += left;
        vOffset += top;
        this.uWidth = width;
        this.vHeight = height;
        refreshUV();
    }

    public T subImage(int left, int top, int width, int height) {
        return withClone((t) -> t.setCropping(left, top, width, height));
    }

    public PoseContext getPoseContext(){
        return poseContext;
    }

    public T withPoseContext(PoseContext context){
        return withClone((t)->t.poseContext = context);
    }
}
