package kasuga.lib.core.client.interaction;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class GuiOperatingPerspectiveScreen extends Screen {
    float fov = 1.0F;

    public GuiOperatingPerspectiveScreen() {
        super(new TextComponent(""));
    }

    public void render(int mouseX, int mouseY, float partialTicks) {}


    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        fov = Math.max(Math.min(1.0F, fov - (float) pDelta / 5.0F), 0.1F);
        return true;
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if(super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY)){
            return true;
        }
        Minecraft.getInstance().player.turn(pDragX, pDragY);
        return true;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public float getFovModifier() {
        return fov;
    }
}
