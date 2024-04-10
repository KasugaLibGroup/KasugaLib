package kasuga.lib.core.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import kasuga.lib.core.client.gui.enums.DisplayType;
import kasuga.lib.core.client.gui.enums.PositionType;
import kasuga.lib.core.client.gui.layout.ElementLocator;
import kasuga.lib.core.client.gui.layout.LayoutAllocator;
import kasuga.lib.core.client.render.texture.SimpleTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public abstract class SimpleWidget extends AbstractWidget implements IBackground {
    private SimpleTexture background = null;
    private SimpleWidget parent = null;
    private PositionType positionType;
    private DisplayType displayType;
    private ElementLocator elementLocator = new ElementLocator();
    public SimpleWidget(int x, int y, int width, int height, PositionType type,DisplayType displayType) {
        super(x, y, width, height, Component.empty());
        this.positionType = type;
        this.displayType = displayType;
        this.elementLocator.setLeft(x);
        this.elementLocator.setTop(y);
        this.elementLocator.setWidth(width);
        this.elementLocator.setHeight(height);
    }

    public SimpleWidget(int width, int height, PositionType type,DisplayType displayType) {
        super(0, 0, width, height, Component.empty());
        this.positionType = type;
        this.displayType = displayType;
        this.elementLocator.setLeft(0);
        this.elementLocator.setTop(0);
        this.elementLocator.setWidth(width);
        this.elementLocator.setHeight(height);
    }

    public void setBackground(SimpleTexture texture) {
        this.background = texture;
    }

    public void setBackground(ResourceLocation location) {
        this.background = new SimpleTexture(location);
    }

    public SimpleTexture getBackground() {
        return background;
    }

    public boolean hasBackground() {
        return background != null;
    }

    public void setParent(SimpleWidget parent) {
        this.parent = parent;
        this.triggerLocate();
    }

    public SimpleWidget getParent() {
        return parent;
    }

    public PositionType getPositionType() {
        return positionType;
    }

    public void setPositionType(PositionType type) {
        this.positionType = type;
    }

    public boolean isAbsolute() {
        return positionType == PositionType.ABSOLUTE && hasParent();
    }

    public boolean isLocationTypeValid() {
        if (positionType == PositionType.ABSOLUTE && !hasParent()) return false;
        return positionType == PositionType.INVALID;
    }

    public boolean isFixed() {
        return positionType == PositionType.FIXED;
    }

    public boolean hasParent() {
        return parent != null;
    }

    public void setX(int x) {
        this.elementLocator.setLeft(x);
        this.triggerLocate();
    }

    public void setY(int y) {
        this.elementLocator.setTop(y);
        this.triggerLocate();
    }
    public void setLeft(int left) {
        setX(left);
        this.triggerLocate();
    }

    public void setTop(int top) {
        setY(top);
        this.triggerLocate();
    }

    public void setRight(int right) {
        this.elementLocator.setRight(right);
    }

    public void setBottom(int bottom) {
        this.elementLocator.setBottom(bottom);
    }

    public abstract void init();

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {}

    public void onClose(){}

    public DisplayType getDisplayType() {
        return displayType;
    }

    LayoutAllocator allocator = new LayoutAllocator(this);


    public LayoutAllocator getAllocator(){
        return allocator;
    }

    public void locate(){
        allocator.relocate();
    }

    public ElementLocator getElementLocator() {
        return elementLocator;
    }

    public void triggerLocate(){
        if(this.parent != null && this.positionType != PositionType.FIXED && this.positionType != PositionType.ABSOLUTE)
            this.parent.triggerLocate();
        else
            this.locate();
    }

    public ElementBoundingBox getAbsoluteLocation(){
        return this.allocator.getSelfBoundingBox();
    }

    public void updatePosition(){
        ElementBoundingBox boundingBox = getAbsoluteLocation();
        this.x = boundingBox.left;
        this.y = boundingBox.top;
        this.width = boundingBox.getWidth();
        this.height = boundingBox.getHeight();
    }

    @Override
    public void setHeight(int value) {
        this.elementLocator.setHeight(value);
        this.triggerLocate();
    }

    @Override
    public void setWidth(int pWidth) {
        this.elementLocator.setWidth(pWidth);
        this.triggerLocate();

    }
}
