package kasuga.lib.core.client.frontend.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.client.frontend.common.layouting.LayoutBox;
import kasuga.lib.core.client.frontend.gui.events.mouse.MouseClickEvent;
import kasuga.lib.core.client.frontend.gui.events.mouse.MouseReleasedEvent;
import kasuga.lib.core.client.frontend.rendering.RenderContext;
import kasuga.lib.core.util.data_type.Vec2i;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class GuiScreen extends Screen {
    private final GuiInstance instance;
    private final boolean autoClose;

    public GuiScreen(GuiInstance guiInstance) {
        super(Component.literal(""));
        this.instance = guiInstance;
        this.instance.open(this);
        this.autoClose = false;
    }

    public GuiScreen(ResourceLocation screenId){
        super(Component.literal(""));
        this.instance = KasugaLib.STACKS.GUI.orElseThrow(IllegalStateException::new).create(screenId);
        this.instance.open(this);
        this.autoClose = true;
    }

    @Override
    protected void init() {
        instance.updateSourceInfo(this, new SourceInfo(new LayoutBox(0,0,width,height)));
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        this.instance.beforeRender();
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        instance.getContext().ifPresent((context)->{
            context.render(this,RenderContext.fromScreen(this,pPoseStack,pMouseX,pMouseY,pPartialTick));
        });
        this.instance.afterRender();
    }

    @Override
    public void onClose() {
        super.onClose();
        this.instance.close(this);
        if(autoClose){
            KasugaLib.STACKS.GUI.orElseThrow(IllegalStateException::new).closeInstance(this.instance);
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        MouseClickEvent event = MouseClickEvent.fromScreen(null, new Vec2i((int)pMouseX,(int)pMouseY), pButton);
        instance.getContext().ifPresent((context)->{
            context.appendTask(()->{
                context.getRootNode().onMouseEvent(this,event);
            });
        });
        return true;
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        MouseReleasedEvent event = MouseReleasedEvent.fromScreen(null, new Vec2i((int)pMouseX,(int)pMouseY), pButton);
        instance.getContext().ifPresent((context)->{
            context.appendTask(()->{
                context.getRootNode().onMouseEvent(this,event);
            });
        });
        return true;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
