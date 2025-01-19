package kasuga.lib.core.create.device.locator;

import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageContraption;
import net.minecraft.core.BlockPos;

public record InteractiveContext(
        Carriage carriage,
        CarriageContraption contraption,
        BlockPos blockPos
) {

}
