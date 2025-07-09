package kasuga.lib.core.client.frontend.gui.nodes;

import com.caoccao.javet.annotations.V8Convert;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.client.frontend.common.layouting.LayoutBox;
import kasuga.lib.core.client.frontend.common.layouting.LayoutContext;
import kasuga.lib.core.client.frontend.common.layouting.LayoutNode;
import kasuga.lib.core.client.frontend.common.style.StyleAttributeProxy;
import kasuga.lib.core.client.frontend.common.style.StyleList;
import kasuga.lib.core.client.frontend.common.style.StyleTarget;
import kasuga.lib.core.client.frontend.dom.nodes.DomNode;
import kasuga.lib.core.client.frontend.font.ExtendableProperty;
import kasuga.lib.core.client.frontend.gui.GuiContext;
import kasuga.lib.core.client.frontend.gui.events.mouse.MouseDownEvent;
import kasuga.lib.core.client.frontend.gui.events.mouse.MouseEvent;
import kasuga.lib.core.client.frontend.gui.events.mouse.MouseUpEvent;
import kasuga.lib.core.client.frontend.gui.layout.EdgeSize2D;
import kasuga.lib.core.client.frontend.rendering.BackgroundRenderer;
import kasuga.lib.core.client.frontend.rendering.RenderContext;
import kasuga.lib.core.javascript.engine.annotations.HostAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.common.util.Lazy;

import java.util.*;

@V8Convert()
public class GuiDomNode extends DomNode<GuiContext> {

    @HostAccess.Export
    public StyleList<StyleTarget> styles = new StyleList<>(KasugaLib.STACKS.GUI.orElseThrow(IllegalStateException::new).styleRegistry);


    public ExtendableProperty<Integer> fontSize = new ExtendableProperty<>(
            ()->parent instanceof GuiDomNode node ? node.fontSize.get() : Minecraft.getInstance().font.lineHeight,
            ()->{
                children.forEach((c)->{
                    if(c instanceof GuiDomNode child)
                        child.fontSize.notifyUpdate();
                });
                this.fontSizeUpdated();
            }
    );
    private Integer zIndex = 0;

    protected void fontSizeUpdated() {

    }

    public GuiDomNode(GuiContext context){
        super(context);
        attributes.registerProxy("style",new StyleAttributeProxy(styles));
        styles.setCallback(this::onStyleUpdate);
    }

    private void onStyleUpdate() {
        if(parent != null && parent instanceof GuiDomNode parentNode){
            parentNode.styles.notifyUpdate();
        }
    }

    public StyleList<StyleTarget> getStyle() {
        return styles;
    }

    Lazy<LayoutContext<?,GuiDomNode>> layoutManager = Lazy.concurrentOf(()->new LayoutContext<>(this, this.domContext.getLayoutEngine()));

    public LayoutContext<?,GuiDomNode> getLayoutManager() {
        return layoutManager.get();
    }

    protected BackgroundRenderer background = new BackgroundRenderer();

    @HostAccess.Export
    @Override
    public boolean addChildBefore(DomNode<GuiContext> child, DomNode<GuiContext> before) {
        this.domContext.queueDuringRender(()->{
            int index = children.indexOf(before);
            if(index == -1)
                return;
            addChildInstant(index, child);
        });
        return true;
    }

    @HostAccess.Export
    @Override
    public boolean addChildAt(int i, DomNode<GuiContext> child) {
        this.domContext.queueDuringRender(()->{
            addChildInstant(i, child);
        });
        return true;
    }

    protected void addChildInstant(int i, DomNode<GuiContext> child){
        if(child instanceof GuiDomNode domNode)
            getLayoutManager().addChild(i,domNode.getLayoutManager());
        styles.notifyUpdate();
        super.addChildAt(i,child);
    }

    @HostAccess.Export
    @Override
    public boolean removeChild(DomNode<GuiContext> child) {
        this.domContext.queueDuringRender(()-> {
            boolean result = super.removeChild(child);
            if (child instanceof GuiDomNode domNode) {
                getLayoutManager().removeChild(domNode.getLayoutManager());
            }
            styles.notifyUpdate();
            super.removeChild(child);
        });
        return super.children.contains(child);
    }

