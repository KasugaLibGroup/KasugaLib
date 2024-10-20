package kasuga.lib.example_env;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.util.Envs;
import kasuga.lib.registrations.registry.CreateRegistry;

public class ExampleMain {

    public static final CreateRegistry testRegistry = new CreateRegistry(KasugaLib.MOD_ID, KasugaLib.EVENTS);
    public static void invoke() {
        // ExampleTrackMaterial.invoke();
        AllExampleElements.invoke();
        if (Envs.isClient()) AllClient.invoke();
        // ExampleTracks.invoke();
        // AllExampleBogey.invoke();
        AllExampleElements.testRegistry.submit();
    }
}
