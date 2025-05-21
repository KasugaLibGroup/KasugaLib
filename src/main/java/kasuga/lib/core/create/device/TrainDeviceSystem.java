package kasuga.lib.core.create.device;

import net.minecraft.nbt.CompoundTag;

import java.util.Optional;

public class TrainDeviceSystem {

    protected final TrainDeviceManager manager;

    public TrainDeviceSystem(TrainDeviceManager manager) {
        this.manager = manager;
    }
    public void write(CompoundTag systemTag) {}

    public void read(CompoundTag systemTag) {}

    public void tick() {}

    public Optional<Double> beforeSpeed() {
        return Optional.empty();
    }

    public void notifySpeed(double speed) {}

    public boolean notifySingalBack() {
        return false;
    }

    public boolean notifySingalFront() {
        return false;
    }

    public boolean cancelSlowdown() {
        return false;
    }

    public void notifyDistance(double distance) {

    }
}
