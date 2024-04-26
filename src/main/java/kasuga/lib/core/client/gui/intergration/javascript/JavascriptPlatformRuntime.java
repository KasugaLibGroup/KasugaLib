package kasuga.lib.core.client.gui.intergration.javascript;

import kasuga.lib.core.client.gui.runtime.PlatformModule;
import kasuga.lib.core.client.gui.runtime.PlatformRuntime;
import kasuga.lib.core.client.gui.thread.GuiContext;
import net.minecraft.resources.ResourceLocation;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;

import java.util.HashMap;

public class JavascriptPlatformRuntime implements PlatformRuntime<JavascriptPlatformModule> {

    Context javascriptVmContext;

    JavascriptGuiBinding binding;
    private GuiContext guiContext;

    HashMap<ResourceLocation,Value> cachedModules = new HashMap<>();
    private final JavascriptPlatform platform;

    JavascriptPlatformRuntime(JavascriptPlatform platform){
        javascriptVmContext = Context.newBuilder()
                .allowHostAccess(HostAccess.SCOPED)
                .build();
        this.platform = platform;
    }
    @Override
    public void importModule(JavascriptPlatformModule module) {
        module.asCommonJs(this);
    }

    @Override
    public void importModule(ResourceLocation moduleLocation) {
        this.getModule(moduleLocation);
    }

    @Override
    public void run(String sourceCode) {
        javascriptVmContext.eval("js",sourceCode);
    }

    @Override
    public void bindContext(GuiContext context) {
        this.guiContext = context;
        Value languageBinding = javascriptVmContext.getBindings("js");
        languageBinding.putMember("minecraft",guiContext.getBinding().getNative());
    }

    public Value getModule(ResourceLocation resourceLocation) {
        if(cachedModules.containsKey(resourceLocation))
            return cachedModules.get(resourceLocation);
        JavascriptPlatformModule module = this.platform.createModule(resourceLocation);
        Value localBinding = module.asCommonJs(this);
        cachedModules.put(resourceLocation,localBinding);
        return localBinding;
    }

    @Override
    public void close() {
        this.javascriptVmContext.close();
    }

    @Override
    public GuiContext getContext() {
        return guiContext;
    }
}
