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

        if(!level.isClientSide) {
            BlockState bogey_state = level.getBlockState(pos.above());
            if(bogey_state.getBlock() instanceof AbstractBogeyBlock<?>) {
                BlockEntity entity = level.getBlockEntity(pos.above());
                if(entity instanceof AbstractBogeyBlockEntity) {
                    CompoundTag tag = ((AbstractBogeyBlockEntity) entity).getBogeyData();
                    Direction d = getAssemblyDirection().getOpposite();

                    // 这里改成 true 就可以返回正确的朝向数据，如果为 false 则不改变从 Carriage 返回的值
                    // 以下为对应方向的转置顺序, 前者为从Carriage中拿到的方块，后者为正确朝向
                    // North -> West
                    // South -> East
                    // West -> South
                    // East -> North

                    if(false) {
                        d = Direction.fromYRot(d.toYRot() - 90);
                    }
                    NBTHelper.writeEnum(tag, BogeyDataConstants.BOGEY_ASSEMBLY_DIRECTION_KEY, d);
                    ((AbstractBogeyBlockEntity) entity).setBogeyData(tag);
                }
            }
        }

        BlockPos up = BlockPos.containing(track.getUpNormal(level, pos, state));
        BlockPos down = BlockPos.containing(track.getUpNormal(level, pos, state).scale(-1));

        boolean upsideDown =
                (player.getViewXRot(1.0F) < 0
                        && (track.getBogeyAnchor(level, pos, state)).getBlock()
                                instanceof AbstractBogeyBlock<?> bogey
                        && bogey.canBeUpsideDown());
        BlockPos targetPos = upsideDown ? pos.offset(down) : pos.offset(up);

        BlockEntity blockEntity = level.getBlockEntity(targetPos);

        if (!(blockEntity instanceof AbstractBogeyBlockEntity bogeyBlockEntity)) return;

        CompoundTag bogeyData = bogeyBlockEntity.getBogeyData();

        NBTHelper.writeEnum(bogeyData, BogeyDataConstants.BOGEY_ASSEMBLY_DIRECTION_KEY, getAssemblyDirection());

        bogeyBlockEntity.setBogeyData(bogeyData);
    }
}
