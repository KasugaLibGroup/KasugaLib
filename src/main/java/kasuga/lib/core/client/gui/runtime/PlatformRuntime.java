package kasuga.lib.core.client.gui.runtime;

import kasuga.lib.core.client.gui.thread.GuiContext;
import net.minecraft.resources.ResourceLocation;
import org.graalvm.polyglot.Source;

public interface PlatformRuntime<M extends PlatformModule> {
    public void importModule(M module);
    public void importModule(ResourceLocation moduleLocation);

    public void run(String sourceCode);

    public void run(Source stream);

    public void bindContext(GuiContext context);

    void close();

    void tick();

    GuiContext getContext();
}
