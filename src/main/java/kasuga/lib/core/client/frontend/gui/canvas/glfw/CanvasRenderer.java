package kasuga.lib.core.client.frontend.gui.canvas.glfw;

import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import kasuga.lib.core.util.Callback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import org.joml.Matrix4f;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

import static net.minecraft.client.Minecraft.ON_OSX;

public class CanvasRenderer {
    RenderBuffers renderBuffer = Minecraft.getInstance().renderBuffers();

    TextureTarget target = new TextureTarget(256,256,true, ON_OSX);

    Queue<Callback> taskQueue = new ArrayDeque<>();


    private AtomicBoolean shouldClear;

    public void init(){
        target.createBuffers(256,256,false);
        target.setClearColor(0,1,0,1);
        target.clear(true);
    }
    public void render(){
        if(this.shouldClear.get())
            target.clear(true);

        RenderSystem.clear(16640, ON_OSX);
        target.bindWrite(true);
        RenderSystem.enableBlend();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();


        Matrix4f matrix4f = orthographic(0.0F, 256F, 0.0F, 256F, 1000.0F, 3000.0F);
        // TODO: Needed to be checked.
        RenderSystem.setProjectionMatrix(matrix4f, RenderSystem.getVertexSorting());
        PoseStack poseStack = RenderSystem.getModelViewStack();
        poseStack.setIdentity();
        poseStack.translate(0.0, 0.0, (double)(1000.0F - 3000.0F));
        RenderSystem.applyModelViewMatrix();
        Lighting.setupFor3DItems();
        actualRender();
        target.unbindWrite();
        RenderSystem.disableBlend();
        RenderSystem.disableBlend();
        RenderSystem.clear(16640, ON_OSX);
    }

    public void renderToBuffer(MultiBufferSource.BufferSource bufferSource, int x, int y, int width, int height){
        VertexConsumer consumer = bufferSource.getBuffer(CanvasRenderType.CANVAS.apply(target.getColorTextureId()));
        consumer.vertex(x, y + height, 0).uv(0, 1).endVertex();
        consumer.vertex(x + width, y + height, 0).uv(1, 1).endVertex();
        consumer.vertex(x + width, y, 0).uv(1, 0).endVertex();
        consumer.vertex(x, y, 0).uv(0, 0).endVertex();
        bufferSource.endBatch();
    }


    public void renderToScreen(int x,int y,int width,int height){
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, target.getColorTextureId());
        RenderSystem.setShaderColor(1,1,1,1);
        RenderSystem.enableBlend();
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(x, y, 0.0).uv(0, 1).endVertex();
        buffer.vertex(x, y + height, 0.0).uv(0, 0).endVertex();
        buffer.vertex(x + width, y + height, 0.0).uv(1,0).endVertex();
        buffer.vertex(x + width, y, 0.0).uv(1,1).endVertex();

        BufferUploader.drawWithShader(buffer.end());
        RenderSystem.disableBlend();
    }

    private void actualRender() {
        MultiBufferSource.BufferSource bufferSource = renderBuffer.bufferSource();
        while(!taskQueue.isEmpty()){
            taskQueue.poll().execute();
        }
        bufferSource.endBatch();
    }

    public void close(){
        target.destroyBuffers();
    }

    public void pushTask(Callback drawFunction) {
        taskQueue.add(drawFunction);
    }

    public static Matrix4f orthographic(float pMinX, float pMaxX, float pMinY, float pMaxY, float pMinZ, float pMaxZ) {
        Matrix4f matrix4f = new Matrix4f();
        float f = pMaxX - pMinX;
        float f1 = pMinY - pMaxY;
        float f2 = pMaxZ - pMinZ;
        matrix4f.m00(2.0F / f);
        matrix4f.m11(2.0F / f1);
        matrix4f.m22(-2.0F / f2);
        matrix4f.m03(-(pMaxX + pMinX) / f);
        matrix4f.m13(-(pMinY + pMaxY) / f1);
        matrix4f.m23(-(pMaxZ + pMinZ) / f2);
        matrix4f.m33(1.0F);
        return matrix4f;
    }
}
