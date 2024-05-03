package kasuga.lib.mixins.mixin.client;

import com.simibubi.create.content.trains.bogey.AbstractBogeyBlock;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlockEntity;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import com.simibubi.create.foundation.utility.NBTHelper;
import kasuga.lib.core.create.BogeyDataConstants;
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

    @Shadow(remap = false) abstract Direction getAssemblyDirection();

    @Redirect(method = "refreshAssemblyInfo", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"))
    public BlockState doGetBlockState(Level instance, BlockPos pos){
        BlockState state = instance.getBlockState(pos);
        if(state.getBlock() instanceof AbstractBogeyBlock<?>) {
            BlockEntity entity = instance.getBlockEntity(pos);
            if(entity instanceof AbstractBogeyBlockEntity){
                CompoundTag tag = ((AbstractBogeyBlockEntity) entity).getBogeyData();
                Direction d = getAssemblyDirection().getOpposite();

                // 这里改成 true 就可以返回正确的朝向数据，如果为 false 则不改变从 Carriage 返回的值
                // 以下为对应方向的转置顺序, 前者为从Carriage中拿到的方块，后者为正确朝向
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
