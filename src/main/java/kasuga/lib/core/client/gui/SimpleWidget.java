package kasuga.lib.core.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import kasuga.lib.core.client.gui.enums.DisplayType;
import kasuga.lib.core.client.gui.enums.PositionType;
import kasuga.lib.core.client.gui.structure.ElementBoundingBox;
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
    public SimpleWidget(int x, int y, int width, int height, PositionType type,DisplayType displayType) {
        super(x, y, width, height, Component.empty());
        this.positionType = type;
        this.displayType = displayType;
    }

    public SimpleWidget(int width, int height, PositionType type,DisplayType displayType) {
        super(0, 0, width, height, Component.empty());
        this.positionType = type;
        this.displayType = displayType;
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
        this.x = isAbsolute() ? (x + parent.x()) : x;
    }

    public void setY(int y) {
        this.y = isAbsolute() ? (y + parent.y()) : y;
    }

    public int x() {
        return isAbsolute() ? (x - parent.x()) : x;
    }

    public int y() {
        return isAbsolute() ? (y - parent.y()) : y;
    }

    public void setLeft(int left) {
        setX(left);
    }

    public void setTop(int top) {
        setY(top);
    }

    public void setRight(int right) {
        this.width = isAbsolute() ? (parent.x + parent.width - right - x) : (Minecraft.getInstance().screen == null ?
                0 : Minecraft.getInstance().screen.width - right - x);
    }

    public void setBottom(int bottom) {
        this.height = isAbsolute() ? (parent.y + parent.height - bottom - y) : (Minecraft.getInstance().screen == null ?
                0 : Minecraft.getInstance().screen.height - bottom - y);
    }

    public int left() {
        return x();
    }

    public int top() {
        return y();
    }

    public int right() {
        return isAbsolute() ? (parent.x + parent.width - x - width) : Minecraft.getInstance().screen == null ?
                0 : Minecraft.getInstance().screen.width - x - width;
    }

    public int bottom () {
        return isAbsolute() ? (parent.y + parent.height - y - height) : Minecraft.getInstance().screen == null ?
                0 : Minecraft.getInstance().screen.height - y - height;
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
}
