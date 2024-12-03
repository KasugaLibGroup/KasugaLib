package kasuga.lib.core.client.interaction;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class GuiOperatingPerspectiveScreen extends Screen {
    float fov = 1.0F;
    private double initialXPos;
    private double initialYPos;

    public GuiOperatingPerspectiveScreen() {
        super(Component.literal(""));
        Window window = Minecraft.getInstance().getWindow();
        initialXPos = window.getScreenWidth() / 2.0F;
        initialYPos = window.getScreenHeight() / 2.0F;
    }

    public void render(int mouseX, int mouseY, float partialTicks) {}


    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        fov = Math.max(Math.min(1.0F, fov - (float) pDelta / 5.0F), 0.1F);
        return true;
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        MouseHandler handler = Minecraft.getInstance().mouseHandler;
        this.initialXPos = handler.xpos();
        this.initialYPos = handler.ypos();
        InputConstants.grabOrReleaseMouse(this.minecraft.getWindow().getWindow(), 212995, this.initialXPos, this.initialYPos);
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        InputConstants.grabOrReleaseMouse(this.minecraft.getWindow().getWindow(), 212993, this.initialXPos, this.initialYPos);
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if(super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY)){
            return true;
        }
        Minecraft.getInstance().player.turn(pDragX * fov, pDragY * fov);
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
