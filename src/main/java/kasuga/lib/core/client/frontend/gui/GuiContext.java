package kasuga.lib.core.client.frontend.gui;

import com.caoccao.javet.annotations.V8Convert;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.client.frontend.common.layouting.LayoutEngine;
import kasuga.lib.core.client.frontend.dom.DomContext;
import kasuga.lib.core.client.frontend.dom.registration.DOMPriorityRegistry;
import kasuga.lib.core.client.frontend.gui.layout.LayoutEngines;
import kasuga.lib.core.client.frontend.gui.nodes.GuiDomNode;
import kasuga.lib.core.client.frontend.gui.nodes.GuiDomRoot;
import kasuga.lib.core.client.frontend.rendering.RenderContext;
import kasuga.lib.core.javascript.JavascriptContext;
import kasuga.lib.core.javascript.Tickable;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

@V8Convert()
public class GuiContext extends DomContext<GuiDomNode,GuiDomRoot> implements Tickable {

    private final GuiInstance guiInstance;
    LayoutEngine<?,GuiDomNode> layoutEngine;

    GuiAttachTarget attachedTargets = new GuiAttachTarget();
    ReentrantLock renderLock = new ReentrantLock();

    public volatile boolean isRendering;

    public GuiContext(GuiInstance guiInstance, DOMPriorityRegistry registry, ResourceLocation location) {
        super(registry, location);
        layoutEngine = LayoutEngines.YOGA;
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
        if(!isRendering){
            this.renderLock.lock();
            beforeRenderTick();
            this.renderLock.unlock();
        }
        super.tick();
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
}
