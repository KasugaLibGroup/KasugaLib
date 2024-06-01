package kasuga.lib.example_env.entity;

import net.minecraft.nbt.CompoundTag;

public class DoorControl {
    boolean leftFront, rightFront, leftBack, rightBack, midBack;

    public DoorControl() {
        leftFront = false;
        rightFront = false;
        leftBack = false;
        rightBack = false;
        midBack = false;
    }

    public void read(CompoundTag tag) {
        leftFront = tag.getBoolean("lf");
        leftBack = tag.getBoolean("lb");
        rightFront = tag.getBoolean("rf");
        rightBack = tag.getBoolean("rb");
        midBack = tag.getBoolean("mb");
    }

    public void write(CompoundTag tag) {
        tag.putBoolean("lf", leftFront);
        tag.putBoolean("lb", leftBack);
        tag.putBoolean("rf", rightFront);
        tag.putBoolean("rb", rightBack);
        tag.putBoolean("mb", midBack);
    }

    public void setLeftBack(boolean leftBack) {
        this.leftBack = leftBack;
    }

    public void setLeftFront(boolean leftFront) {
        this.leftFront = leftFront;
    }

    public void setRightFront(boolean rightFront) {
        this.rightFront = rightFront;
    }

    public void setRightBack(boolean rightBack) {
        this.rightBack = rightBack;
    }

    public void setMidBack(boolean midBack) {
        this.midBack = midBack;
    }

    public boolean isLeftFront() {
        return leftFront;
    }

    public boolean isLeftBack() {
        return leftBack;
    }

    public boolean isRightBack() {
        return rightBack;
    }

    public boolean isRightFront() {
        return rightFront;
    }

    public boolean isMidBack() {
        return midBack;
    }
}