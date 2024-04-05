package kasuga.lib.core.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public abstract class SimpleScreen extends Screen {
    public SimpleScreen(Component pTitle) {
        super(pTitle);
    }

    @Override
    public abstract void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick);

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }
}
