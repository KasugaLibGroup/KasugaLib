package kasuga.lib.core.create.track_overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.trains.graph.TrackNode;
import kasuga.lib.core.client.world_overlay.WorldOverlay;
import net.minecraft.client.renderer.MultiBufferSource;

public abstract class TrackNodeOverlay extends WorldOverlay {

    protected final TrackNode node;
    public TrackNodeOverlay(TrackNode node) {
        this.node = node;
        setPosition(node.getLocation().getLocation());
    }
    @Override
    public void render(PoseStack pose, MultiBufferSource buffer, int light, int overlay, float partial) {

    }
}
