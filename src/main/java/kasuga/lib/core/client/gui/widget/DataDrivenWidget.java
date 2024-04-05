package kasuga.lib.core.client.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import kasuga.lib.core.client.gui.enums.LocationType;
import kasuga.lib.core.client.gui.SimpleWidget;
import kasuga.lib.core.xml.IXmlObject;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;

public class DataDrivenWidget extends SimpleWidget {
    private int bgx, bgy, bgWidth, bgHeight;
    public DataDrivenWidget(int pX, int pY, int pWidth, int pHeight, LocationType type) {
        super(pX, pY, pWidth, pHeight, type);
    }

    public DataDrivenWidget(int width, int height, LocationType type) {
        super(width, height, type);
    }

    @Override
    public void init() {}

    public void setBackgroundX(int x) {
        this.bgx = this.getX() + x;
    }

    public void setBackgroundY(int y) {
        this.bgy = this.getY() + y;
    }

    public void setBackgroundWidth(int width) {
        this.bgWidth = width;
    }

    public void setBackgroundHeight(int height) {
        this.bgHeight = height;
    }

    public void setBackgroundLeft(int left) {
        this.bgx = this.getX() + left;
    }

    public void setBackgroundTop(int top) {
        this.bgy = this.getY() + top;
    }

    public void setBackgroundRight(int right) {
        this.bgWidth = this.width + this.getX() - bgx - right;
    }

    public void setBackgroundBottom(int bottom) {
        this.bgHeight = this.height + this.getY() - bgy - bottom;
    }

    public void decode(IXmlObject<?> object) {}

    public IXmlObject<?> encode() {
        return null;
    }

    @Override
    protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {

    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {

    }
}
