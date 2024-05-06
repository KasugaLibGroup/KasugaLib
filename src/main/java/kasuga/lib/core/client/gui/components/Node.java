package kasuga.lib.core.client.gui.components;

import kasuga.lib.core.client.gui.context.MouseEvent;
import kasuga.lib.core.client.gui.context.RenderContext;
import kasuga.lib.core.client.gui.layout.yoga.YogaMeasureFunction;
import kasuga.lib.core.client.gui.layout.yoga.YogaNode;
import kasuga.lib.core.client.gui.render.BackgroundRender;
import kasuga.lib.core.client.gui.style.StyleList;
import kasuga.lib.core.client.render.texture.SimpleTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

public class Node implements GuiComponent{
    MultipleLocator locator;
    List<Node> children;
    Node parent;
    BackgroundRender background = new BackgroundRender();
    StyleList styles = new StyleList();
    boolean isClosed = false;

    public Node(){
        children = new ArrayList<>();
        YogaMeasureFunction measureFunction = measure();
        locator = new MultipleLocator(measureFunction);
    }

    public void insertChild(int index,Node node){
        this.locator.addChildrenAt(index,node.getLocator());
        this.children.add(index,node);
        node.parent = this;
    }

    public MultipleLocator getLocator() {
        return locator;
    }

    public void addChild(Node node){
        int index = this.children.size();
        this.insertChild(index,node);
    }

    public void removeChild(int index){
        this.locator.removeChildAt(index);
        this.children.remove(index).parent = null;
    }

    public void removeChild(Node node){
        int index = this.children.indexOf(node);
        if(index == -1)
            return;
        this.removeChild(index);
    }

    public void markMeasureDirty(){
        if(this.isClosed)
            return;
        this.locator.markDirty();
        this.locator.markAllCacheDirty();
    }

    public void checkShouldReLayout(Object source){
        if(this.isClosed || !this.locator.hasNewLayout(source))
            return;

        this.locator.visited(source);

        this.locator.markCacheDirty(source);

        for (Node child : children) {
            child.checkShouldReLayout(source);
        }
    }

    public YogaMeasureFunction measure(){
        return null;
    }

    public void renderPreTick(Object source){
        this.locator.updateCache(source);

        for (Node child : children) {
            child.renderPreTick(source);
        }
    }

    @Override
    public void dispatchRender(RenderContext context) {
        if(isClosed)
            return;
        CalculatedPositionCache positionCache = this.locator.getPosition(context.getSource());
        if(positionCache == null)
            return;
        render(context,positionCache);
        context.pushZStack();
        for (Node child : this.children) {
            child.dispatchRender(context);
        }
        context.popZStack();
    }

    public void applyStyles(){
        if(this.isClosed)
            return;
        styles.apply(this);
        for (Node child : this.children) {
            child.applyStyles();
        }
    }

    public void render(RenderContext context,CalculatedPositionCache positionCache){
        background.render(context,(int)positionCache.x,(int)positionCache.y,(int)positionCache.width,(int)positionCache.height);
        if(Minecraft.getInstance().options.renderDebug)
            Minecraft.getInstance().font.draw(context.pose(),String.format("x=%.1f,y=%.1f,w=%.1f,h=%.1f",positionCache.x,positionCache.y,positionCache.width,positionCache.height),positionCache.x,positionCache.y,0xff0000ff);
    }

    public StyleList style(){
        return styles;
    }

    public void close(){
        System.out.println("[GC] Kasuga GUI Node Free");
        isClosed = true;
        locator.close();
        for (Node child : this.children) {
            child.close();
        }
    }

    public BackgroundRender getBackground() {
        return background;
    }

    public List<Node> children() {
        return children;
    }


    HashMap<String, Function<MouseEvent,Boolean>> mouseEvents = new HashMap<>();

    public void onClick(MouseEvent fromParent){
        MouseEvent localMouseEvent = locator.transformMouseEvent(fromParent);
        if(localMouseEvent == null)
            return;
        if(mouseEvents.containsKey("click") && mouseEvents.get("click").apply(localMouseEvent))
            return;
        for (Node child : this.children) {
            child.onClick(localMouseEvent);
        }
    }

    public void listenMouseEvent(String name,Function<MouseEvent,Boolean> callback){
        this.mouseEvents.put(name,callback);
    }
}
