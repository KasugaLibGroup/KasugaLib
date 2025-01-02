package kasuga.lib.core.create.device.locator;

import net.minecraft.core.BlockPos;

import java.util.Objects;

public class CarriageBlockPosDeviceLocator extends TrainDeviceLocator {
    protected final BlockPos pos;

    public CarriageBlockPosDeviceLocator(BlockPos pos) {
        this.pos = pos;
    }

    public BlockPos getPos() {
        return pos;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof CarriageBlockPosDeviceLocator)) return false;
        CarriageBlockPosDeviceLocator that = (CarriageBlockPosDeviceLocator) object;
        return Objects.equals(pos, that.pos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pos);
    }
}
