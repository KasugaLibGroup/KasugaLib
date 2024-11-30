package kasuga.lib.core.client.frontend.gui.layout;

import kasuga.lib.core.client.frontend.common.layouting.LayoutEngine;
import kasuga.lib.core.client.frontend.gui.layout.yoga.YogaLayoutEngine;
import net.minecraftforge.common.util.Lazy;

public class LayoutEngines {
    public static Lazy<LayoutEngine> YOGA = Lazy.of(YogaLayoutEngine::new);
}
