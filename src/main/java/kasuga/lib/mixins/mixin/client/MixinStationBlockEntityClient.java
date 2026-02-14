package kasuga.lib.mixins.mixin.client;

import com.simibubi.create.content.trains.bogey.AbstractBogeyBlock;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlockEntity;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import kasuga.lib.core.create.BogeyDataConstants;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(StationBlockEntity.class)
public abstract class MixinStationBlockEntityClient {

    @Shadow(remap = false)
    public abstract Direction getAssemblyDirection();

    @Redirect(method = "refreshAssemblyInfo", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"))
    public BlockState doGetBlockState(Level instance, BlockPos pos){
        BlockState state = instance.getBlockState(pos);
        if(state.getBlock() instanceof AbstractBogeyBlock<?>) {
            BlockEntity entity = instance.getBlockEntity(pos);
            if(entity instanceof AbstractBogeyBlockEntity){
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
        return state;
    }
}
