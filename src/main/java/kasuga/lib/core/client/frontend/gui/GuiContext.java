package kasuga.lib.core.client.frontend.gui;

import com.caoccao.javet.annotations.V8Convert;
import com.mojang.blaze3d.vertex.PoseStack;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.client.frontend.common.layouting.LayoutEngine;
import kasuga.lib.core.client.frontend.dom.DomContext;
import kasuga.lib.core.client.frontend.dom.registration.DOMPriorityRegistry;
import kasuga.lib.core.client.frontend.gui.events.mouse.MouseDragEndEvent;
import kasuga.lib.core.client.frontend.gui.events.mouse.MouseUpEvent;
import kasuga.lib.core.client.frontend.gui.layout.LayoutEngines;
import kasuga.lib.core.client.frontend.gui.nodes.GuiDomNode;
import kasuga.lib.core.client.frontend.gui.nodes.GuiDomRoot;
import kasuga.lib.core.client.frontend.rendering.RenderContext;
import kasuga.lib.core.client.frontend.rendering.VertexBufferCache;
import kasuga.lib.core.compat.iris.IrisOculusCompat;
import kasuga.lib.core.javascript.JavascriptContext;
import kasuga.lib.core.javascript.Tickable;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

@V8Convert()
public class GuiContext extends DomContext<GuiDomNode,GuiDomRoot> implements Tickable {

    private final GuiInstance guiInstance;
    LayoutEngine<?,GuiDomNode> layoutEngine;

    GuiAttachTarget attachedTargets = new GuiAttachTarget();
    ReentrantLock renderLock = new ReentrantLock();
    HashMap<Object, VertexBufferCache> cache = new HashMap<>();
    public volatile boolean isRendering;
    private WeakHashMap<Object, Boolean> renderDirty = new WeakHashMap<>();

    public GuiContext(GuiInstance guiInstance, DOMPriorityRegistry registry, ResourceLocation location) {
        super(registry, location);
        layoutEngine = LayoutEngines.DEFAULT.create();
        this.guiInstance = guiInstance;
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
        cache.put(source, new VertexBufferCache());
        this.getRootNode().getLayoutManager().addSource(source);
    }

    public void removeSource(Object source) {
        cache.remove(source);
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
        if(!isRendering){
            this.renderLock.lock();
            beforeRenderTick();
            this.renderLock.unlock();
        }
        super.tick();
        this.getRootNode().getLayoutManager().tick();
    }

    public void renderTick() {
        this.renderDirty.clear();
    }

    public void render(Object source, RenderContext context){
        if(!ready){
            return;
        }

        VertexBufferCache cache = this.cache.get(source);

        if(cache == null){
            return;
        }

        VertexBufferCache.MultiBufferStore store = cache.getMultiBufferStore();
        if(store == null){
            return;
        }

        if(KasugaLib.STACKS.COMPATS.IRIS_OCULUS.map(IrisOculusCompat::isRenderingShadow).orElse(false)){
            return;
        }

        if(context.getContextType() == RenderContext.RenderContextType.SCREEN){
            KasugaLib.STACKS.COMPATS.IRIS_OCULUS.ifPresent((i)->i.pushExtendedVertexFormat(false));
        }

        try{
            if(!renderDirty.containsKey(source)){
                this.cachedRender(store, source, context);
                this.renderDirty.put(source, true);
            }
            if(context.getContextType() == RenderContext.RenderContextType.WORLD) {
                store.upload(context.poseMatrix(), context.getBufferSource());
            } else {
                context.pose().pushPose();
                context.pose().scale(1,-1,1);
                store.upload(context.poseMatrix());
                context.pose().popPose();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        if(context.getContextType() == RenderContext.RenderContextType.SCREEN){
            KasugaLib.STACKS.COMPATS.IRIS_OCULUS.ifPresent((i)->i.popExtendedVertexFormat());
        }

    }

    private void cachedRender(VertexBufferCache.MultiBufferStore store, Object source, RenderContext context) {
        RenderContext fakeContext = new RenderContext(RenderContext.RenderContextType.WORLD);
        fakeContext.setPoseStack(new PoseStack());
        fakeContext.setMouseContext(context.mouse());
        fakeContext.pushLight(context.getLight());
        fakeContext.setBufferSource(store);
        fakeContext.setSource(context.source);
        store.begin();
        getRootNode().render(source, fakeContext);
        store.gc();
    }


    Queue<Runnable> afterRenderTickTasks = new ArrayDeque<>(32);

    public void beforeRenderTick(){
        Runnable task;
        while((task = afterRenderTickTasks.poll()) != null){
            task.run();
        }
    }

    public void enqueueAfterRenderTask(Runnable runnable) {
        this.afterRenderTickTasks.add(runnable);
    }

    public void queueDuringRender(Runnable task) {
        Optional<JavascriptContext> threadContext = this.getRenderer().getContext();
        if(threadContext.isEmpty()){
            task.run();
            return;
        }
        JavascriptContext context = threadContext.get();
        if(!this.isRendering){
            this.renderLock.lock();
            beforeRenderTick();
            RuntimeException _e = null;
            try{
                task.run();
            }catch (RuntimeException e){
                e.printStackTrace();
            }
            this.renderLock.unlock();
        }else{
            enqueueAfterRenderTask(task);
        }
    }

    public void queueDuringRenderUnsafe(Runnable task){
        Optional<JavascriptContext> threadContext = this.getRenderer().getContext();
        if(threadContext.isEmpty()){
            task.run();
            return;
        }
        JavascriptContext context = threadContext.get();
        if(!this.isRendering){
            beforeRenderTick();
            task.run();
            this.renderLock.unlock();
        }else{
            enqueueAfterRenderTask(task);
        }
    }

    @Override
    public Object getContextModuleNative(String contextModuleName) {
        return this.guiInstance.getModule(contextModuleName);
    }

    List<GuiDomNode> activateElements = List.of();

    public void setActivateElement(GuiDomNode node) {
        activateElements = List.of(node);
    }

    public void removeActivateElement() {
        activateElements = List.of();
    }

    public void removeActivateElement(MouseUpEvent $event){
        MouseDragEndEvent event = MouseDragEndEvent.fromMouseUp($event);
        for (GuiDomNode activateElement : activateElements) {
            activateElement.dispatchEvent(event.getType(), event.withTarget(activateElement));
        }
        activateElements = List.of();
    }

    public List<GuiDomNode> getActivateElements() {
        return activateElements;
    }
}
