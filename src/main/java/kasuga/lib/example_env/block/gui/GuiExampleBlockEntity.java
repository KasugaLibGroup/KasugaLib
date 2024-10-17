package kasuga.lib.example_env.block.gui;

import kasuga.lib.core.menu.GuiBinding;
import kasuga.lib.core.menu.GuiMenu;
import kasuga.lib.core.menu.GuiMenuUtils;
import kasuga.lib.core.menu.targets.Target;
import kasuga.lib.core.menu.targets.WorldRendererTarget;
import kasuga.lib.example_env.AllExampleElements;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import java.util.UUID;

public class GuiExampleBlockEntity extends BlockEntity {
    GuiMenu menuEntry;
    UUID serverId;
    public GuiExampleBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(AllExampleElements.guiExampleTile.getType(), pPos, pBlockState);
        menuEntry = new GuiMenu((uuid)->GuiBinding.create(uuid).execute(new ResourceLocation("kasuga_lib","example")));
    }

    @Override
    public void setLevel(Level pLevel) {
        super.setLevel(pLevel);
        if(pLevel.isClientSide){
            menuEntry.createGuiInstance();
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, ()->()-> WorldRendererTarget.attach(menuEntry));
        }
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

    public void openScreen(){
        GuiMenuUtils.openScreen(menuEntry);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if(level.isClientSide())
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, ()->()-> WorldRendererTarget.detach(menuEntry));
    }
}
