package kasuga.lib.core.client.frontend.gui;

import kasuga.lib.KasugaLib;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public class GuiInstance {

    private final ResourceLocation location;
    GuiContext context;

    public Set<Object> targets = new HashSet<>();

    public GuiInstance(ResourceLocation location){
        this.location = location;
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
        this.openInternal(screen);
        this.context.createSource(screen);
        this.context.getAttachedTargets().attach(screen);
        return screen;
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
        if(this.context == null){
            this.context = new GuiContext(KasugaLib.STACKS.GUI.domRegistry, location);
            this.context.start();
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
        return this.context != null /* && !this.context.closable() */;
    }

    protected void stop() {
        this.context.stop();
        this.context = null;
    }

    public Optional<GuiContext> getContext() {
        return Optional.ofNullable(context);
    }
}
