package kasuga.lib.core.client.gui.runtime;

import net.minecraft.resources.ResourceLocation;

import java.io.IOException;

public interface Platform<M extends PlatformModule,T extends PlatformRuntime<M>> {
    T createRuntime();

    M createModule(ResourceLocation location) throws IOException;
}
