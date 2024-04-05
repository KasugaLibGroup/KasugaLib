package kasuga.lib.core.client.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.client.animation.infrastructure.IAnchor;
import kasuga.lib.core.client.render.PoseContext;
import kasuga.lib.core.client.render.SimpleColor;
import kasuga.lib.core.client.render.RendererUtil;
import kasuga.lib.registrations.client.ModelReg;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;

public class SimpleModel {

    public final String key;
    RandomSource random;
    BakedModel model = null;
    SimpleColor color;
    PoseContext context = PoseContext.of();
    RenderTypeBuilder builder = null;
    RenderType cacheType = null;
    boolean useParentPose = false, shouldRender = true;

    public SimpleModel(@Nonnull String key, BakedModel model) {
        this.key = key;
        this.model = model;
        random = KasugaLib.STACKS.random();
        color = SimpleColor.fromRGBInt(0xffffff);
    }

    public SimpleModel(@Nonnull ModelReg reg) {
        this(reg.registrationKey, reg.getModel().model);
    }

    public void copyModelFrom(SimpleModel model) {
        this.model = model.model;
    }

    public void renderType(RenderTypeBuilder builder) {
        this.builder = builder;
    }

    public void addAction(PoseContext.Action act) {
        context.addAct(act);
    }

    public void translate(double x, double y, double z) {
        context.translate(x, y, z);
    }

    public void rotateY(float yRot) {
        context.rotateY(yRot);
    }

    public void rotateZ(float yRot) {
        context.rotateZ(yRot);
    }

    public void rotateX(float xRot) {
        context.rotateX(xRot);
    }

    public void scale(float x, float y, float z) {
        context.scale(x, y, z);
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

    public void setColor(int r, int g, int b, int a) {
        this.color = SimpleColor.fromRGBA(r, g, b, a);
    }

    public void setColor(SimpleColor color) {
        this.color = color;
    }

    public SimpleColor getColor() {
        return color;
    }

    public void turnToPlayer(@Nullable Player player, Vec3 position) {
        this.rotateY((float) RendererUtil.getVecHorizontalAngles(position, player == null ? position : player.getEyePosition()));
    }

    public boolean isUsingParentPose() {
        return useParentPose;
    }

    public void shouldUseParentPose(boolean flag) {
        this.useParentPose = flag;
    }

    public boolean isRendering()  {
        return shouldRender;
    }

    public void shouldRender(boolean flag) {
        this.shouldRender = flag;
    }


    public void render(PoseStack pose, MultiBufferSource source, float x, float y, float z, int light, int overlay) {
        if(!shouldRender) return;
        if(builder == null) return;
        if(cacheType == null) cacheType = builder.build();
        boolean shouldPush = pose.clear();
        Matrix4f lastMatrix = null;
        if (shouldPush) {
            lastMatrix = pose.last().pose();
            pose.popPose();
        }
        pose.pushPose();
        if(useParentPose && lastMatrix != null) pose.mulPoseMatrix(lastMatrix);
        context.apply(pose);
        pose.translate(x, y, z);
        renderModel(pose, source.getBuffer(cacheType), transform(random, cacheType), color, light, overlay);
        pose.popPose();
        if (shouldPush) {
            pose.pushPose();
            pose.mulPoseMatrix(lastMatrix);
        }
    }

    List<BakedQuad> transform(RandomSource random, RenderType type) {
        if(model == null) return List.of();
        return model.getQuads(null, null, random, ModelData.EMPTY, type);
    }

    void renderModel(PoseStack pose, VertexConsumer consumer, List<BakedQuad> quads, SimpleColor color, int light, int overlay) {
        for(BakedQuad bakedquad : quads) {
            consumer.putBulkData(pose.last(), bakedquad, color.getfR(), color.getfG(), color.getfB(), light, overlay);
        }
    }

    @Override
    public SimpleModel clone() {
        return new SimpleModel(String.valueOf(this.key.toCharArray()), this.model);
    }

    public BakedModel getModel() {
        return model;
    }

    public interface RenderTypeBuilder{
        RenderType build();
    }
}
