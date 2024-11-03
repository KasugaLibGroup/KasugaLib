package kasuga.lib.core.create.track_overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.content.trains.track.BezierConnection;
import kasuga.lib.core.client.world_overlay.WorldOverlay;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;

public abstract class TrackEdgeOverlay extends WorldOverlay {

    protected final TrackEdge edge;
    protected float percentage;
    public TrackEdgeOverlay(TrackEdge edge, float percentage) {
        this.edge = edge;
        this.percentage = percentage;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }

    public boolean isTurn() {
        return edge.isTurn();
    }

    public Vec3 getCurrentPosition() {
        TrackNode left = edge.node1, right = edge.node2;
        Vec3 leftPos = left.getLocation().getLocation();
        Vec3 rightPos = right.getLocation().getLocation();
        if (!isTurn()) {
            return leftPos.add(rightPos.subtract(leftPos).scale(percentage));
        }
        BezierConnection bezier = edge.getTurn();
        return bezier.getPosition(percentage);
    }

    @Override
    public abstract void render(PoseStack pose, MultiBufferSource buffer, int light, int overlay, float partial);
}
