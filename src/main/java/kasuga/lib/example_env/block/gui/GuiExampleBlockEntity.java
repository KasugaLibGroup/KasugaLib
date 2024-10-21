package kasuga.lib.example_env.block.gui;

import kasuga.lib.core.menu.GuiBinding;
import kasuga.lib.core.menu.GuiMenu;
import kasuga.lib.core.menu.GuiMenuUtils;
import kasuga.lib.core.menu.JavascriptGuiMenu;
import kasuga.lib.core.menu.targets.Target;
import kasuga.lib.core.menu.targets.WorldRendererTarget;
import kasuga.lib.example_env.AllExampleElements;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import java.util.UUID;

public class GuiExampleBlockEntity extends BlockEntity{
    JavascriptGuiMenu menuEntry;
    UUID serverId;
    public GuiExampleBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(AllExampleElements.guiExampleTile.getType(), pPos, pBlockState);
        menuEntry = new JavascriptGuiMenu((uuid)->GuiBinding.create(uuid).execute(new ResourceLocation("kasuga_lib","example")));
    }

    @Override
    public void setLevel(Level pLevel) {
        super.setLevel(pLevel);
        initMenu(pLevel);
    }

    private void initMenu(Level pLevel) {
        if(pLevel.isClientSide){
            menuEntry.createGuiInstance();
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, ()->()-> WorldRendererTarget.attach(menuEntry));
            if(!menuEntry.hasRemote() && serverId != null){
                menuEntry.createConnection(serverId);
            }
        }

        menuEntry.listen(pLevel.isClientSide);
    }

    private void disposeMenu(){
        if(level.isClientSide())
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, ()->()-> WorldRendererTarget.detach(menuEntry));
        menuEntry.close();
        menuEntry.unlisten(level.isClientSide);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putUUID("KasugaMenuEntryId", menuEntry.getID());
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        if(tag.hasUUID("KasugaMenuEntryId")) {
            serverId = tag.getUUID("KasugaMenuEntryId");
            menuEntry.createConnection(serverId);
        }
    }

    int clickCount = 0;

    public void incrementData(){
        CompoundTag syncData = new CompoundTag();
        syncData.putInt("click", ++clickCount);
        menuEntry.send(syncData);
    }

    public void openScreen(){
        GuiMenuUtils.openScreen(menuEntry);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        disposeMenu();
    }

    public int i=0;

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, BlockEntity block) {
        GuiExampleBlockEntity entity = (GuiExampleBlockEntity)level.getBlockEntity(blockPos);
        entity.incrementData();
    }
}
