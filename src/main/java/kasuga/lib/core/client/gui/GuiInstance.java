package kasuga.lib.core.client.gui;

import kasuga.lib.core.client.gui.thread.GuiContext;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.graalvm.polyglot.Source;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public class GuiInstance {
    private ResourceLocation mainFile;
    private Source mainSource;
    private final Supplier<GuiContext> contextSupplier;
    GuiContext context;
    Set<Object> targets = new HashSet<>();
    public GuiInstance(Supplier<GuiContext> guiContextSupplier, ResourceLocation mainFile) {
        this.contextSupplier = guiContextSupplier;
        this.mainFile = mainFile;
    }

    public GuiInstance(Supplier<GuiContext> guiContextSupplier, Source source){
        this.contextSupplier = guiContextSupplier;
        this.mainSource = source;
    }

    public void open(Entity entity){
        this.openInternal(entity);
        this.context.createSource(entity);
        this.context.getAttachedTargets().attach(entity);
        this.loadInternal();
    }

    private void loadInternal() {
        if(mainFile!=null)
            context.runAsync(mainFile);
        else if(mainSource != null)
            context.runSource(mainSource);
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
