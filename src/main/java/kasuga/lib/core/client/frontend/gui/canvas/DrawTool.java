package kasuga.lib.core.client.frontend.gui.canvas;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.GameRenderer;

import java.util.function.BiConsumer;

public class DrawTool {
    public static void drawLine(float x1, float y1, float x2, float y2, float lineWidth, BiConsumer<VertexConsumer,Integer> vertexProperty) {
        float deltaX = x2 - x1;
        float deltaY = y2 - y1;

        float length = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        if(length == 0)
            return;

        float kX = deltaY / length;
        float kY = -deltaX / length;

        float rightUpX = x1 + kX * lineWidth;
        float rightUpY = y1 + kY * lineWidth;
        float rightDownX = x2 + kX * lineWidth;
        float rightDownY = y2 + kY * lineWidth;
        float leftDownX = x1 - kX * lineWidth;
        float leftDownY = y1 - kY * lineWidth;
        float leftUpX = x2 - kX * lineWidth;
        float leftUpY = y2 - kY * lineWidth;

        drawTetragon(rightUpX, rightUpY, rightDownX, rightDownY, leftDownX, leftDownY, leftUpX, leftUpY, vertexProperty);
    }

    public static void drawRect(float x, float y, float width, float height, BiConsumer<VertexConsumer,Integer> vertexProperty) {
        drawTetragon(x, y, x + width, y, x + width, y + height, x, y + height, vertexProperty);
    }

    public static void drawTetragon(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4, BiConsumer<VertexConsumer,Integer> vertexProperty) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableBlend();
        BufferBuilder vertexConsumer = Tesselator.getInstance().getBuilder();

        vertexConsumer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        vertexConsumer.vertex(x1, y1, 0).endVertex();
        vertexProperty.accept(vertexConsumer,0);
        vertexConsumer.endVertex();

        vertexConsumer.vertex(x2, y2, 0).endVertex();
        vertexProperty.accept(vertexConsumer,1);
        vertexConsumer.endVertex();

        vertexConsumer.vertex(x3, y3, 0).endVertex();
        vertexProperty.accept(vertexConsumer,2);
        vertexConsumer.endVertex();

        vertexConsumer.vertex(x4, y4, 0).endVertex();
        vertexProperty.accept(vertexConsumer,3);
        vertexConsumer.endVertex();

        vertexConsumer.end();
        BufferUploader.end(vertexConsumer);
        RenderSystem.disableBlend();

    }
}
