package kasuga.lib.core.client.render.texture;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import kasuga.lib.core.client.render.SimpleColor;
import kasuga.lib.core.client.render.PoseContext;
import kasuga.lib.core.client.render.RendererUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;

public class WorldTexture extends SimpleTexture {
    RenderTypeBuilder builder = null;
    RenderType cachedType = null;
    PoseContext context = PoseContext.of();

    public WorldTexture(@NotNull ResourceLocation location, int uOffset, int vOffset, int uWidth, int vHeight, int color, float alpha) {
        super(location, uOffset, vOffset, uWidth, vHeight, color, alpha);
    }

    public WorldTexture(@NotNull ResourceLocation location, InputStream stream, int uOffset, int vOffset, int uWidth, int vHeight, int color, float alpha) {
        super(location, stream, uOffset, vOffset, uWidth, vHeight, color, alpha);
    }

    public WorldTexture(@NotNull ResourceLocation location, int uOffset, int vOffset, int uWidth, int vHeight, int color) {
        super(location, uOffset, vOffset, uWidth, vHeight, color);
    }

    public WorldTexture(@NotNull ResourceLocation location, int uOffset, int vOffset, int uWidth, int vHeight) {
        super(location, uOffset, vOffset, uWidth, vHeight);
    }

    public WorldTexture(@NotNull ResourceLocation location, int uWidth, int vHeight, int color, float alpha) {
        super(location, uWidth, vHeight, color, alpha);
    }

    public WorldTexture(@NotNull ResourceLocation location, int uWidth, int vHeight, int color) {
        super(location, uWidth, vHeight, color);
    }

    public WorldTexture(@NotNull ResourceLocation location, int uWidth, int vHeight) {
        super(location, uWidth, vHeight);
    }

    public WorldTexture(@NotNull ResourceLocation location, int color, float alpha) {
        super(location, color, alpha);
    }

    public WorldTexture(@NotNull ResourceLocation location, int color) {
        super(location, color);
    }

    public WorldTexture(@NotNull ResourceLocation location, float alpha) {
        super(location, alpha);
    }

    public WorldTexture(@NotNull ResourceLocation location) {
        super(location);
    }

    public WorldTexture cut(int left, int up, int right, int down) {
        return new WorldTexture(location, uOffset + left, vOffset + up, uWidth - right - left, vHeight - down - up, color.getRGB(), color.getA());
    }

    public WorldTexture cutSize(int left,int up,int width,int height){
        return new WorldTexture(location, uOffset + left, vOffset+up, width,height,color.getRGB(),color.getA());
    }

    public WorldTexture flipY() {
        return new WorldTexture(location, uOffset + uWidth, vOffset, - uWidth, vHeight, color.getRGB(), color.getA());
    }

    public WorldTexture flipX() {
        return new WorldTexture(location, uOffset, vOffset + vHeight, uWidth, - vHeight, color.getRGB(), color.getA());
    }

    public void addAction(PoseContext.Action act) {
        context.addAct(act);
    }

    public void addAction(int index, PoseContext.Action act) {
        context.addAct(index, act);
    }

    public void translate(double x, double y, double z) {
        context.translate(x, - y, z);
    }

    public void scale(float x, float y, float z) {
        context.scale(x, y, z);
    }

    public void rotateX(float x_rotation) {
        context.rotateX(x_rotation);
    }

    public void rotateY(float y_rotation) {
        context.rotateY(y_rotation);
    }

    public void rotateZ(float z_rotation) {
        context.rotateZ(z_rotation);
    }

    public void lockMovement() {
        this.context.setLock(true);
    }

    public void unlockMovement() {
        this.context.setLock(false);
    }

    public boolean isMovementLocked() {
        return this.context.isLocked();
    }

    public void shouldAutoClearMovement(boolean flag) {
        context.setAutoClear(flag);
    }

    public boolean isAutoClearMovements() {
        return context.isAutoClear();
    }

    public void renderType(RenderTypeBuilder builder) {
        this.builder = builder;
    }

    public RenderType getCachedType() {
        return cachedType;
    }

    @Override
    public WorldTexture withColor(int color,float alpha){
        return new WorldTexture(location,uOffset,vOffset,uWidth,vHeight,color,alpha);
    }


    public void turnToPlayer(@Nullable Player player, Vec3 position) {
        this.rotateY((float) RendererUtil.getVecHorizontalAngles(position, player == null ? position : player.getEyePosition()));
    }

    public float getFixedHeight(float width) {
        return width / (float) widthHeightRatio();
    }

    public float getFixedWidth(float height) {
        return height * (float) widthHeightRatio();
    }


    public void render(PoseStack pose, MultiBufferSource buffer, float width, float height, int light) {
        if(builder == null) return;
        if(cachedType == null)
            cachedType = builder.build(location);
        boolean shouldPush = !pose.clear();
        Matrix4f lastMatrix = null;
        if(shouldPush) {
            pose.pushPose();
        } else {
            lastMatrix = pose.last().pose();
            pose.popPose();
            pose.pushPose();
        }
        pose.scale(1.0f, -1.0f, 1.0f);
        pose.translate(.5f, 0, .5f);
        context.apply(pose);
        Matrix4f matrix = pose.last().pose();
        VertexConsumer consumer = buffer.getBuffer(cachedType);
        buildVertex(consumer, matrix, 0, 0, 0, fuOffset, fvOffset, color, light);
        buildVertex(consumer, matrix, width, 0, 0, fuOffset + fuWidth, fvOffset, color, light);
        buildVertex(consumer, matrix, width, height, 0, fuOffset + fuWidth, fvOffset + fvHeight, color, light);
        buildVertex(consumer, matrix, 0, height, 0, fuOffset, fvOffset + fvHeight, color, light);
        pose.popPose();
        if(!shouldPush) {
            pose.pushPose();
            pose.mulPoseMatrix(lastMatrix);
        }
    }

    public void renderScaled(PoseStack pose, MultiBufferSource buffer, int light, float axis, boolean isWidth) {
        if(isWidth)
            render(pose, buffer, axis, getFixedHeight(axis), light);
        else
            render(pose, buffer, getFixedWidth(axis), axis, light);
    }

    public void renderCentered(PoseStack pose, MultiBufferSource buffer, float width, float height, int light) {
        translate(- width/2, height/2, 0);
        render(pose, buffer, width, height, light);
    }

    public void renderCenteredScaled(PoseStack pose, MultiBufferSource buffer, int light, float axis, boolean isWidth) {
        if(isWidth) {
            translate( - axis / 2, getFixedHeight(axis) / 2, 0);
            render(pose, buffer, axis, getFixedHeight(axis), light);
        }
        else {
            translate( - getFixedWidth(axis) / 2, axis / 2, 0);
            render(pose, buffer, getFixedWidth(axis), axis, light);
        }
    }

    private void buildVertex(VertexConsumer consumer, Matrix4f matrix,
                             float x, float y, float z, float u, float v, SimpleColor color, int light) {
        consumer.vertex(matrix, x, y, z)
                .color(color.getfR(), color.getfG(), color.getfB(), color.getA())
                .uv(u, v)
                .uv2(light)
                .endVertex();
    }

    public interface RenderTypeBuilder {
        RenderType build(ResourceLocation location);
    }
}
