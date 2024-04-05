package kasuga.lib.core.base;

import kasuga.lib.core.annos.Beta;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Don't use.
 */
@Beta
public abstract class  TickingBlockEntity extends BlockEntity {
    public TickingBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    public abstract void tick();
}
