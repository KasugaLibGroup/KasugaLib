package kasuga.lib.core.client.frontend.gui.layout;

import kasuga.lib.core.client.frontend.gui.layout.vanilla.VanillaLayoutEngine;
import kasuga.lib.core.client.frontend.gui.layout.yoga.YogaLayoutEngine;

public class LayoutEngines {
    public static LayoutEngineType<YogaLayoutEngine> YOGA = LayoutEngineType.of(()->YogaLayoutEngine::new);
    public static LayoutEngineType<VanillaLayoutEngine> VANILLA = LayoutEngineType.of(()-> VanillaLayoutEngine::new);
    public static LayoutEngineType<?> DEFAULT = VANILLA;
    public static void init(){
        DEFAULT.create().init();
    }
}
