package kasuga.lib.core.client.frontend.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sk89q.worldedit.math.Vector2;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.client.frontend.common.layouting.LayoutBox;
import kasuga.lib.core.client.frontend.gui.events.mouse.*;
import kasuga.lib.core.client.frontend.gui.nodes.GuiDomNode;
import kasuga.lib.core.client.frontend.rendering.RenderContext;
import kasuga.lib.core.util.data_type.Pair;
import kasuga.lib.core.util.data_type.Vec2i;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.concurrent.atomic.AtomicReference;

public class GuiScreen extends Screen {
    private final GuiInstance instance;
    private final boolean autoClose;

    public GuiScreen(GuiInstance guiInstance) {
        super(new TextComponent(""));
        this.instance = guiInstance;
        this.instance.open(this);
        this.autoClose = false;
    }

    public GuiScreen(ResourceLocation screenId){
        super(new TextComponent(""));
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


    Pair<Double, Double> lastClickedPos = null;
    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        MouseDownEvent event = MouseDownEvent.fromScreen(null, new Vec2i((int)pMouseX,(int)pMouseY), pButton);
        instance.getContext().ifPresent((context)->{
            context.appendTask(()->{
                context.getRootNode().onMouseEvent(this,event);
            });
        });
        lastClickedPos = Pair.of(pMouseX, pMouseY);
        isLastClicked = true;
        lastX = pMouseX;
        lastY = pMouseY;
        return true;
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        MouseUpEvent event = MouseUpEvent.fromScreen(null, new Vec2i((int)pMouseX,(int)pMouseY), pButton);
        instance.getContext().ifPresent((context)->{
            context.appendTask(()->{
                context.getRootNode().onMouseEvent(this,event);
            });
        });

        if(lastClickedPos != null) {
            if(Math.abs(lastClickedPos.getFirst() - pMouseX) + Math.abs(lastClickedPos.getSecond() - pMouseY) < 0.1){
                MouseClickEvent clickEvent = MouseClickEvent.fromScreen(null, new Vec2i((int)pMouseX,(int)pMouseY), pButton);
                instance.getContext().ifPresent((context)->{
                    context.appendTask(()->{
                        context.getRootNode().onMouseEvent(this,clickEvent);
                    });
                });
            }
        }
        lastClickedPos = null;
        isLastClicked = false;
        lastX = 0;
        lastY = 0;
        return true;
    }

    double lastX = 0;
    double lastY = 0;
    boolean isLastClicked = false;

    @Override
    public void mouseMoved(double pMouseX, double pMouseY) {
        MouseMoveEvent moveEvent = MouseMoveEvent.fromScreen(null, new Vec2i((int)pMouseX,(int)pMouseY), 0);
        AtomicReference<MouseDragEvent> dragEvent = new AtomicReference<>(null);

        if(isLastClicked) {
            double xDeltaD = pMouseX - lastX, yDeltaD = pMouseY - lastY;
            int xDeltaI = (int) xDeltaD, yDeltaI = (int) yDeltaD;
            if(xDeltaI != 0 || yDeltaI != 0) {
                lastX += xDeltaI;
                lastY += yDeltaI;
                Vec2i delta = new Vec2i(xDeltaI, yDeltaI);
                dragEvent.set(MouseDragEvent.fromScreen(null, new Vec2i((int) pMouseX, (int) pMouseY), 0, delta));
            }
        }

        instance.getContext().ifPresent((context)->{
            context.appendTask(()->{
                context.getRootNode().onMouseEvent(this,moveEvent);
                if(dragEvent.get() != null){
                    for (GuiDomNode activateElement : context.getActivateElements()) {
                        activateElement.dispatchEvent(dragEvent.get().getType(), dragEvent.get().withTarget(activateElement));
                    }
                }
            });
        });
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
