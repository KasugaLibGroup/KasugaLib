package kasuga.lib.example_env.block.gui;

import kasuga.lib.core.menu.base.GuiBinding;
import kasuga.lib.core.menu.api.GuiMenuUtils;
import kasuga.lib.core.menu.base.GuiMenu;
import kasuga.lib.core.menu.targets.WorldRendererTarget;
import kasuga.lib.example_env.AllExampleElements;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import java.util.UUID;

public class GuiExampleBlockEntity extends BlockEntity{
    GuiMenu menuEntry;
    UUID serverId;
    public GuiExampleBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(AllExampleElements.guiExampleTile.getType(), pPos, pBlockState);
        menuEntry = AllExampleElements.MENU_EXAMPLE.create();
    }

    @Override
    public void setLevel(Level pLevel) {
        super.setLevel(pLevel);
        if(pLevel instanceof ServerLevel serverLevel){
            menuEntry.asServer();
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putUUID("KasugaMenuEntryId", menuEntry.asServer());
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        if(tag.hasUUID("KasugaMenuEntryId")) {
            serverId = tag.getUUID("KasugaMenuEntryId");
            if(this.menuEntry != null){
                this.menuEntry.close();
            }
            this.menuEntry = AllExampleElements.MENU_EXAMPLE.create();
            this.menuEntry.asClient(serverId);
        }
    }

    int clickCount = 0;

    public void incrementData(){
        CompoundTag syncData = new CompoundTag();
        syncData.putInt("click", ++clickCount);
        menuEntry.broadcast(syncData);
    }

    public void openScreen(){
        GuiMenuUtils.openScreen(menuEntry);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        menuEntry.close();
    }

    public int i=0;

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, BlockEntity block) {
        GuiExampleBlockEntity entity = (GuiExampleBlockEntity)level.getBlockEntity(blockPos);
        entity.incrementData();
    }
}
