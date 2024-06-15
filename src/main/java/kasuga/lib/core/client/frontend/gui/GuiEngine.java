package kasuga.lib.core.client.frontend.gui;

import kasuga.lib.core.client.frontend.dom.nodes.NodeTypeRegistry;
import kasuga.lib.core.client.frontend.dom.registration.DOMPriorityRegistry;
import kasuga.lib.core.client.frontend.gui.nodes.AllGuiNodes;
import kasuga.lib.core.client.frontend.gui.nodes.GuiDomNode;
import kasuga.lib.core.client.frontend.gui.styles.AllGuiStyles;
import kasuga.lib.core.client.frontend.gui.styles.GuiStyleRegistry;
import kasuga.lib.core.client.frontend.rendering.RenderContext;
import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;

public class GuiEngine {
    public final DOMPriorityRegistry domRegistry = new DOMPriorityRegistry();

    public final NodeTypeRegistry<GuiDomNode> nodeTypeRegistry = new NodeTypeRegistry<>();

    public final GuiStyleRegistry styleRegistry = new GuiStyleRegistry();

    public void init(){
        AllGuiStyles.register(styleRegistry);
        AllGuiNodes.register(nodeTypeRegistry);
    }

    public HashSet<GuiInstance> instances = new HashSet<>();

    public GuiInstance create(ResourceLocation location){
        GuiInstance instance = new GuiInstance(location);
        this.instances.add(instance);
        return instance;
    }

    public void closeInstance(GuiInstance instance){
        this.instances.remove(instance);
    }

    public void renderTick() {
        for (GuiInstance instance : instances) {
            instance.getContext().ifPresent(GuiContext::renderTick);
        }
    }
}
