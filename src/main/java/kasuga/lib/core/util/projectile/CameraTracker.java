package kasuga.lib.core.util.projectile;

import lombok.Getter;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.world.phys.Vec3;

import static kasuga.lib.core.util.projectile.PanelRenderer.BASE_OFFSET_2;

@Getter
public class CameraTracker {

    private final Camera camera;
    private final Gui gui;

    public CameraTracker(Gui gui, Camera camera) {
        this.camera = camera;
        this.gui = gui;
    }

    public Ray test(float mouseX, float mouseY) {
        Vec3 vec = camera.getPosition();
        Vec3 forward = new Vec3(camera.getLookVector()).add(BASE_OFFSET_2);
        return new Ray(vec, forward);
    }


}
