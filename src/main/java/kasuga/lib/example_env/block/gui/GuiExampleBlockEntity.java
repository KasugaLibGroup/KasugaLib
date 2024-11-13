package kasuga.lib.example_env.block.gui;

import kasuga.lib.core.menu.IBlockEntityMenuHolder;
import kasuga.lib.core.menu.api.GuiMenuUtils;
import kasuga.lib.core.menu.base.GuiMenu;
import kasuga.lib.core.menu.network.BlockEntityMenuIdSyncPacket;
import kasuga.lib.core.packets.AllPackets;
import kasuga.lib.example_env.AllExampleElements;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PacketDistributor;

import java.util.UUID;

public class GuiExampleBlockEntity extends BlockEntity implements IBlockEntityMenuHolder {
    GuiMenu menuEntry;
    UUID serverId;
    public GuiExampleBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(AllExampleElements.guiExampleTile.getType(), pPos, pBlockState);
        menuEntry = AllExampleElements.MENU_EXAMPLE.create();
    }

    @Override
    public void setLevel(Level pLevel) {
        super.setLevel(pLevel);
        if(pLevel != null && pLevel instanceof ServerLevel serverLevel){
            menuEntry.asServer();
            sendMenuIdUpdate();
        }
    }

    public void sendMenuIdUpdate() {
        if (level instanceof ServerLevel) {
            UUID serverId = menuEntry.asServer();
            BlockEntityMenuIdSyncPacket packet = new BlockEntityMenuIdSyncPacket(
                serverId, 
                worldPosition, 
                level.dimension()
            );
            AllPackets.CHANNEL_REG.getChannel().send(
                    PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(worldPosition)),
                    packet
            );
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        if (level instanceof ServerLevel) {
            CompoundTag tag = super.getUpdateTag();
            tag.putUUID("menuId", menuEntry.getServerId());
            return tag;
        } else {
            return super.getUpdateTag();
        }
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        if(tag.hasUUID("menuId")){
            serverId = tag.getUUID("menuId");
            notifyMenuId(serverId);
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
        if(level instanceof ServerLevel serverLevel){
            entity.incrementData();
        }
    }

    @Override
    public void notifyMenuId(UUID menuId) {
        if(menuId.equals(this.menuEntry.getServerId()))
            return;
        if(this.menuEntry != null){
            this.menuEntry.close();
        }
        this.menuEntry = AllExampleElements.MENU_EXAMPLE.create();
        this.menuEntry.asClient(menuId);
    }
}
