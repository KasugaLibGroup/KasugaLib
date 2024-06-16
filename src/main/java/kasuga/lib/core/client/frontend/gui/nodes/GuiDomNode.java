package kasuga.lib.core.client.frontend.gui.nodes;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.client.frontend.common.layouting.LayoutBox;
import kasuga.lib.core.client.frontend.common.layouting.LayoutContext;
import kasuga.lib.core.client.frontend.common.layouting.LayoutNode;
import kasuga.lib.core.client.frontend.common.style.StyleAttributeProxy;
import kasuga.lib.core.client.frontend.common.style.StyleList;
import kasuga.lib.core.client.frontend.common.style.StyleTarget;
import kasuga.lib.core.client.frontend.dom.nodes.DomNode;
import kasuga.lib.core.client.frontend.gui.GuiContext;
import kasuga.lib.core.client.frontend.gui.layout.EdgeSize2D;
import kasuga.lib.core.client.frontend.rendering.BackgroundRenderer;
import kasuga.lib.core.client.frontend.rendering.RenderContext;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;
import org.graalvm.polyglot.HostAccess;

import java.util.Objects;

public class GuiDomNode extends DomNode<GuiContext> {

    @HostAccess.Export
    public StyleList<StyleTarget> styles = new StyleList<>(KasugaLib.STACKS.GUI.styleRegistry);

    GuiDomNode(GuiContext context){
        super(context);
        attributes.registerProxy("style",new StyleAttributeProxy(styles));
    }

    public StyleList<StyleTarget> getStyle() {
        return styles;
    }

    Lazy<LayoutContext<?,GuiDomNode>> layoutManager = Lazy.concurrentOf(()->new LayoutContext<>(this,this.domContext.getLayoutEngine()));

    public LayoutContext<?,GuiDomNode> getLayoutManager() {
        return layoutManager.get();
    }

    protected LazyOptional<BackgroundRenderer> background = LazyOptional.empty();

    @HostAccess.Export
    @Override
    public boolean addChildAt(int i, DomNode<GuiContext> child) {
        if(child instanceof GuiDomNode domNode)
            getLayoutManager().addChild(i,domNode.getLayoutManager());
        return super.addChildAt(i,child);
    }

    @HostAccess.Export
    @Override
    public boolean removeChild(DomNode<GuiContext> child) {
        boolean result = super.removeChild(child);
        if(child instanceof GuiDomNode domNode)
            getLayoutManager().removeChild(domNode.getLayoutManager());
        return result;
    }

    @Override
    public void render(Object source, RenderContext context) {
        super.render(source, context);

        LayoutNode layout = this.getLayoutManager()
                .getSourceNode(source);

        LayoutBox coordinate = layout.getPosition();
        background.ifPresent((bg)->bg.render(context,(int)coordinate.x,(int)coordinate.y,(int)coordinate.width,(int)coordinate.height));

        EdgeSize2D border = layout.getBorder();
        MultiBufferSource bufferSource = context.getBufferSource();
        // @todo render border


    }

    @Override
    public void close() {
        super.close();
        getLayoutManager().close();
    }
}
