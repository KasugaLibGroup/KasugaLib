package kasuga.lib.core.client.render.texture;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import kasuga.lib.core.client.frontend.rendering.RenderContext;
import kasuga.lib.core.client.render.PoseContext;
import kasuga.lib.core.client.render.SimpleColor;
import kasuga.lib.core.util.LazyRecomputable;
import kasuga.lib.core.util.Resources;
import kasuga.lib.core.util.data_type.Pair;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Text;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.function.Supplier;

public class Texture extends GraphicsImage<Texture> {
    @Nullable
    protected ResourceLocation location;
    @Nullable
    protected BufferedImage image;
    @Nullable
    protected byte[] rawImage;

    protected SimpleColor color;

    Pair<RenderTypeBuilder, RenderType> renderTypeCache;
    protected Texture(
            @Nullable ResourceLocation location,
            @Nullable BufferedImage image,
            @Nullable byte[] rawImage,
            float uOffset, float vOffset, float uWidth, float vHeight,
            float imageWidth, float imageHeight,
            SimpleColor color) {
        super(uOffset, vOffset, uWidth, vHeight, imageWidth, imageHeight);
        this.location = location;
        this.color = color;
        this.image = image;
        updateRawImage(rawImage, false);
        readImageInfo();
        setUV(uOffset, vOffset, uWidth, vHeight);
    }

    protected void updateRawImage(byte[] rawImage, boolean force){
        if(this.rawImage == rawImage && !force){
            return;
        }
        if(rawImage != null){
            this.rawImage = rawImage;
            return;
        }
        if(this.location == null || Objects.equals(this.location.getNamespace(), "kasuga_lib_tempory")){
            throw new IllegalStateException("Cannot initilize image with both no RawImage and ResourceLocation");
        }
        this.rawImage = getRawImageFromLocation(this.location);
        updateImage(null, force);
    }

    protected void updateImage(BufferedImage image, boolean force) {
        if(this.image != null && !force){
            return;
        }
        if(image != null){
            this.image = image;
            return;
        }
        if(this.rawImage == null  || Objects.equals(this.location.getNamespace(), "kasuga_lib_tempory")){
            throw new IllegalStateException("Cannot initilize image with both no RawImage and ResourceLocation");
        }
        try(ByteArrayInputStream is = new ByteArrayInputStream(rawImage)){
            this.image = ImageIO.read(is);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    protected static byte[] getRawImageFromLocation(ResourceLocation location){
        try{
            Resource resource = Resources.getResource(location);
            InputStream stream = resource.open();
            return stream.readAllBytes();
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }


    @Override
    public Texture cloneTexture() {
        return new Texture(
                location,
                image,
                rawImage,
                uOffset,
                vOffset,
                uWidth,
                vHeight,
                imageWidth,
                imageHeight,
                color
        );
    }

    public void render(float x, float y, float width, float height){
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, location);
        RenderSystem.setShaderColor(color.getfR(), color.getfG(), color.getfB(), color.getA());
        RenderSystem.enableBlend();
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(x, y, 0.0).uv(uOffsetUVCache, vOffsetUVCache).endVertex();
        buffer.vertex(x, y + height, 0.0).uv(uOffsetUVCache, vOffsetUVCache + vHeightUVCache).endVertex();
        buffer.vertex(x + width, y + height, 0.0).uv(uOffsetUVCache + uWidthUVCache,vOffsetUVCache + vHeightUVCache).endVertex();
        buffer.vertex(x + width, y, 0.0).uv(uOffsetUVCache + uWidthUVCache, vOffsetUVCache).endVertex();
        BufferUploader.drawWithShader(buffer.end());
        RenderSystem.disableBlend();
    }

    protected void readImageInfo() {
        this.imageWidth = image.getWidth();
        this.imageHeight = image.getHeight();
    }

    protected void buildVertex(VertexConsumer consumer, Matrix4f matrix,
                             float x, float y, float z, float u, float v, SimpleColor color, int light) {
        consumer.vertex(matrix, x, y, z)
                .color(color.getfR(), color.getfG(), color.getfB(), color.getA())
                .uv(u, v)
                .uv2(light)
                .endVertex();
    }


    public void render(RenderTypeBuilder builder, PoseStack pose, MultiBufferSource buffer, float width, float height, int light) {
        if (builder == null) return;
        if (renderTypeCache == null || renderTypeCache.getFirst() != builder)
            renderTypeCache = Pair.of(builder, builder.build(location));
        boolean shouldPush = !pose.clear();
        Matrix4f lastMatrix = null;
        if (shouldPush) {
            pose.pushPose();
        } else {
            lastMatrix = pose.last().pose();
            pose.popPose();
            pose.pushPose();
        }
        pose.scale(1.0f, -1.0f, 1.0f);
        pose.translate(.5f, 0, .5f);
        poseContext.apply(pose);
        Matrix4f matrix = pose.last().pose();
        VertexConsumer consumer = buffer.getBuffer(renderTypeCache.getSecond());
        buildVertex(consumer, matrix, 0, height, 0, uOffsetUVCache, vOffsetUVCache + vHeightUVCache, color, light);
        buildVertex(consumer, matrix, width, height, 0, uOffsetUVCache + uWidthUVCache, vOffsetUVCache + vHeightUVCache, color, light);
        buildVertex(consumer, matrix, width, 0, 0, uOffsetUVCache + uWidthUVCache, vOffsetUVCache, color, light);
        buildVertex(consumer, matrix, 0, 0, 0, uOffsetUVCache, vOffsetUVCache, color, light);
        pose.popPose();
        if (!shouldPush) {
            pose.pushPose();
            pose.mulPoseMatrix(lastMatrix);
        }
    }
}
