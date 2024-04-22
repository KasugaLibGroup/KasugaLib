package kasuga.lib.core.client.gui.components;

import kasuga.lib.core.client.gui.context.RenderContext;
import kasuga.lib.core.client.gui.layout.yoga.YogaMeasureFunction;
import kasuga.lib.core.client.gui.layout.yoga.YogaNode;
import kasuga.lib.core.client.gui.render.BackgroundRender;
import kasuga.lib.core.client.gui.style.StyleList;
import kasuga.lib.core.client.render.texture.SimpleTexture;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

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
        this.removeChild(index);
    }

    public YogaNode getLocatorNode(){
        return locatorNode;
    }

    public void markReLayout(){
        this.shouldCalculateLayout = true;
    }

    public void markMeasureDirty(){
        this.locatorNode.dirty();
        markReLayout();
    }

    public void checkShouldReLayout(){
        if(!locatorNode.hasNewLayout())
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

    @Override
    public void dispatchRender(RenderContext context) {
        if(isClosed)
            return;
        if(shouldCalculateLayout){
            positionCache.fromYoga(parent,this,locatorNode);
            shouldCalculateLayout = false;
        }
        render(context);
        context.pushZStack();
        for (Node child : this.children) {
            child.dispatchRender(context);
        }
        context.popZStack();
    }

    public void applyStyles(){
        styles.apply(this);
        for (Node child : this.children) {
            child.applyStyles();
        }
    }

    public static SimpleTexture TEST_TEXTURE = new SimpleTexture(new ResourceLocation("kasuga_lib","textures/gui/pixel.png"),32,32);;

    public void render(RenderContext context){
        background.render(context,(int)positionCache.x,(int)positionCache.y,(int)positionCache.width,(int)positionCache.height);
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
}
