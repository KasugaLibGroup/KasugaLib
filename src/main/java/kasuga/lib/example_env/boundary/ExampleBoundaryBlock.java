package kasuga.lib.example_env.boundary;

import com.simibubi.create.foundation.block.IBE;
import kasuga.lib.example_env.ExampleMain;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ExampleBoundaryBlock extends Block implements IBE<ExampleBoundaryBlockEntity> {
    public ExampleBoundaryBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public Class<ExampleBoundaryBlockEntity> getBlockEntityClass() {
        return ExampleBoundaryBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ExampleBoundaryBlockEntity> getBlockEntityType() {
        return ExampleBoundaryModule.EXAMPLE_BOUNDARY_BLOCK_ENTITY.getType();
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        IBE.onRemove(pState, pLevel, pPos, pNewState);
    }
}
