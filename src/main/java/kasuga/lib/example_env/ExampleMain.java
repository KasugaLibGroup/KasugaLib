package kasuga.lib.example_env;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.util.Envs;
import kasuga.lib.example_env.boundary.ExampleBoundaryModule;
import kasuga.lib.example_env.train.ExampleTrainDeviceModule;
import kasuga.lib.registrations.registry.CreateRegistry;

public class ExampleMain {

    public static final CreateRegistry testRegistry = new CreateRegistry(KasugaLib.MOD_ID, KasugaLib.EVENTS);
    public static void invoke() {
        // ExampleTrackMaterial.invoke();
        AllExampleElements.invoke();
        if (Envs.isClient()) AllClient.invoke();
        // testRegistry.submit();
        // ExampleTracks.invoke();
        // AllExampleBogey.invoke();
        ExampleTrainDeviceModule.invoke();
        ExampleBoundaryModule.invoke();
        AllExampleElements.testRegistry.submit();
    }
}
