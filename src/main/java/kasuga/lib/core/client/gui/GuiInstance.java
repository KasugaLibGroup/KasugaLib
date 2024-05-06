package kasuga.lib.core.client.gui;

import kasuga.lib.core.client.gui.thread.GuiContext;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public class GuiInstance {
    private final ResourceLocation mainFile;
    private final Supplier<GuiContext> contextSupplier;
    GuiContext context;
    Set<Object> targets = new HashSet<>();
    public GuiInstance(Supplier<GuiContext> guiContextSupplier, ResourceLocation mainFile) {
        this.contextSupplier = guiContextSupplier;
        this.mainFile = mainFile;
    }

    public void open(Entity entity){
        this.openInternal(entity);
        this.context.createSource(entity);
        this.context.getAttachedTargets().attach(entity);
        this.loadInternal();
    }

    private void loadInternal() {
        context.runAsync(mainFile);
    }

    public void open(BlockEntity block){
        this.openInternal(block);
        this.context.createSource(block);
        this.context.getAttachedTargets().attach(block);
        this.loadInternal();
    }

    public Screen createScreen(){
        KasugaScreen kasugaScreen = new KasugaScreen(this);
        this.openInternal(kasugaScreen);
        this.context.createSource(kasugaScreen);
        this.context.getAttachedTargets().attach(kasugaScreen);
        this.loadInternal();
        return kasugaScreen;
    }

    public void close(Screen screen){
        this.closeInternal(screen);
        this.context.removeSource(screen);
        this.context.getAttachedTargets().detach(screen);
    }

    public void close(Entity entity){
        this.closeInternal(entity);
        this.context.getAttachedTargets().detach(entity);
        this.context.removeSource(entity);
    }

    public void close(BlockEntity block){
        this.closeInternal(block);
        this.context.getAttachedTargets().detach(block);
        this.context.removeSource(block);
    }

    public void openInternal(Object target){
        if(this.context == null || this.context.closed()){
            this.run();
        }
        targets.add(target);
    }

    public void closeInternal(Object target){
        targets.remove(target);
        if(this.targets.isEmpty()){
            this.stop();
        }
    }

    public boolean isOpened(){
        return this.context != null && !this.context.closable();
    }

    protected void run(){
        this.context = contextSupplier.get();
    }

    protected void stop() {
        this.context.close();
        this.context = null;
    }

    public Optional<GuiContext> getContext() {
        return Optional.ofNullable(context);
    }
}
