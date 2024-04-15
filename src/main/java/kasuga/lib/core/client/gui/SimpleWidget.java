package kasuga.lib.core.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import kasuga.lib.core.client.gui.attributes.Attribute;
import kasuga.lib.core.client.gui.attributes.AttributeType;
import kasuga.lib.core.client.gui.attributes.Attributes;
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

import java.util.HashMap;

public abstract class SimpleWidget extends AbstractWidget implements IBackground {
    private SimpleTexture background = null;
    private SimpleWidget parent = null;
    private ElementLocator elementLocator = new ElementLocator();

    private HashMap<AttributeType<?>, Attribute<?>> attributes = new HashMap<>();


    public SimpleWidget(int x, int y, int width, int height, PositionType type,DisplayType displayType) {
        super(x, y, width, height, Component.empty());
        this.setPositionType(type);
        this.setDisplayType(displayType);
        this.elementLocator.setLeft(x);
        this.elementLocator.setTop(y);
        this.elementLocator.setWidth(width);
        this.elementLocator.setHeight(height);
    }

    public SimpleWidget(){
        super(0,0,0,0,Component.empty());
    }

    public SimpleWidget(int width, int height, PositionType type,DisplayType displayType) {
        super(0, 0, width, height, Component.empty());
        this.setPositionType(type);;
        this.setDisplayType(displayType);
        this.elementLocator.setLeft(0);
        this.elementLocator.setTop(0);
        this.elementLocator.setWidth(width);
        this.elementLocator.setHeight(height);
    }

    public void setAttribute(AttributeType<?> attributeType,Attribute<?> attribute){
        if(attribute.canApplyTo(this)){
            this.attributes.put(attributeType,attribute);
            attribute.apply(this);
        }
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
        return Attributes.POSITION_TYPE.castGet(this.attributes).getValue();
    }

    public void setPositionType(PositionType type) {
        this.setAttribute(Attributes.POSITION_TYPE,Attributes.POSITION_TYPE.create(type));
    }

    public boolean isAbsolute() {
        return getPositionType() == PositionType.ABSOLUTE && hasParent();
    }

    public boolean isLocationTypeValid() {
        if (getPositionType() == PositionType.ABSOLUTE && !hasParent()) return false;
        return getPositionType() == PositionType.INVALID;
    }

    public boolean isFixed() {
        return getPositionType() == PositionType.FIXED;
    }

    public boolean hasParent() {
        return parent != null;
    }

    public void setX(int x) {
        this.setAttribute(Attributes.LEFT,Attributes.LEFT.create(x));
    }

    public void setY(int y) {
        this.setAttribute(Attributes.TOP,Attributes.TOP.create(y));
    }
    public void setLeft(int left) {
        setX(left);
    }

    public void setTop(int top) {
        setY(top);
    }

    public void setRight(int right) {
        this.setAttribute(Attributes.RIGHT,Attributes.RIGHT.create(right));
    }

    public void setBottom(int bottom) {
        this.setAttribute(Attributes.BOTTOM,Attributes.BOTTOM.create(bottom));
    }

    public abstract void init();

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        if (this.visible) {
            this.isHovered = pMouseX >= this.x && pMouseY >= this.y && pMouseX < this.x + this.width && pMouseY < this.y + this.height;
        }
    }

    @Override
    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {}

    public void onClose(){}

    public DisplayType getDisplayType() {
        return Attributes.DISPLAY_TYPE.castGet(this.attributes).getValue();
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
        if(this.parent != null && this.getPositionType() != PositionType.FIXED && this.getPositionType() != PositionType.ABSOLUTE)
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
    public void setHeight(int height) {
        this.setAttribute(Attributes.HEIGHT,Attributes.HEIGHT.create(width));
    }

    @Override
    public void setWidth(int width) {
        this.setAttribute(Attributes.WIDTH,Attributes.WIDTH.create(width));
    }

    public void setDisplayType(DisplayType displayType){
        this.setAttribute(Attributes.DISPLAY_TYPE,Attributes.DISPLAY_TYPE.create(displayType));
    }
}
