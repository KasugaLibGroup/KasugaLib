package kasuga.lib.core.client.gui.thread;

import java.util.*;

public class GuiContextManager {
    protected Set<GuiContext> contexts = new HashSet<>();

    public void tick() {
        contexts.forEach(GuiContext::tick);
        contexts.forEach(GuiContext::renderPreTick);
        ArrayList<GuiContext> needsToRemove = new ArrayList<>();
        for (GuiContext context : contexts) {
            if(context.closable())
                needsToRemove.add(context);
        }
        for (GuiContext removingContext : needsToRemove) {
            removingContext.close();
            contexts.remove(removingContext);
        }

    }

    public void addContext(GuiContext context) {
        this.contexts.add(context);
    }
}
