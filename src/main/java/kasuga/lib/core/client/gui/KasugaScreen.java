package kasuga.lib.core.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import kasuga.lib.core.client.gui.components.Node;
import kasuga.lib.core.client.gui.context.MouseEvent;
import kasuga.lib.core.client.gui.context.PlaneMouseContext;
import kasuga.lib.core.client.gui.context.RenderContext;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

public class KasugaScreen extends Screen {

    private final Node root;
    private final Supplier<Void> callback;

    protected KasugaScreen(Node root) {
        super(Component.literal(""));
        this.root = root;
        this.callback = null;
    }

    public KasugaScreen(Node root, Supplier<Void> callback) {
        super(Component.literal(""));
        this.root = root;
        this.callback = callback;
    }

    public static Screen screenWithTickFunction(Node root, Supplier<Void> callback){
        return new KasugaScreen(root,callback);
    }

    public static Screen screen(Node root){
        return new KasugaScreen(root);
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        if(this.callback != null)
            this.callback.get();
        RenderContext context = new RenderContext(RenderContext.RenderContextType.SCREEN);
        context.setPoseStack(pPoseStack);
        context.setPartialTicks(pPartialTick);
        context.setMouseContext(new PlaneMouseContext(pMouseX,pMouseY));
        this.root.applyStyles();
        this.root.getLocatorNode().calculateLayout(width,height);
        this.root.checkShouldReLayout();
        this.root.dispatchRender(context);
    }

    @Override
    public void onClose() {
        super.onClose();
        this.root.close();
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        this.root.onClick(new MouseEvent((float) pMouseX, (float) pMouseY,pButton));
        return true;
    }
}
