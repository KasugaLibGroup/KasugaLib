package kasuga.lib.core.client.gui.layout;

import kasuga.lib.core.client.gui.ElementBoundingBox;
import net.minecraft.client.Minecraft;

import java.util.HashMap;

public class ElementLocator {
    public enum Locators{
        LEFT,
        RIGHT,
        TOP,
        BOTTOM,
        WIDTH,
        HEIGHT
    }

    HashMap<Locators,Integer> anchors = new HashMap<>();

    ElementLocator(int left,int top,int height,int width){
        setLeft(left);
        setTop(top);
        setHeight(height);
        setWidth(width);
    }

    public ElementLocator(){
        this(0,0,0,0);
    }

    private void set(Locators target,Locators keep,Locators drop,int value) {
        if(anchors.containsKey(keep) && anchors.containsKey(drop)){
            anchors.remove(drop);
        }
        anchors.put(target,value);
    }

    public void setHeight(int value){
        set(Locators.HEIGHT,Locators.TOP,Locators.BOTTOM,value);
    }

    public void setWidth(int value){
        set(Locators.WIDTH,Locators.LEFT,Locators.RIGHT,value);
    }

    public void setLeft(int value){
        set(Locators.LEFT,Locators.RIGHT,Locators.WIDTH,value);
    }

    public void setRight(int value){
        set(Locators.RIGHT,Locators.LEFT,Locators.WIDTH,value);
    }

    public void setTop(int value){
        set(Locators.TOP,Locators.BOTTOM,Locators.HEIGHT,value);
    }

    public void setBottom(int value){
        set(Locators.BOTTOM,Locators.TOP,Locators.HEIGHT,value);
    }

    public int getRelativeLeft(){
        return this.anchors.get(Locators.LEFT);
    }

    public int getRelativeRight(){
        return this.anchors.get(Locators.RIGHT);
    }

    public int getRelativeTop(){
        return this.anchors.get(Locators.TOP);
    }

    public int getRelativeBottom(){
        return this.anchors.get(Locators.BOTTOM);
    }

    public int getAbsoluteLeft(int parentAbsoluteLeft,int parentWidth){
        return anchors.containsKey(Locators.LEFT) ?
                parentAbsoluteLeft + anchors.get(Locators.LEFT) :
                parentAbsoluteLeft + parentWidth - anchors.get(Locators.RIGHT)  - anchors.get(Locators.WIDTH);
    }

    public int getAbsoluteTop(int parentAbsoluteTop,int parentHeight){
        return anchors.containsKey(Locators.TOP) ?
                parentAbsoluteTop + anchors.get(Locators.TOP):
                parentAbsoluteTop + parentHeight - anchors.get(Locators.BOTTOM) - anchors.get(Locators.HEIGHT);
    }

    public int getWidth(int parentWidth){
        return anchors.containsKey(Locators.WIDTH)
                ? anchors.get(Locators.WIDTH) :
                parentWidth - anchors.get(Locators.RIGHT) - anchors.get(Locators.LEFT);
    }

    public int getHeight(int parentHeight){
        return anchors.containsKey(Locators.HEIGHT) ?
                anchors.get(Locators.HEIGHT) :
                parentHeight - anchors.get(Locators.BOTTOM) - anchors.get(Locators.TOP);
    }

    public ElementBoundingBox locateAbsolute(ElementBoundingBox parent){
        return ElementBoundingBox.ofHeightWidth(
                getAbsoluteLeft(parent.left,parent.getWidth()),
                getAbsoluteTop(parent.top,parent.getHeight()),
                getWidth(parent.getWidth()),
                getHeight(parent.getHeight())
        );
    }
}
