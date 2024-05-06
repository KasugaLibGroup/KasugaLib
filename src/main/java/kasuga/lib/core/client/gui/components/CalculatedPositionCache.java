package kasuga.lib.core.client.gui.components;

import kasuga.lib.core.client.gui.layout.yoga.YogaNode;
import kasuga.lib.core.client.gui.style.Styles;

public class CalculatedPositionCache {
    float relativeX;
    float relativeY;
    float x;
    float y;
    float width;
    float height;
    boolean dirty;

    boolean fromYoga(CalculatedPositionCache parent,YogaNode node){
        float oldX = x,oldY = y, oldW = width, oldH = height;
        relativeX = x = node.getLayoutLeft();
        relativeY = y = node.getLayoutTop();
        width = node.getLayoutWidth();
        height = node.getLayoutHeight();
        if(parent != null /* && thisNode.style().get(Styles) */){
            x += parent.x;
            y += parent.y;
        }
        return oldX!=x || oldY!=y || oldW != width || oldH != height;
    }

    void markDirty(){
        this.dirty = true;
    }

    boolean attemptUpdate(CalculatedPositionCache parent,YogaNode node){
        if(dirty){
            dirty = false;
            return this.fromYoga(parent,node);
        }
        return false;
    }
}
