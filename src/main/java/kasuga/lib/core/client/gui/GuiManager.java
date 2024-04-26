package kasuga.lib.core.client.gui;

import kasuga.lib.core.client.gui.thread.GuiThreadPool;
import net.minecraft.resources.ResourceLocation;

public class GuiManager {
    protected GuiThreadPool threadPool = new GuiThreadPool();

    public GuiManager(){
        threadPool.createRenderThread();
    }

    public GuiInstance create(ResourceLocation mainFile){
        return new GuiInstance(()->threadPool.createContext(),mainFile);
    }

    public void renderTick() {
        threadPool.dispatchRenderTick();
    }
}
