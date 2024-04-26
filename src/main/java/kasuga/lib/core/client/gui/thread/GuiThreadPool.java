package kasuga.lib.core.client.gui.thread;

import java.util.ArrayList;
import java.util.List;

public class GuiThreadPool {
    List<GuiThread> threads = new ArrayList<>();

    public void createRenderThread(){
        GuiThread thread = new GuiThread();
        threads.add(thread);
        thread.start();
    }

    public GuiContext createContext(){
        if(this.threads.isEmpty())
            return null;
        GuiContext context = new GuiContext(threads.get(0));
        threads.get(0).attachContext(context);
        return context;
    }

    public void dispatchRenderTick() {
        for (GuiThread thread : this.threads) {
            thread.dispatchTick();
        }
    }
}
