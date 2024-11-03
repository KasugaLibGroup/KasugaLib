package kasuga.lib.core.client.world_overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public abstract class WorldOverlay {
    protected boolean shouldRender;
    protected Vec3 position;

    public WorldOverlay() {
        shouldRender = true;
        position = Vec3.ZERO;
    }

    public WorldOverlay(Vec3 position) {
        this.position = position;
        this.shouldRender = true;
    }

    public void setShouldRender(boolean shouldRender) {
        this.shouldRender = shouldRender;
    }

    public void setPosition(Vec3 position) {
        this.position = position;
    }

    public Vec3 getPosition() {
        return position;
    }

    public boolean shouldRender() {
        return shouldRender;
    }

    public abstract void render(PoseStack pose, MultiBufferSource buffer, int light, int overlay, float partial);
}
