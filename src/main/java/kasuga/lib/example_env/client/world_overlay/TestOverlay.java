package kasuga.lib.example_env.client.world_overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import kasuga.lib.core.client.world_overlay.WorldOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;

public class TestOverlay extends WorldOverlay {

    public TestOverlay() {
        super();
    }
    @Override
    public void render(PoseStack pose, MultiBufferSource buffer, int light, int overlay, float partial) {
        Font font = Minecraft.getInstance().font;
        font.drawInBatch("test", 0, 0, 0xff000000,
                false, pose.last().pose(), buffer, false,
                0, light);
    }
}
