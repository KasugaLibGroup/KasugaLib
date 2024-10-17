package kasuga.lib.example_env.block.gui;

import kasuga.lib.example_env.AllExampleElements;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class GuiExampleBlock extends Block implements EntityBlock {
    public GuiExampleBlock(Properties pProperties) {
        super(pProperties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return AllExampleElements.guiExampleTile.getType().create(blockPos, blockState);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if(!pLevel.isClientSide)
            return InteractionResult.SUCCESS;
        if(pLevel.getBlockEntity(pPos) instanceof GuiExampleBlockEntity blockEntity) {
            blockEntity.openScreen();
        }
        return InteractionResult.SUCCESS;
    }
}
