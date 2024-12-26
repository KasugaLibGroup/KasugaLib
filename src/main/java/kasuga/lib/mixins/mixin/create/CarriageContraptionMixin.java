package kasuga.lib.mixins.mixin.create;

import com.simibubi.create.content.contraptions.actors.trainControls.ControlsBlock;
import com.simibubi.create.content.trains.entity.CarriageContraption;
import kasuga.lib.core.base.AllKasugaTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CarriageContraption.class, remap = false)
public class CarriageContraptionMixin {
    @Shadow private boolean forwardControls;

    @Shadow private boolean backwardControls;

    @Shadow private boolean sidewaysControls;

    @Shadow private Direction assemblyDirection;

    @Inject(
            method = "capture",
            at = @At(
                    value = "HEAD"
            )
    )
    public void beforeCapture(Level world, BlockPos pos, CallbackInfo ci){
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        captureTrainController(blockState, block);
        // if(block instanceof TrainDeviceProvider provider){
        //    TrainDeviceType device = provider.getDeviceType();
        //}
    }

    private void captureTrainController(BlockState blockState, Block block){
        if(blockState.is(AllKasugaTags.DRIVE_CONTROLLER_BLOCKS)){
            if(block.getStateDefinition().getProperty("facing") == null){
                forwardControls = true;
                backwardControls = true;
                return;
            }
            Direction facing = (Direction)blockState.getValue(ControlsBlock.FACING);
            if (facing.getAxis() != this.assemblyDirection.getAxis()) {
                sidewaysControls = true;
                return;
            }
            boolean forwards = facing == assemblyDirection;
            if (forwards) {
                this.forwardControls = true;
            } else {
                this.backwardControls = true;
            }
            return;
        }
    }
}
