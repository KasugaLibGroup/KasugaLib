package kasuga.lib.core.client.gui.runtime;

import kasuga.lib.core.client.gui.thread.GuiContext;
import net.minecraft.resources.ResourceLocation;

public interface PlatformRuntime<M extends PlatformModule> {
    public void importModule(M module);
    public void importModule(ResourceLocation moduleLocation);

    public void run(String sourceCode);

    public void bindContext(GuiContext context);

    void close();

    GuiContext getContext();
}
