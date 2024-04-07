package kasuga.lib.core.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import kasuga.lib.core.client.gui.enums.LocationType;
import kasuga.lib.core.client.render.texture.SimpleTexture;
import net.minecraft.client.Minecraft;
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
        this.x = isRelative() ? (x + parent.x()) : x;
    }

    public void setY(int y) {
        this.y = isRelative() ? (y + parent.y()) : y;
    }

    public int x() {
        return isRelative() ? (x - parent.x()) : x;
    }

    public int y() {
        return isRelative() ? (y - parent.y()) : y;
    }

    public void setLeft(int left) {
        setX(left);
    }

    public void setTop(int top) {
        setY(top);
    }

    public void setRight(int right) {
        this.width = isRelative() ? (parent.x + parent.width - right - x) : (Minecraft.getInstance().screen == null ?
                0 : Minecraft.getInstance().screen.width - right - x);
    }

    public void setBottom(int bottom) {
        this.height = isRelative() ? (parent.y + parent.height - bottom - y) : (Minecraft.getInstance().screen == null ?
                0 : Minecraft.getInstance().screen.height - bottom - y);
    }

    public int left() {
        return x();
    }

    public int top() {
        return y();
    }

    public int right() {
        return isRelative() ? (parent.x + parent.width - x - width) : Minecraft.getInstance().screen == null ?
                0 : Minecraft.getInstance().screen.width - x - width;
    }

    public int bottom () {
        return isRelative() ? (parent.y + parent.height - y - height) : Minecraft.getInstance().screen == null ?
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
}
