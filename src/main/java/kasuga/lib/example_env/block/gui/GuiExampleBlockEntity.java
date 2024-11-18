package kasuga.lib.example_env.block.gui;

import kasuga.lib.core.menu.base.GuiMenu;
import kasuga.lib.core.menu.locator.BlockMenuLocator;
import kasuga.lib.core.menu.locator.GuiMenuHolder;
import kasuga.lib.example_env.AllExampleElements;
import net.minecraft.CrashReport;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class GuiExampleBlockEntity extends BlockEntity {
    private GuiMenuHolder holder;
    
    public GuiExampleBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(AllExampleElements.guiExampleTile.getType(), pPos, pBlockState);
    }

    @Override
    public void setLevel(Level pLevel) {
        super.setLevel(pLevel);
        try{
            holder = new GuiMenuHolder.Builder()
                    .with(AllExampleElements.MENU_EXAMPLE)
                    .locatedAt(BlockMenuLocator.of(level, getBlockPos()))
                    .build();
            holder.enable(pLevel);
        }catch (Exception e){
            Minecraft.crash(new CrashReport("Failed to create GuiMenuHolder", e));
        }
    }

    int clickCount = 0;

    public void incrementData(){
        CompoundTag syncData = new CompoundTag();
        syncData.putInt("click", ++clickCount);
        holder.getMenu(0).ifPresent((menu)->menu.broadcast(syncData));
    }

    public void openScreen(){
        holder.openScreen(0);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        holder.disable();
    }

    @Override
    public void clearRemoved() {
        super.clearRemoved();
        holder.enable(level);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, BlockEntity block) {
        GuiExampleBlockEntity entity = (GuiExampleBlockEntity)block;
        if(level instanceof ServerLevel) {
            entity.incrementData();
        }
    }

    public Optional<GuiMenu> getMenu() {
        return holder.getMenu(0);
    }
}
