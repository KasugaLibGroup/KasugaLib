package kasuga.lib.core.client.frontend.gui;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.client.frontend.common.layouting.LayoutBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class GuiInstance {

    private final ResourceLocation location;
    GuiContext context;

    public Set<Object> targets = new HashSet<>();

    public Map<Object, SourceInfo> sourceInfos = new HashMap<>();

    public Map<String, Object> contextObject = new HashMap<>();

    public GuiInstance(ResourceLocation location){
        this.location = location;
    }

    public void open(Object object){
        this.openInternal(object);
        this.context.createSource(object);
        this.context.getAttachedTargets().attach(object);
    }

    public void open(Entity entity){
        this.openInternal(entity);
        this.context.createSource(entity);
        this.context.getAttachedTargets().attach(entity);
    }


    public void open(BlockEntity block){
        this.openInternal(block);
        this.context.createSource(block);
        this.context.getAttachedTargets().attach(block);
    }

    public Screen createScreen(){
        GuiScreen screen = new GuiScreen(this);
        this.open(screen);
        return screen;
    }

    protected void open(GuiScreen screen){
        this.openInternal(screen);
        this.context.createSource(screen);
        this.context.getAttachedTargets().attach(screen);
    }

    public void close(Screen screen){
        this.context.removeSource(screen);
        this.context.getAttachedTargets().detach(screen);
        this.closeInternal(screen);
    }

    public void close(Entity entity){
        this.context.getAttachedTargets().detach(entity);
        this.context.removeSource(entity);
        this.closeInternal(entity);
    }

    public void close(BlockEntity block){
        this.context.getAttachedTargets().detach(block);
        this.context.removeSource(block);
        this.closeInternal(block);
    }

    public void close(Object object){
        this.context.getAttachedTargets().detach(object);
        this.context.removeSource(object);
        this.closeInternal(object);
    }

    public void openInternal(Object target){
        if(this.context == null){
            this.context = new GuiContext(this, KasugaLib.STACKS.GUI.orElseThrow(IllegalStateException::new).domRegistry, location);
            this.sourceInfos.forEach((source, info)->{
                this.context.setSourceInfo(source, info);
            });
            this.context.start();
        }
        updateSourceInfo(target,new SourceInfo(LayoutBox.ZERO));
        targets.add(target);
    }

    public void closeInternal(Object target){
        targets.remove(target);
        this.removeSourceInfo(target);
        if(this.targets.isEmpty()){
            this.stop();
        }
    }

    public boolean isOpened(){
        return this.context != null /* && !this.context.closable() */;
    }

    protected void stop() {
        this.context.stop();
        this.context = null;
    }

    public Optional<GuiContext> getContext() {
        return Optional.ofNullable(context);
    }

    public void updateSourceInfo(Object source, SourceInfo sourceInfo){
        this.sourceInfos.put(source, sourceInfo);
        getContext().ifPresent((c)->{
            c.setSourceInfo(source, sourceInfo);
        });
    }

    public void removeSourceInfo(Object source){
        this.sourceInfos.remove(source);
        getContext().ifPresent((c)->{
            c.removeSourceInfo(source);
        });
    }

    public void beforeRender(){
        if(this.context == null)
            return;
        this.context.isRendering = true;
        this.context.renderLock.lock();
    }

    public void afterRender(){
        if(this.context == null)
            return;
        this.context.renderLock.unlock();
        this.context.isRendering = false;
        this.context.getRenderer().getContext().ifPresent((c)->{
            c.dispatchBeforeRenderTick();
        });
    }

    public Object getModule(String contextModuleName) {
        return contextObject.get(contextModuleName);
    }

    public void putContextObject(String contextModuleName, Object object){
        contextObject.put(contextModuleName, object);
    }
}
