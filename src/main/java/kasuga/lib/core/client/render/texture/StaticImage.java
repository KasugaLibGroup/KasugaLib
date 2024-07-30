package kasuga.lib.core.client.render.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.annos.Inner;
import kasuga.lib.core.client.render.SimpleColor;
import kasuga.lib.core.util.Resources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import java.util.function.Supplier;

public class StaticImage {

    public static final HashMap<ResourceLocation, Supplier<StaticImage>> STACK = new HashMap<>();
    public static final HashSet<StaticImageHolder> HOLDERS = new HashSet<>();
    public final ResourceLocation id;
    public final BufferedImage image;
    public final byte[] rawData;

    private StaticImage(ResourceLocation id) throws IOException {
        this.id = id;
        Resource resource = Resources.getResource(id);
        this.rawData = resource.open().readAllBytes();
        ByteArrayInputStream stream = new ByteArrayInputStream(rawData);
        image = ImageIO.read(stream);
    }

    private StaticImage(ResourceLocation id, InputStream inputStream) throws IOException {
        this.id = id;
        this.rawData = inputStream.readAllBytes();
        ByteArrayInputStream stream = new ByteArrayInputStream(rawData);
        this.image = ImageIO.read(stream);
        stream.close();
    }

    private StaticImage(ResourceLocation id, byte[] bytes) throws IOException {
        this.id = id;
        this.rawData = bytes;
        ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
        image = ImageIO.read(stream);
    }

    @Inner
    public static Supplier<StaticImage> createImage(FriendlyByteBuf buf) throws IOException {
        ResourceLocation location = buf.readResourceLocation();
        byte[] bytes = buf.readByteArray();
        return createImage(location, new ByteArrayInputStream(bytes));
    }

    @Inner
    public static Supplier<StaticImage> createImage(CompoundTag nbt) throws IOException {
        return createImage(
                new ResourceLocation(nbt.getString("id_namespace"), nbt.getString("id_path")),
                new ByteArrayInputStream(nbt.getByteArray("data")));
    }

    @Inner
    public static Supplier<StaticImage> createImage(ResourceLocation id, InputStream inputStream) throws IOException {
        if (STACK.containsKey(id)) return STACK.get(id);
        Resources.CheatResourceLocation cheat = Resources.CheatResourceLocation.copy(id);
        StaticImage image = new StaticImage(cheat, inputStream);
        ByteArrayInputStream stream = new ByteArrayInputStream(image.rawData);
        registerImageToMc(cheat, stream);
        stream.close();
        STACK.put(image.id, () -> image);
        return () -> image;
    }

    @Inner
    public static Supplier<StaticImage> createImage(ResourceLocation id) throws IOException {
        if (STACK.containsKey(id)) return STACK.get(id);
        StaticImage image1 = new StaticImage(id);
        ByteArrayInputStream stream = new ByteArrayInputStream(image1.rawData);
        registerLocalImageToMc(image1.id, stream);
        stream.close();
        return () -> image1;
    }


    private static DynamicTexture registerLocalImageToMc(ResourceLocation id, InputStream stream) throws IOException {
        NativeImage image = NativeImage.read(stream);
        DynamicTexture texture = new DynamicTexture(image);
        Minecraft.getInstance().textureManager.register(id, texture);
        return texture;
    }

    private static DynamicTexture registerImageToMc(Resources.CheatResourceLocation id, InputStream stream) throws IOException {
        if (!KasugaLib.STACKS.isTextureRegistryFired()) {

        }
        NativeImage image = NativeImage.read(stream);
        DynamicTexture texture = new DynamicTexture(image);
        Minecraft.getInstance().textureManager.register(id, texture);
        return texture;
    }

    public void serialize(FriendlyByteBuf buf) {
        buf.writeResourceLocation(this.id);
        buf.writeByteArray(this.rawData);
    }

    public void serialize(CompoundTag nbt) {
        nbt.putString("id_namespace", id.getNamespace());
        nbt.putString("id_path", id.getPath());
        nbt.putByteArray("data", rawData);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(rawData);
    }

