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

    void fromYoga(Node parent,Node thisNode,YogaNode node){
        float oldX = x,oldY = y, oldW = width, oldH = height;
        relativeX = x = node.getLayoutLeft();
        relativeY = y = node.getLayoutTop();
        width = node.getLayoutWidth();
        height = node.getLayoutHeight();
        if(parent != null /* && thisNode.style().get(Styles) */){
            x += parent.positionCache.x;
            y += parent.positionCache.y;
        }

        if(oldX!=x || oldY!=y || oldW != width || oldH != height){
            thisNode.children().forEach(Node::markReLayout);
        }
    }
}
