package kasuga.lib.example_env.client.world_overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.trains.graph.TrackEdge;
import kasuga.lib.core.create.track_overlay.TrackEdgeOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;

public class TestTrackEdgeOverlay extends TrackEdgeOverlay {
    public TestTrackEdgeOverlay(TrackEdge edge, float percentage) {
        super(edge, percentage);
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource buffer, int light, int overlay, float partial) {
        super.applyTranslationAndRotation(pose);

        Font font = Minecraft.getInstance().font;
        font.drawInBatch("test", 0, 0, 0xff000000, false,
                pose.last().pose(), buffer, false, 0, light);
    }
}
