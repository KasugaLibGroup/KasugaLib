package kasuga.lib.core.client.frontend.gui;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.client.frontend.common.layouting.LayoutEngine;
import kasuga.lib.core.client.frontend.dom.DomContext;
import kasuga.lib.core.client.frontend.dom.registration.DOMPriorityRegistry;
import kasuga.lib.core.client.frontend.gui.layout.LayoutEngines;
import kasuga.lib.core.client.frontend.gui.nodes.GuiDomNode;
import kasuga.lib.core.client.frontend.gui.nodes.GuiDomRoot;
import kasuga.lib.core.client.frontend.rendering.RenderContext;
import kasuga.lib.core.javascript.Tickable;
import kasuga.lib.core.util.Callback;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayDeque;
import java.util.HashMap;

public class GuiContext extends DomContext<GuiDomNode,GuiDomRoot> implements Tickable {

    LayoutEngine<?,GuiDomNode> layoutEngine;

    GuiAttachTarget attachedTargets = new GuiAttachTarget();
    private ArrayDeque<Callback> queue = new ArrayDeque<>();

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
        return KasugaLib.STACKS.GUI.orElseThrow(IllegalStateException::new).nodeTypeRegistry.create(name, this);
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

    public void removeSourceInfo(Object source) {
        this.info.remove(source);
    }

    @Override
    public void tick() {
        while(!queue.isEmpty()){
            queue.poll().execute();
        }
        this.getRootNode().getLayoutManager().tick();
    }

    public void renderTick() {

    }

    public void render(Object source, RenderContext context){
        if(!ready){
            return;
        }
        getRootNode().render(source, context);
    }

    public void appendTask(Callback callback) {
        this.queue.add(callback);
    }
}
