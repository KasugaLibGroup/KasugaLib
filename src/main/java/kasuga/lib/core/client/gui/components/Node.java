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
    YogaNode locatorNode;

    List<Node> children;

    Node parent;

    private boolean shouldCalculateLayout = true;
    CalculatedPositionCache positionCache = new CalculatedPositionCache();

    BackgroundRender background = new BackgroundRender();

    StyleList styles = new StyleList();
    boolean isClosed = false;

    public Node(){
        locatorNode = YogaNode.create();
        children = new ArrayList<>();
        YogaMeasureFunction measureFunction = measure();
        if(measureFunction != null){
            this.locatorNode.setMeasureFunction(measureFunction);
        }
    }

    public void insertChild(int index,Node node){
        this.locatorNode.addChildAt(index,node.getLocatorNode());
        this.children.add(index,node);
        node.parent = this;
    }

    public void addChild(Node node){
        int index = this.children.size();
        this.insertChild(index,node);
    }

    public void removeChild(int index){
        this.locatorNode.removeChildAt(index);
        this.children.remove(index).parent = null;
    }

    public void removeChild(Node node){
        int index = this.children.indexOf(node);
        if(index == -1)
            return;
        this.removeChild(index);
    }

    public YogaNode getLocatorNode(){
        return locatorNode;
    }

    public void markReLayout(){
        this.shouldCalculateLayout = true;
    }

    public void markMeasureDirty(){
        if(this.isClosed)
            return;
        this.locatorNode.dirty();
        markReLayout();
    }

    public void checkShouldReLayout(){
        if(this.isClosed || !locatorNode.hasNewLayout())
            return;

        locatorNode.visited();

        markReLayout();

        for (Node child : children) {
            child.checkShouldReLayout();
        }
    }

    public YogaMeasureFunction measure(){
        return null;
    }

    public void renderPreTick(){
        if(shouldCalculateLayout){
            positionCache.fromYoga(parent,this,locatorNode);
            shouldCalculateLayout = false;
        }
        for (Node child : children) {
            child.renderPreTick();
        }
    }

    @Override
    public void dispatchRender(RenderContext context) {
        if(isClosed)
            return;
        render(context);
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

    public static SimpleTexture TEST_TEXTURE = new SimpleTexture(new ResourceLocation("kasuga_lib","textures/gui/pixel.png"),32,32);;

    public void render(RenderContext context){
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
        this.locatorNode.free();
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

    public MouseEvent transformMouseEvent(MouseEvent event){
        MouseEvent newEvent = new MouseEvent(
                event.mouseX() - this.positionCache.x,
                event.mouseY() - this.positionCache.y,
                event.button());
        if(newEvent.mouseX() < 0 ||
                newEvent.mouseY() < 0 ||
                newEvent.mouseX() > this.positionCache.width ||
                newEvent.mouseY() > this.positionCache.height
        ){
            return null;
        }
        return newEvent;
    }

    HashMap<String, Function<MouseEvent,Boolean>> mouseEvents = new HashMap<>();

    public void onClick(MouseEvent fromParent){
        MouseEvent localMouseEvent = transformMouseEvent(fromParent);
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
