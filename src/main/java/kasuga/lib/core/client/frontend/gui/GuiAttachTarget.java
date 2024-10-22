package kasuga.lib.core.client.frontend.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class GuiAttachTarget implements Iterable<Object> {
    Set<Entity> entity = new HashSet<>();
    Set<BlockEntity> block = new HashSet<>();
    Set<Screen> screen = new HashSet<>();
    Set<Object> objects = new HashSet<>();
    int unattachedLivingTicks = 0;

    public void detach(){
        this.entity.clear();
        this.block.clear();
        this.screen.clear();
    }

    public boolean isClosable(){
        return isBlockEntitySourceClosable() && isEntitySourceClosable() && isScreenSourceClosable() && objects.isEmpty();
    }

    public boolean isBlockEntitySourceClosable(){
        return block
                .stream()
                .allMatch((block)->
                        !block.hasLevel() ||
                        block.isRemoved() ||
                        !block.getLevel().isLoaded(block.getBlockPos())
                );
    }

    public boolean isEntitySourceClosable(){
        return entity.stream().allMatch((e)->e.isRemoved() || !e.level().isLoaded(e.blockPosition()));
    }

    public boolean isScreenSourceClosable(){
        Minecraft minecraft = Minecraft.getInstance();
        return screen.isEmpty();
    }

    public int tickClosable(){
        if(this.isClosable())
            return this.unattachedLivingTicks++;
        return this.unattachedLivingTicks = 0;
    }

    public void attach(BlockEntity block){
        this.block.add(block);
    }

    public void attach(Screen screen){
        this.screen.add(screen);
    }

    public void attach(Entity entity){
        this.entity.add(entity);
    }

    public void detach(BlockEntity block){
        this.block.remove(block);
    }

    public void detach(Screen screen){
        this.screen.remove(null);
    }

    public void detach(Entity entity){
        this.entity.remove(entity);
    }

    public void attach(Object object){this.objects.add(object);}

    public void detach(Object object){this.objects.remove(object);}

    @NotNull
    @Override
    public Iterator<Object> iterator() {
        ArrayList<Object> list = new ArrayList<>();
        list.addAll(this.entity);
        list.addAll(this.block);
        list.addAll(this.screen);
        return list.iterator();
    }
}
