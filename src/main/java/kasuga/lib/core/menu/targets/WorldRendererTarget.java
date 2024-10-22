package kasuga.lib.core.menu.targets;

import kasuga.lib.core.client.frontend.gui.GuiInstance;
import kasuga.lib.core.client.frontend.rendering.RenderContext;
import kasuga.lib.core.menu.GuiMenu;


public class WorldRendererTarget {
    private final GuiInstance guiInstance;

    public WorldRendererTarget(GuiInstance guiInstance) {
        this.guiInstance = guiInstance;
    }

    public void render(RenderContext context){
        guiInstance.beforeRender();
        guiInstance.getContext().ifPresent((guiContext)->{
            guiContext.render(context.source, context);
        });
        guiInstance.afterRender();
    }

    public void attach(){
        guiInstance.open(WorldRendererTarget.class);
    }

    public void detach(){
        guiInstance.close(WorldRendererTarget.class);
    }

    public static void attach(GuiMenu menu){
        menu.getBinding().apply(Target.WORLD_RENDERER).attach();
    }
    public static void detach(GuiMenu menu){
        menu.getBinding().apply(Target.WORLD_RENDERER).detach();
    }
}
