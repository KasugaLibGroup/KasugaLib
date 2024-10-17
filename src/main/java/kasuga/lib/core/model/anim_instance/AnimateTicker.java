package kasuga.lib.core.model.anim_instance;

public class AnimateTicker {
    public final float playSpeed;
    public final AnimationInstance animation;
    private int starterTick, tick;
    private boolean moving;
    public AnimateTicker(AnimationInstance instance, float playSpeed) {
        this.playSpeed = playSpeed;
        this.animation = instance;
    }

    public void setTick(int tick) {
        this.tick = tick;
    }

    public void setStarterTick(int tick) {
        this.starterTick = tick;
    }

    public int getTick() {
        return tick;
    }

    public int getStarterTick() {
        return starterTick;
    }

    public float getPlaySpeed() {
        return playSpeed;
    }

    public boolean isMoving() {
        return moving;
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
    }

    public float tickToSec(float partial) {
        int offset = tick - starterTick;
        return ((float) offset + partial) / 20f;
    }

    protected void tick() {

    }
}
