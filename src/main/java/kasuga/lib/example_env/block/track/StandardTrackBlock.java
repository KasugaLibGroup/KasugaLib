package kasuga.lib.example_env.block.track;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.schematics.requirement.ISpecialBlockItemRequirement;
import com.simibubi.create.content.trains.track.*;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;

public class StandardTrackBlock extends TrackBlock
        implements IBE<TrackBlockEntity>,
                IWrenchable,
                ITrackBlock,
                ISpecialBlockItemRequirement,
                ProperWaterloggedBlock {

    public StandardTrackBlock(Properties properties, TrackMaterial material) {
        super(properties, material);
    }

    @Override
    public BlockState getBogeyAnchor(BlockGetter world, BlockPos pos, BlockState state) {
        return state;
        /*
        return TrackBlockInit.KY_STANDARD_BOGEY
                .getDefaultState()
                .setValue(
                        BlockStateProperties.HORIZONTAL_AXIS,
                        state.getValue(SHAPE) == TrackShape.XO
                                ? Direction.Axis.X
                                : Direction.Axis.Z);

         */
    }

    @Override
    public InteractionResult use(
            BlockState state,
            Level world,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit) {
        InteractionResult result = super.use(state, world, pos, player, hand, hit);
        if (result.consumesAction()) return result;

        if (!world.isClientSide && AllItems.BRASS_HAND.isIn(player.getItemInHand(hand))) {
            TrackPropagator.onRailAdded(world, pos, state);
            return InteractionResult.SUCCESS;
        }
        return result;
    }
}
