package kasuga.lib.core.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import kasuga.lib.core.client.gui.enums.LocationType;
import kasuga.lib.core.client.render.texture.SimpleTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public abstract class SimpleWidget extends AbstractWidget implements IBackground {
    private SimpleTexture background = null;
    private SimpleWidget parent = null;
    private LocationType locationType;

    public SimpleWidget(int x, int y, int width, int height, LocationType type) {
        super(x, y, width, height, Component.empty());
        this.locationType = type;
    }

    public SimpleWidget(int width, int height, LocationType type) {
        super(0, 0, width, height, Component.empty());
        this.locationType = type;
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

    public LocationType getLocationType() {
        return locationType;
    }

    public void setLocationType(LocationType type) {
        this.locationType = type;
    }

    public boolean isRelative() {
        return locationType == LocationType.RELATIVE && hasParent();
    }

    public boolean isLocationTypeValid() {
        if (locationType == LocationType.RELATIVE && !hasParent()) return false;
        return locationType == LocationType.INVALID;
    }

    public boolean isAbsolute() {
        return locationType == LocationType.ABSOLUTE;
    }

    public boolean hasParent() {
        return parent != null;
    }

    public void setX(int x) {
        super.setX(isRelative() ? (x + parent.x()) : x);
    }

    public void setY(int y) {
        super.setY(isRelative() ? (y + parent.y()) : y);
    }

    public int x() {
        return isRelative() ? (super.getX() - parent.x()) : super.getX();
    }

    public int y() {
        return isRelative() ? (super.getY() - parent.y()) : super.getY();
    }

    public void setLeft(int left) {
        setX(left);
    }

    public void setTop(int top) {
        setY(top);
    }

    public void setRight(int right) {
        this.width = isRelative() ? (parent.getX() + parent.width - right - getX()) : (Minecraft.getInstance().screen == null ?
                0 : Minecraft.getInstance().screen.width - right - getX());
    }

    public void setBottom(int bottom) {
        this.height = isRelative() ? (parent.getY() + parent.height - bottom - getY()) : (Minecraft.getInstance().screen == null ?
                0 : Minecraft.getInstance().screen.height - bottom - getY());
    }

    public int left() {
        return x();
    }

    public int top() {
        return y();
    }

    public int right() {
        return isRelative() ? (parent.getX() + parent.width - getX() - width) : Minecraft.getInstance().screen == null ?
                0 : Minecraft.getInstance().screen.width - getX() - width;
    }

    public int bottom () {
        return isRelative() ? (parent.getY() + parent.height - getY() - height) : Minecraft.getInstance().screen == null ?
                0 : Minecraft.getInstance().screen.height - getY() - height;
    }

    public abstract void init();

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }
}
