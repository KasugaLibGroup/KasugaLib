package kasuga.lib.core.client.frontend.gui;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.client.frontend.common.layouting.LayoutEngine;
import kasuga.lib.core.client.frontend.dom.DomContext;
import kasuga.lib.core.client.frontend.dom.registration.DOMPriorityRegistry;
import kasuga.lib.core.client.frontend.gui.layout.LayoutEngines;
import kasuga.lib.core.client.frontend.gui.nodes.GuiDomNode;
import kasuga.lib.core.client.frontend.gui.nodes.GuiDomRoot;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;

public class GuiContext extends DomContext<GuiDomNode,GuiDomRoot> {

    LayoutEngine<?,GuiDomNode> layoutEngine;

    GuiAttachTarget attachedTargets = new GuiAttachTarget();

    public GuiContext(DOMPriorityRegistry registry, ResourceLocation location) {
        super(registry, location);
        layoutEngine  = LayoutEngines.YOGA;
    }

    @Override
    protected GuiDomRoot createRoot() {
        return new GuiDomRoot(this);
    }

    @Override
    public GuiDomNode createNodeInternal(String name) {
        return KasugaLib.STACKS.GUI.nodeTypeRegistry.create(name, this);
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

    public void renderTick(){
        this.getRootNode().getLayoutManager().tick();
    }

    public LayoutEngine<?,GuiDomNode> getLayoutEngine(){
        return layoutEngine;
    }

    HashMap<Object,SourceInfo> info = new HashMap<>();

    public SourceInfo getSourceInfo(Object source) {
        return info.get(source);
    }

    public void setSourceInfo(Object source,SourceInfo sourceInfo) {
        this.info.put(source, sourceInfo);
    }
}
