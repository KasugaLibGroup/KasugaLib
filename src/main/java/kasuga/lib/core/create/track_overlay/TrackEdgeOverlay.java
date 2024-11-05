package kasuga.lib.core.create.track_overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.content.trains.track.BezierConnection;
import kasuga.lib.core.client.animation.neo_neo.VectorUtil;
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
        return getPosition(this.percentage);
    }

    public Vec3 getPosition(double percentage) {
        TrackNode left = edge.node1, right = edge.node2;
        Vec3 leftPos = left.getLocation().getLocation();
        Vec3 rightPos = right.getLocation().getLocation();
        if (!isTurn()) {
            return leftPos.add(rightPos.subtract(leftPos).scale(percentage));
        }
        BezierConnection bezier = edge.getTurn();
        return bezier.getPosition(percentage);
    }

    public Quaternion getCurrentRotation() {
        float leftPercentage = this.percentage - 0.01f;
        float rightPercentage = this.percentage + 0.01f;
        Vec3 a = this.getPosition(Math.max(leftPercentage, 0));
        Vec3 b = this.getPosition(Math.min(rightPercentage, 1));
        Vec3 offset = b.subtract(a);
        Vector3f rot = VectorUtil.offsetToRotation(offset);
        rot.sub(new Vector3f(0, 90, 0));
        return Quaternion.fromXYZDegrees(rot);
    }

    public Quaternion getFlipRotation() {
        float leftPercentage = this.percentage - 0.01f;
        float rightPercentage = this.percentage + 0.01f;
        Vec3 a = this.getPosition(Math.max(leftPercentage, 0));
        Vec3 b = this.getPosition(Math.min(rightPercentage, 1));
        Vec3 offset = b.subtract(a);
        Vector3f rot = VectorUtil.offsetToRotation(offset);
        rot.add(new Vector3f(0, 90, 0));
        return Quaternion.fromXYZDegrees(rot);
    }

    public void applyTranslationAndRotation(PoseStack poseStack) {
        Vec3 pos = getCurrentPosition();
        poseStack.translate(pos.x(), pos.y(), pos.z());
        poseStack.mulPose(getCurrentRotation());
    }

    public void applyTranslationAndFlipRotation(PoseStack poseStack) {
        Vec3 pos = getCurrentPosition();
        poseStack.translate(pos.x(), pos.y(), pos.z());
        poseStack.mulPose(getFlipRotation());
    }

    @Override
    public abstract void render(PoseStack pose, MultiBufferSource buffer, int light, int overlay, float partial);
}
