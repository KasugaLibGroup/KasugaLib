package kasuga.lib.core.client.frontend.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import kasuga.lib.core.client.frontend.common.layouting.LayoutBox;
import kasuga.lib.core.client.frontend.rendering.RenderContext;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class GuiScreen extends Screen {
    private final GuiInstance instance;

    public GuiScreen(GuiInstance guiInstance) {
        super(Component.literal(""));
        this.instance = guiInstance;
    }

    @Override
    protected void init() {
        GuiScreen that = this;
        instance.getContext().ifPresent((context)->{
            context.getRootNode().setSourceInfo(that, new SourceInfo(new LayoutBox(0,0,width,height)));
        });
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        instance.getContext().ifPresent((context)->{
            context.getRootNode().render(RenderContext.fromScreen(this,pPoseStack,pMouseX,pMouseY,pPartialTick));
        });
    }
}
