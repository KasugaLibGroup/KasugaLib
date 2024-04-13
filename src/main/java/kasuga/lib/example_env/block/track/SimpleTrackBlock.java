package kasuga.lib.example_env.block.track;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.schematics.requirement.ISpecialBlockItemRequirement;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlock;
import com.simibubi.create.content.trains.track.*;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class SimpleTrackBlock extends TrackBlock implements IBE<TrackBlockEntity>, IWrenchable, ITrackBlock, ISpecialBlockItemRequirement, ProperWaterloggedBlock {
    @Nonnull private final BogeySelector selector;
    public SimpleTrackBlock(@Nonnull BogeySelector selector, Properties properties, TrackMaterial material) {
        super(properties, material);
        this.selector = selector;
    }

    @Override
    public BlockState getBogeyAnchor(BlockGetter world, BlockPos pos, BlockState state) {
        return selector.select(world, pos, state)
                .defaultBlockState().setValue(
                        BlockStateProperties.HORIZONTAL_AXIS,
                        state.getValue(SHAPE) == TrackShape.XO ? Direction.Axis.X : Direction.Axis.Z
                );
    }

    public static class Builder {
        private final NonNullSupplier<BogeySelector> selector;

        public Builder(NonNullSupplier<BogeySelector> selector) {
            this.selector = selector;
        }

        public Builder(Supplier<AbstractBogeyBlock<?>> bogey) {
            this.selector = () -> (a, b, c) -> bogey.get();
        }

        public SimpleTrackBlock build(Properties properties, TrackMaterial material) {
            return new SimpleTrackBlock(selector.get(), properties, material);
        }
    }

    public interface BogeySelector {
        AbstractBogeyBlock<?> select(BlockGetter world, BlockPos pos, BlockState state);
    }
}
