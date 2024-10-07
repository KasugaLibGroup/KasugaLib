package kasuga.lib.mixins.mixin;

import com.simibubi.create.content.trains.bogey.AbstractBogeyBlock;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlockEntity;
import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import com.simibubi.create.content.trains.track.ITrackBlock;
import com.simibubi.create.foundation.utility.NBTHelper;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import kasuga.lib.core.create.BogeyDataConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(value = StationBlockEntity.class, remap = false)
public abstract class MixinStationBlockEntity extends BlockEntity {

    @Shadow(remap = false) abstract Direction getAssemblyDirection();

    @Shadow(remap = false)
    @Nullable
    public abstract GlobalStation getStation();
    @Shadow public abstract void invalidateCaps();

    @Shadow public LerpedFloat flag;
    @Unique private static AbstractBogeyBlockEntity oldBe = null;
    @Unique private static Player player = null;
    private static InteractionHand hand = null;

    private MixinStationBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(
            method = "trackClicked",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z",
                            shift = At.Shift.AFTER))
    public void addData(
            Player player,
            InteractionHand hand,
            ITrackBlock track,
            BlockState state,
            BlockPos pos,
            CallbackInfoReturnable<Boolean> cir) {
        if (level == null) return;

        BlockState bogey_state = level.getBlockState(pos.above());
        if(bogey_state.getBlock() instanceof AbstractBogeyBlock<?>) {
            BlockEntity entity = level.getBlockEntity(pos.above());
            if(entity instanceof AbstractBogeyBlockEntity) {
                CompoundTag tag = ((AbstractBogeyBlockEntity) entity).getBogeyData();
                Direction d = getAssemblyDirection().getOpposite();

                // switch it to true could get correct direction，if false it would not change the value that return from Carriage.
                // Mappings from Carriage to correct direction.
                // North -> West
                // South -> East
                // West -> South
                // East -> North

                NBTHelper.writeEnum(tag, BogeyDataConstants.BOGEY_ASSEMBLY_DIRECTION_KEY, d);
                ((AbstractBogeyBlockEntity) entity).setBogeyData(tag);
            }
        }
    }
}