    public void renderToGui(Vector3f leftTop, Vector3f rightTop, Vector3f leftDown, Vector3f rightDown,
                       Vec2f uvLeftTop, Vec2f uvRightTop, Vec2f uvLeftDown, Vec2f uvRightDown,
                       SimpleColor color) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, id);
        RenderSystem.setShaderColor(
                color.getfR(), color.getfG(), color.getfB(), color.getA());
        RenderSystem.enableBlend();
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(leftTop.x(), leftTop.y(), leftDown.z()).uv(uvLeftTop.x(), uvLeftTop.y()).endVertex();
        buffer.vertex(leftDown.x(), leftDown.y(), leftDown.z()).uv(uvLeftDown.x(), uvLeftDown.y()).endVertex();
        buffer.vertex(rightDown.x(), rightDown.y(), rightDown.z()).uv(uvRightDown.x(),uvRightDown.y()).endVertex();
        buffer.vertex(rightTop.x(), rightTop.y(), rightTop.z()).uv(uvRightTop.x(), uvRightTop.y()).endVertex();
        BufferUploader.drawWithShader(buffer.end());
        RenderSystem.disableBlend();
    }

    public void renderToGui(ImageMask mask) {
        renderToGui(mask.getLeftTop(), mask.getRightTop(), mask.getLeftDown(), mask.getRightDown(),
                mask.getUvLeftTop(), mask.getUvRightTop(), mask.getUvLeftDown(), mask.getUvRightDown(),
                mask.getColor());
    }

    public void renderToWorld(PoseStack pose, MultiBufferSource buffer, RenderType type,
                              Vector3f leftTop, Vector3f rightTop, Vector3f leftDown, Vector3f rightDown,
                              Vec2f uvLeftTop, Vec2f uvRightTop, Vec2f uvLeftDown, Vec2f uvRightDown,
                              SimpleColor color, boolean reverse, int light) {
        boolean shouldPush = !pose.clear();
        Matrix4f lastMatrix = null;
        if(shouldPush) {
            pose.pushPose();
        } else {
            lastMatrix = pose.last().pose();
            pose.popPose();
            pose.pushPose();
        }
        if (!reverse)
            pose.scale(1.0f, -1.0f, 1.0f);
        pose.translate(.5f, 0, .5f);
        Matrix4f matrix = pose.last().pose();
        VertexConsumer consumer = buffer.getBuffer(type);
        buildVertex(consumer, matrix, leftTop, uvLeftTop, color, light);
        buildVertex(consumer, matrix, rightTop, uvRightTop, color, light);
        buildVertex(consumer, matrix, rightDown, uvRightDown, color, light);
        buildVertex(consumer, matrix, leftDown, uvLeftDown, color, light);
        pose.popPose();
        if(!shouldPush) {
            pose.pushPose();
            pose.mulPoseMatrix(lastMatrix);
        }
    }

    public void renderToWorld(PoseStack pose, MultiBufferSource buffer, RenderType type,
                              ImageMask mask, boolean reverse, int light) {
        renderToWorld(pose, buffer, type,
                mask.getLeftTop(), mask.getRightTop(), mask.getLeftDown(), mask.getRightDown(),
                mask.getUvLeftTop(), mask.getUvRightTop(), mask.getUvLeftDown(), mask.getUvRightDown(),
                mask.getColor(), reverse, light);
    }

    private void buildVertex(VertexConsumer consumer, Matrix4f matrix,
                             Vector3f pos, Vec2f uv, SimpleColor color, int light) {
        consumer.vertex(matrix, pos.x(), pos.y(), pos.z())
                .color(color.getfR(), color.getfG(), color.getfB(), color.getA())
                .uv(uv.x(), uv.y())
                .uv2(light)
                .endVertex();
    }

    public ImageMask getMask() {
        return new ImageMask(this);
    }

    public NineSlicedImageMask getNineSlicedMask() {return new NineSlicedImageMask(this);}

    public int width() {
        return image.getWidth();
    }

    public int height() {
        return image.getHeight();
    }
}
