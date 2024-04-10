package kasuga.lib.core.client.gui.layout;

import kasuga.lib.core.client.gui.ElementBoundingBox;
import kasuga.lib.core.client.gui.SimpleWidget;
import kasuga.lib.core.client.gui.enums.DisplayType;
import kasuga.lib.core.client.gui.enums.PositionType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public class LayoutAllocator {
    private final SimpleWidget element;
    private ElementBoundingBox selfBoundingBox;

    public LayoutAllocator(SimpleWidget parent){
        this.element = parent;
        this.relocate();
    }

    int lastX = 0;
    int lastY = 0;
    int lastHeight = 0;

    public ElementBoundingBox allocate(PositionType positionType, DisplayType displayType, ElementLocator locator){
        if(positionType == PositionType.FIXED){
            Screen screen = Minecraft.getInstance().screen;
            if(screen == null)
                return ElementBoundingBox.ofHeightWidth(0,0,0,0);
            return locator.locateAbsolute(ElementBoundingBox.ofHeightWidth(0,0,screen.width,screen.height));
        }

        if(positionType == PositionType.ABSOLUTE){
            if(element.getPositionType() != PositionType.ABSOLUTE && element.getPositionType() != PositionType.FIXED)
                return element.getParent().getAllocator().allocate(positionType, displayType, locator);
            return locator.locateAbsolute(selfBoundingBox);
        }

        int elementHeight = locator.getHeight(selfBoundingBox.getHeight());
        int elementWidth = locator.getWidth(selfBoundingBox.getWidth());

        if(displayType == DisplayType.BLOCK || elementWidth > this.selfBoundingBox.getWidth() - lastX){
            lastY += lastHeight;
            lastHeight = 0;
            lastX = 0;
        }

        ElementBoundingBox boundingBox = ElementBoundingBox.ofHeightWidth(lastX + this.selfBoundingBox.left,lastY + this.selfBoundingBox.top,elementWidth,elementHeight);

        if(displayType == DisplayType.BLOCK){
            lastY += elementHeight;
        }else{
            lastX += elementWidth;
            lastHeight = Math.max(elementHeight,lastHeight);
        }

        return boundingBox;
    }

    public void relocate(){
        this.lastX = 0;
        this.lastY = 0;
        this.lastHeight = 0;
        if(this.element.getParent() == null){
            Screen screen = Minecraft.getInstance().screen;
            if(screen == null){
                this.selfBoundingBox = ElementBoundingBox.EMPTY;
                return;
            }
            this.selfBoundingBox = this.element.getElementLocator().locateAbsolute(ElementBoundingBox.ofHeightWidth(0,0,screen.width,screen.height));
            return;
        }
        this.selfBoundingBox = this.element.getParent().getAllocator().allocate(this.element.getPositionType(),this.element.getDisplayType(),this.element.getElementLocator());
    }

    public ElementBoundingBox getSelfBoundingBox() {
        return selfBoundingBox;
    }
}
