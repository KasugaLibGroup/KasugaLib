package kasuga.lib.example_env.block_entity;

import kasuga.lib.core.client.render.RendererUtil;
import kasuga.lib.example_env.AllExampleElements;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class GreenAppleTile extends BlockEntity {
    public GreenAppleTile(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    public GreenAppleTile(BlockPos pos, BlockState state) {
        this(AllExampleElements.greenApple.getBlockEntityReg().getType(), pos, state);
    }

    @Override
    public AABB getRenderBoundingBox() {
        return AABB.ofSize(RendererUtil.blockPos2Vec3(getBlockPos()), 5, 5, 5);
    }
}
