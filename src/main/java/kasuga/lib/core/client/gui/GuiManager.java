package kasuga.lib.core.client.gui;

import kasuga.lib.core.client.gui.thread.GuiThreadPool;
import net.minecraft.resources.ResourceLocation;
import org.graalvm.polyglot.Source;

public class GuiManager {
    protected GuiThreadPool threadPool = new GuiThreadPool();

    public GuiManager(){
        threadPool.createRenderThread();
    }

    public GuiInstance create(ResourceLocation mainFile){
        return new GuiInstance(()->threadPool.createContext(),mainFile);
    }

    public GuiInstance create(Source mainSource){
        return new GuiInstance(()->threadPool.createContext(),mainSource);
    }

    public void renderTick() {
        threadPool.dispatchRenderTick();
    }
}