    @HostAccess.Export
    @Override
    public boolean addChild(DomNode<GuiContext> child) {
        this.domContext.queueDuringRender(()-> {
            addChildInstant(children.size(), child);
        });
        return true;
    }

    public void render(Object source, RenderContext context){
        renderNode(source, context);
    }

    public float renderNode(Object source, RenderContext context) {
        if(!this.getLayoutManager().hasSource(source))
            return 0;

        this.updateStyles();

        LayoutNode layout = this.getLayoutManager()
                .getSourceNode(source);

        LayoutBox coordinate = layout.getPosition();
        background.render(context,coordinate.x,coordinate.y,coordinate.width,coordinate.height);

        EdgeSize2D border = layout.getBorder();
        MultiBufferSource bufferSource = context.getBufferSource();
        // @todo render border
        HashMap<Integer, ArrayList<DomNode<GuiContext>>> childrenDepthMap = new HashMap<>();

        for (DomNode<GuiContext> child : this.children) {
            childrenDepthMap.computeIfAbsent(((GuiDomNode)child).zIndex, (i)->new ArrayList<>()).add(child);
        }

        List<Integer> zIndexList = new ArrayList<>(childrenDepthMap.keySet().stream().toList());

        zIndexList.sort(Integer::compareTo);

        float totalDepth = 0.003f;
        context.pose().translate(0,0,0.003);

        for(Integer zIndex : zIndexList){
            ArrayList<DomNode<GuiContext>> zList = childrenDepthMap.get(zIndex);
            float maxiumDepth = 0;
            for (DomNode<GuiContext> guiContextDomNode : zList) {
                maxiumDepth = Math.max(
                        maxiumDepth,
                        ((GuiDomNode)guiContextDomNode).renderNode(source, context)
                );
            }
            context.pose().translate(0,0,maxiumDepth + 0.003);
            totalDepth += maxiumDepth + 0.003;
        }
        return totalDepth;
    }

    private void updateStyles() {
        if(!styles.hasNewStyle(this))
            return;

        styles.forEachCacheStyle((style)->{
            style.getTarget().attemptApply(this);
        });

        styles.resetNewStyle(this);
    }

    @Override
    public void close() {
        super.close();
        getLayoutManager().close();
    }

    @Override
    @HostAccess.Export
    public boolean hasFeature(String feature) {
        if(Objects.equals(feature, "style"))
            return true;
        return super.hasFeature(feature);
    }

    public BackgroundRenderer getBackgroundRenderer() {
        return background;
    }

    public boolean onMouseEvent(Object source,MouseEvent event){
        // 1. Find the target children
        // 2. Dispatch the event to the children
        // 3. If the event is not stopped, dispatch the event to the parent

        if(event instanceof MouseUpEvent) {
            domContext.removeActivateElement();
        }

        LayoutContext<?,GuiDomNode> layout = getLayoutManager();
        if(!layout.hasSource(source)){
            return false; // Source already unloaded
        }
        LayoutNode sourceNode = layout.getSourceNode(source);
        if(sourceNode == null)
            return false;
        LayoutBox box = sourceNode.getRelative();
        if(!box.contains(event.getOffsetPosition().x,event.getOffsetPosition().y))
            return false;

        LayoutBox screen = sourceNode.getPosition();

        MouseEvent translated = event.forkChild(
                this,
                event.getScreenPosition().x - (int)screen.x,
                event.getScreenPosition().y - (int)screen.y
        );

        GuiDomNode target = this;

        for (DomNode<GuiContext> child : children) {
            if (child instanceof GuiDomNode domNode) {
                if (domNode.onMouseEvent(source, translated)) {
                    target = domNode;
                    break;
                }
            }
        }

        MouseEvent finalEvent = event.withTarget(target);

        if(!translated.isPropagationStopped()){
            this.dispatchEvent(event.getType(), finalEvent);
        }

        if(target == this && event instanceof MouseDownEvent) {
            this.domContext.setActivateElement(target);
        }

        return true;
    }

    public void setZIndex(Integer zIndex) {
        this.zIndex = zIndex;
    }
}
