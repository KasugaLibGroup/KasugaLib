package kasuga.lib.core.client.gui.components;

import kasuga.lib.core.client.gui.layout.yoga.YogaNode;
import kasuga.lib.core.client.gui.style.Styles;

public class CalculatedPositionCache {
    float x;
    float y;
    float width;
    float height;

    void fromYoga(Node parent,Node thisNode,YogaNode node){
        x = node.getLayoutLeft();
        y = node.getLayoutTop();
        width = node.getLayoutWidth();
        height = node.getLayoutHeight();
        if(parent != null /* && thisNode.style().get(Styles) */){
            x += parent.positionCache.x;
            y += parent.positionCache.y;
        }
    }
}
