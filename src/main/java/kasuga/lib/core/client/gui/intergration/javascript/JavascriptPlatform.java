package kasuga.lib.core.client.gui.intergration.javascript;

import kasuga.lib.core.client.gui.runtime.Platform;
import kasuga.lib.core.client.gui.runtime.PlatformModule;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.util.HashMap;

public class JavascriptPlatform implements Platform<JavascriptPlatformModule,JavascriptPlatformRuntime> {
    HashMap<ResourceLocation,JavascriptPlatformModule> modules = new HashMap<>();
    @Override
    public JavascriptPlatformRuntime createRuntime() {
        return new JavascriptPlatformRuntime(this);
    }

    @Override
    public JavascriptPlatformModule createModule(ResourceLocation location) {
        if(modules.containsKey(location))
            return modules.get(location);
        JavascriptPlatformModule module = JavascriptPlatformModule.fromLocation(location);
        modules.put(location,module);
        return module;
    }
}
