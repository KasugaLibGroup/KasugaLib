package kasuga.lib.core.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class RenderableItem {
    private final ItemStack stack;
    private Font font;
    private boolean renderCount = false;
    private int width = 16, height = 16;
    public RenderableItem(ItemStack stack) {
        this.stack = stack;
        font = Minecraft.getInstance().font;
    }

    public RenderableItem(ItemStack stack, int width, int height) {
        this.stack = stack;
        font = Minecraft.getInstance().font;
        this.width = width;
        this.height = height;
    }

    public boolean isRenderingCount() {
        return renderCount;
    }

    public void renderCount(boolean TorF) {
        this.renderCount = TorF;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public ItemStack getStack() {
        return stack;
    }

    public void render(@Nullable GuiGraphics pose, ItemRenderer renderer, int x, int y, int width, int height){
        if(stack == null) return;
        renderItem(renderer, stack, x, y, width, height);
        if(renderCount && stack.isStackable() && stack.getCount() > 1 && pose != null) {
            String count = String.valueOf(stack.getCount());
            int w = font.width(count);
            pose.drawString(font, count, x + width - w, y + height - font.lineHeight, 0xffffff);
        }
    }

    public void render(@Nullable GuiGraphics pose, ItemRenderer renderer, int x, int y) {
        render(pose, renderer, x, y, width, height);
    }

    public void renderCentered(@Nullable GuiGraphics pose, ItemRenderer renderer, int centerX, int centerY, int width, int height) {
        render(pose, renderer, centerX - width/2, centerY - height/2);
    }

    public void renderCentered(@Nullable GuiGraphics pose, ItemRenderer renderer, int centerX, int centerY) {
        renderCentered(pose, renderer, centerX, centerY, width, height);
    }

    private void renderItem(
            ItemRenderer renderer, ItemStack item, int x, int y, int width, int height) {
        BakedModel model = renderer.getModel(item, (Level) null, (LivingEntity) null, 0);
        this.renderGuiItem(renderer, item, x, y, width, height, model);
    }

    protected void renderGuiItem(
            ItemRenderer renderer,
            ItemStack pStack,
            int x,
            int y,
            int width,
            int height,
            BakedModel pBakedModel) {
        TextureManager manager = Minecraft.getInstance().textureManager;
        manager.getTexture(TextureAtlas.LOCATION_BLOCKS).setFilter(false, false);
        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        PoseStack posestack = RenderSystem.getModelViewStack();
        posestack.pushPose();
        posestack.translate(x, y, 0);
        posestack.translate(((float) width) / 2, ((float) height) / 2, 0.0D);
        posestack.scale(1.0F, -1.0F, 1.0F);
        posestack.scale(((float) width), ((float) height), 1.0F);
        RenderSystem.applyModelViewMatrix();
        PoseStack posestack1 = new PoseStack();
        MultiBufferSource.BufferSource multibuffersource$buffersource =
                Minecraft.getInstance().renderBuffers().bufferSource();
        boolean flag = !pBakedModel.usesBlockLight();
        if (flag) {
            Lighting.setupForFlatItems();
        }

        renderer.render(
                pStack,
                ItemDisplayContext.GUI,
                false,
                posestack1,
                multibuffersource$buffersource,
                15728880,
                OverlayTexture.NO_OVERLAY,
                pBakedModel);
        multibuffersource$buffersource.endBatch();
        RenderSystem.enableDepthTest();
        if (flag) {
            Lighting.setupFor3DItems();
        }

        posestack.popPose();
        RenderSystem.applyModelViewMatrix();
    }
}
