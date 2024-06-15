package kasuga.lib.core.client.render.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.client.render.SimpleColor;
import kasuga.lib.core.util.Resources;
import net.minecraft.CrashReport;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class SimpleTexture {
    int uOffset, vOffset, uWidth, vHeight, imgWidth, imgHeight;
    SimpleColor color;
    float fuOffset, fvOffset, fuWidth, fvHeight;
    float zoom = 1.0f;
    static final int COLOR_DEFAULT = 0xffffff;
    final ResourceLocation location;
    BufferedImage image;
    byte[] bytesOfImage;

    public SimpleTexture(@Nonnull ResourceLocation location, int uOffset, int vOffset, int uWidth, int vHeight, int color, float alpha) {
        this.location = location;
        this.uOffset = uOffset;
        this.vOffset = vOffset;
        this.uWidth = uWidth;
        this.vHeight = vHeight;
        this.color = SimpleColor.fromRGBA(color, alpha);
        if(KasugaLib.STACKS.isTextureRegistryFired())
            uploadPicture(location);
        else
            KasugaLib.STACKS.putUnregisteredPicIn(this);
    }

    public SimpleTexture(@Nonnull ResourceLocation location, InputStream stream, int uOffset, int vOffset, int uWidth, int vHeight, int color, float alpha) {
        this.location = location;
        this.uOffset = uOffset;
        this.vOffset = vOffset;
        this.uWidth = uWidth;
        this.vHeight = vHeight;
        this.color = SimpleColor.fromRGBA(color, alpha);
        try {
            Minecraft.getInstance().textureManager.register(location, new DynamicTexture(NativeImage.read(stream)));
            image = ImageIO.read(stream);
            bytesOfImage = stream.readAllBytes();
            this.imgWidth = image.getWidth();
            this.imgHeight = image.getHeight();
            refreshImage();
        } catch (Exception e) {
            Minecraft.crash(CrashReport.forThrowable(e, ""));
        }
    }

    public void uploadPicture(@Nonnull ResourceLocation location) {
        try {
            Resource resource = Resources.getResource(location);
            InputStream stream = resource.getInputStream();
            image = ImageIO.read(stream);
            bytesOfImage = stream.readAllBytes();
            stream.close();
            this.imgWidth = image.getWidth();
            this.imgHeight = image.getHeight();
            refreshImage();
        } catch (Exception e) {
            Minecraft.crash(CrashReport.forThrowable(e, ""));
        }
    }

    public SimpleTexture(@Nonnull ResourceLocation location, int uOffset, int vOffset, int uWidth, int vHeight, int color) {
        this(location, uOffset, vOffset, uWidth, vHeight, color, 1.0f);
    }

    public SimpleTexture(@Nonnull ResourceLocation location, int uOffset, int vOffset, int uWidth, int vHeight) {
        this(location, uOffset, vOffset, uWidth, vHeight, COLOR_DEFAULT, 1.0f);
    }

    public SimpleTexture(@Nonnull ResourceLocation location, int uWidth, int vHeight, int color, float alpha) {
        this(location, 0, 0, uWidth, vHeight, color, alpha);
    }

    public SimpleTexture(@Nonnull ResourceLocation location, int uWidth, int vHeight, int color) {
        this(location, 0, 0, uWidth, vHeight, color, 1.0f);
    }

    public SimpleTexture(@Nonnull ResourceLocation location, int uWidth, int vHeight) {
        this(location, 0, 0, uWidth, vHeight, COLOR_DEFAULT, 1.0f);
    }

    public SimpleTexture(@Nonnull ResourceLocation location, int color, float alpha) {
        this(location, 0, 0, 0, 0, color, alpha);
        uWidth = imgWidth;
        vHeight = imgHeight;
        if(KasugaLib.STACKS.isTextureRegistryFired())
            refreshImage();
    }

    public SimpleTexture(@Nonnull ResourceLocation location, int color) {
        this(location, color, 1.0f);
    }

    public SimpleTexture(@Nonnull ResourceLocation location, float alpha) {
        this(location, COLOR_DEFAULT, alpha);
    }

    public SimpleTexture(@Nonnull ResourceLocation location) {
        this(location, COLOR_DEFAULT, 1.0f);
    }

    public SimpleTexture cut(int left, int up, int right, int down) {
        return new SimpleTexture(location, uOffset + left, vOffset + up, uWidth - right - left, vHeight - down - up, color.getRGB(), color.getA());
    }

    public SimpleTexture flipY() {
        return new SimpleTexture(location, uOffset + uWidth, vOffset, - uWidth, vHeight, color.getRGB(), color.getA());
    }

    public SimpleTexture flipX() {
        return new SimpleTexture(location, uOffset, vOffset + vHeight, uWidth, - vHeight, color.getRGB(), color.getA());
    }

    public static SimpleTexture fromByteBuf(FriendlyByteBuf buf) {
        ResourceLocation location1 = buf.readResourceLocation();
        int uOffset = buf.readInt();
        int vOffset = buf.readInt();
        int uWidth = buf.readInt();
        int vHeight = buf.readInt();
        CompoundTag colorTag = buf.readAnySizeNbt();
        SimpleColor color = SimpleColor.fromNbt(colorTag);
        byte[] bytes = buf.readByteArray();
        return new SimpleTexture(location1, new ByteArrayInputStream(bytes), uOffset, vOffset, uWidth, vHeight, color.getRGB(), color.getA());
    }

    public void toByteBuf(FriendlyByteBuf buf) {
        buf.writeResourceLocation(location);
        buf.writeInt(uOffset);
        buf.writeInt(vOffset);
        buf.writeInt(uWidth);
        buf.writeInt(vHeight);
        CompoundTag colorTag = new CompoundTag();
        color.toNbt(colorTag);
        buf.writeNbt(colorTag);
        buf.writeByteArray(bytesOfImage);
    }

    public ResourceLocation getLocation() {
        return location;
    }

    private double aspectRatio() {
        return (double) uWidth / (double) vHeight;
    }

    public double widthHeightRatio() {
        return aspectRatio();
    }

    public double heightWidthRatio() {
        return 1 / aspectRatio();
    }

    public int getFixedHeight(int fromWidth) {
        return (int) ((double)fromWidth / widthHeightRatio());
    }

    public int getFixedWidth(int fromHeight) {
        return (int) ((double) fromHeight * widthHeightRatio());
    }

    public int uOffset() {
        return uOffset;
    }

    public int vOffset() {
        return vOffset;
    }

    public float fuOffset() {
        return (float) uOffset / (float) imgWidth;
    }

    public float fvOffset() {
        return (float) vOffset / (float) imgHeight;
    }

    public float fuWidth() {
        return (float) uWidth / (float) imgWidth;
    }

    public float fvHeight() {
        return (float) vHeight / (float) imgHeight;
    }

    public float fuOffsetR(){
        return fuOffset() + fuWidth();
    }

    public float fvOffsetD() {
        return fvOffset() + fvHeight();
    }

    public int width() {
        return (int) (zoom * uWidth);
    }

    public int height() {
        return (int) (zoom * vHeight);
    }

    public void zoom(float zoom) {
        this.zoom = Math.min(Math.max(zoom, 0), 5);
    }

    public float getAlpha() {
        return color.getA();
    }

    public void setAlpha(float alpha) {
        this.color = color.setA(alpha);
    }

    public SimpleColor getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = SimpleColor.fromRGBAInt(color);
    }

    public void setColor(int r, int g, int b, float a) {
        this.color = color.setR(r).setG(g).setB(b).setA(a);
    }

    public void refreshImage() {
        fuOffset = ((float) uOffset)/((float) imgWidth);
        fvOffset = ((float) vOffset)/((float) imgHeight);
        fuWidth = ((float) uWidth)/((float) imgWidth);
        fvHeight = ((float) vHeight)/((float) imgHeight);
    }

    public void setColor(int r, int g, int b) {
        this.color = color.setR(r).setG(g).setB(b);
    }

    public void render(int x, int y, int width, int height) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, location);
        RenderSystem.setShaderColor(
                color.getfR(), color.getfG(), color.getfB(), color.getA());
        RenderSystem.enableBlend();
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(x, y, 0.0).uv(fuOffset, fvOffset).endVertex();
        buffer.vertex(x, y + height, 0.0).uv(fuOffset, fvOffset + fvHeight).endVertex();
        buffer.vertex(x + width, y + height, 0.0).uv(fuOffset + fuWidth,fvOffset + fvHeight).endVertex();
        buffer.vertex(x + width, y, 0.0).uv(fuOffset + fuWidth, fvOffset).endVertex();
        buffer.end();
        BufferUploader.end(buffer);
        RenderSystem.disableBlend();
    }

    public void renderScaled(int x, int y, int axis, boolean isWidth) {
        if(isWidth) render(x, y, axis, getFixedHeight(axis));
        else render(x, y, getFixedWidth(axis), axis);
    }

    public void renderLazy(int x, int y) {
        render(x, y, (int) (uWidth * zoom), (int) (vHeight * zoom));
    }

    public void renderCenteredLazy(int centerX, int centerY) {
        renderCentered(centerX, centerY, (int) (uWidth * zoom), (int) (vHeight * zoom));
    }

    public void renderCentered(int centerX, int centerY, int width, int height) {
        render(centerX - width/2, centerY - height/2, width, height);
    }

    public void renderCenteredScaled(int centerX, int centerY, int axis, boolean isWidth) {
        if(isWidth)
            renderCentered(centerX, centerY, axis, getFixedHeight(axis));
        else
            renderCentered(centerX, centerY, getFixedWidth(axis), axis);
    }
}
