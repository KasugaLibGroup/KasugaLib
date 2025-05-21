package kasuga.lib.core.client.frontend.gui.layout.yoga;

import kasuga.lib.core.client.frontend.common.layouting.LayoutEngine;
import kasuga.lib.core.client.frontend.gui.layout.LayoutEngineType;
import kasuga.lib.core.client.frontend.gui.layout.LayoutEngines;
import kasuga.lib.core.client.frontend.gui.layout.yoga.api.YogaFileLocator;
import kasuga.lib.core.client.frontend.gui.nodes.GuiDomNode;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

public class YogaLayoutEngine implements LayoutEngine<YogaLayoutNode,GuiDomNode> {

    @Override
    public YogaLayoutNode createNode(GuiDomNode node, Object source) {
        return new YogaLayoutNode(this, node,source);
    }

    @Override
    public LayoutEngineType<?> getType() {
        return LayoutEngines.YOGA;
    }

    @Override
    public void init() {
        YogaStyleHandlers.init();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT,()-> YogaFileLocator::configureLWJGLPath);
    }
}
