package kasuga.lib.core.create.device;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.UUID;

public record TrainDeviceLocation(
   UUID trainId,
    int carriageIndex,
   BlockPos position
) {}
