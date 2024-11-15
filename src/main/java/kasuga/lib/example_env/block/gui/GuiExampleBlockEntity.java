package kasuga.lib.example_env.block.gui;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import kasuga.lib.core.menu.behaviour.BlockEntityMenuBehaviour;
import kasuga.lib.example_env.AllExampleElements;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class GuiExampleBlockEntity extends SmartBlockEntity {
    private BlockEntityMenuBehaviour menuBehaviour;
    
    public GuiExampleBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(AllExampleElements.guiExampleTile.getType(), pPos, pBlockState);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(menuBehaviour = new BlockEntityMenuBehaviour(this, () -> AllExampleElements.MENU_EXAMPLE.create()));
    }

    int clickCount = 0;

    public void incrementData() {
        CompoundTag syncData = new CompoundTag();
        syncData.putInt("click", ++clickCount);
        menuBehaviour.getMenuEntry().broadcast(syncData);
    }

    public void openScreen() {
        menuBehaviour.openScreen();
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, BlockEntity block) {
        GuiExampleBlockEntity entity = (GuiExampleBlockEntity)level.getBlockEntity(blockPos);
        if(level instanceof ServerLevel serverLevel){
            entity.incrementData();
        }
    }
}
