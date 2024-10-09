package kasuga.lib.core.client.frontend.gui.canvas;

import kasuga.lib.core.client.frontend.gui.canvas.glfw.CanvasRenderer;

import java.util.ArrayList;

public class CanvasManager {
    ArrayList<CanvasRenderer> canvasList = new ArrayList<>();

    public void add(CanvasRenderer canvas){
        this.canvasList.add(canvas);
    }

    public void remove(CanvasRenderer canvas){
        this.canvasList.remove(canvas);
    }

    public CanvasRenderer create(int width, int height){
        CanvasRenderer renderer = new CanvasRenderer(this, width, height);
        this.add(renderer);
        return renderer;
    }
}
