package kasuga.lib.core.client.frontend.gui.canvas;

import com.mojang.blaze3d.vertex.VertexConsumer;
import kasuga.lib.core.client.frontend.gui.canvas.glfw.CanvasRenderer;
import kasuga.lib.core.util.Callback;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

import java.util.function.Consumer;

public class CanvasRenderingContext2D {
    CanvasRenderer canvasRenderer;
    Point2D current = Point2D.ZERO;
    float lineWidth = 0.0F;

    public void moveTo(float x, float y){
        current = new Point2D(x, y);
    }

    public void lineTo(float x, float y){
        Point2D next = new Point2D(x, y);
        this.draw(()->{
            DrawTool.drawLine(current.x(), current.y(), next.x(), next.y(), lineWidth, this::strokeVertexProperty);
        });
        current = next;
    }

    public void fillRect(float x, float y, float width, float height){
        this.draw(()->{
            DrawTool.drawRect(x, y, width, height, this::fillVertexProperty);
            DrawTool.drawLine(x, y, x + width, y, lineWidth, this::strokeVertexProperty);
            DrawTool.drawLine(x + width, y, x + width, y + height, lineWidth, this::strokeVertexProperty);
            DrawTool.drawLine(x + width, y + height, x, y + height, lineWidth, this::strokeVertexProperty);
            DrawTool.drawLine(x, y + height, x, y, lineWidth, this::strokeVertexProperty);});
    }

    public void clearRect(float x, float y, float width, float height){
        this.draw(()->{
            DrawTool.drawRect(x, y, width, height, this::clearColorProperty);
        });
    }

    private void draw(Callback drawFunction){
        this.canvasRenderer.pushTask(drawFunction);
    }

    private void strokeVertexProperty(VertexConsumer consumer, Integer integer) {
        consumer.color(0.0F, 0.0F, 0.0F, 1.0F);
    }

    private void clearColorProperty(VertexConsumer consumer, Integer integer) {
        consumer.color(0.0F, 0.0F, 0.0F, 0.0F);
    }

    private void fillVertexProperty(VertexConsumer consumer, Integer integer) {
        consumer.color(1.0F, 0.0F, 0.0F, 1.0F);
    }


}
