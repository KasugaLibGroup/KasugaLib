package kasuga.lib.core.client.frontend.gui.nodes;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.client.frontend.common.layouting.LayoutBox;
import kasuga.lib.core.client.frontend.common.layouting.LayoutContext;
import kasuga.lib.core.client.frontend.common.layouting.LayoutNode;
import kasuga.lib.core.client.frontend.gui.GuiContext;
import kasuga.lib.core.client.frontend.gui.canvas.CanvasRenderingContext2D;
import kasuga.lib.core.client.frontend.gui.canvas.glfw.CanvasRenderer;
import kasuga.lib.core.client.frontend.rendering.RenderContext;
import org.graalvm.polyglot.HostAccess;

import java.util.Map;

public class GuiCanvasNode extends GuiDomNode{

    CanvasRenderer renderer;

    GuiCanvasNode(GuiContext context) {
        super(context);
    }

    @Override
    public void render(Object source, RenderContext context) {
        if(!this.getLayoutManager().hasSource(source))
            return;

        LayoutNode layout = this.getLayoutManager().getSourceNode(source);
        LayoutBox positionBox = layout.getPosition();

        String realWidthStr = this.attributes.get("width");
        String realHeightStr = this.attributes.get("height");
        int realWidth = -1;
        int realHeight = -1;
        try {
            realWidth = Integer.parseInt(realWidthStr);
            realHeight = Integer.parseInt(realHeightStr);
        }catch (NumberFormatException e){}
        if(realHeight <= 0) realHeight = (int)Math.floor(positionBox.getHeight());
        if(realWidth <= 0) realWidth = (int)Math.floor(positionBox.getHeight());

        if(renderer == null){
            renderer = KasugaLib.STACKS.GUI.orElseThrow().canvasManager.create(
                    realHeight,
                    realWidth
            );
        }

        super.render(source, context);

        renderer.renderToScreen(positionBox.x, positionBox.y, positionBox.width, positionBox.height);
    }

    @Override
    public void close() {
        super.close();
    }

    private Map<String, Object> contextCache;

    @HostAccess.Export
    public Object getContext(String contextType){
        switch (contextType){
            case "2d":
                return contextCache.computeIfAbsent("2d", (i)->new CanvasRenderingContext2D(renderer));
        }
        throw new IllegalArgumentException("Unsupported Canvas Context Type:");
    }
}
