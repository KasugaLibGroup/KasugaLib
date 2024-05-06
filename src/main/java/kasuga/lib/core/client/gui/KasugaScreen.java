package kasuga.lib.core.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import kasuga.lib.core.client.gui.context.RenderContext;
import kasuga.lib.core.client.gui.thread.GuiContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class KasugaScreen extends Screen {
    GuiInstance instance;
    protected KasugaScreen(GuiInstance instance) {
        super(Component.literal(""));
        this.instance = instance;
    }

    @Override
    protected void init() {
        this.instance.context.size(this,width,height);
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        if(this.instance.context == null)
            return;
        this.instance.context.render(RenderContext.fromScreen(this,pPoseStack,pMouseX,pMouseY,pPartialTick));
    }

    @Override
    public void removed(){
        this.instance.close(this);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        // this.root.onClick(new MouseEvent((float) pMouseX, (float) pMouseY,pButton));
        return true;
    }
}
