package kasuga.lib.core.client.frontend.gui;

import kasuga.lib.core.client.frontend.dom.DomContext;
import kasuga.lib.core.client.frontend.dom.nodes.DomNode;
import kasuga.lib.core.client.frontend.dom.registration.DOMPriorityRegistry;
import kasuga.lib.core.client.frontend.gui.nodes.GuiDomNode;
import kasuga.lib.core.client.frontend.gui.nodes.GuiDomRoot;
import net.minecraft.resources.ResourceLocation;

public class GuiContext extends DomContext<GuiDomNode,GuiDomRoot> {

    GuiAttachTarget attachedTargets = new GuiAttachTarget();

    public GuiContext(DOMPriorityRegistry registry, ResourceLocation location) {
        super(registry, location);
    }

    @Override
    protected GuiDomRoot createRoot() {
        return new GuiDomRoot();
    }

    @Override
    public DomNode createNodeInternal(String name) {
        return null;
    }


    public GuiAttachTarget getAttachedTargets() {
        return attachedTargets;
    }

    public void createSource(Object source) {
        this.getRootNode().getLayoutManager().addSource(source);
    }

    public void removeSource(Object source) {
        this.getRootNode().getLayoutManager().removeSource(source);
    }
}
