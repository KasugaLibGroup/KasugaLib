package kasuga.lib.core.base;

import com.jozufozu.flywheel.core.PartialModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.content.trains.track.TrackMaterial;
import com.simibubi.create.content.trains.track.TrackShape;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import kasuga.lib.KasugaLib;
import kasuga.lib.registrations.create.TrackMaterialReg;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

public class CustomTrackRenderer<T extends TrackBlock> extends CustomBlockRenderer<T> {

    public TrackMaterial material = null;
    public TrackMaterialReg.TrackOffsetIdentifier offset = null;
    public static final Vec3 ONE = new Vec3(1, 0, 0);
    public static final Vec3 NEG_ONE = new Vec3(-1, 0, 0);
    public CustomTrackRenderer(Supplier<T> blockSupplier) {
        super(blockSupplier);
    }

    @Override
    public synchronized void render(BlockState state, BlockPos pos, BlockAndTintGetter level,
                                    PoseStack stack, VertexConsumer consumer, RenderType type,
                                    int light) {
        if (isBusy()) return;
        setBusy(true);
        if (material == null) {
            material = getBlock().getMaterial();
            offset = KasugaLib.STACKS.getCachedTrackMaterial(material).trackOffsets();
        }
        PartialModel left = material.getModelHolder().segment_left(),
                right = material.getModelHolder().segment_right(),
                tie = material.getModelHolder().tie();
        SuperByteBuffer leftBuffer = CachedBufferer.partial(left, state),
                rightBuffer = CachedBufferer.partial(right, state),
                tieBuffer = CachedBufferer.partial(tie, state);
        TrackShape shape = state.getValue(TrackBlock.SHAPE);
        Direction direction = getFacingMapping(shape);
        if (direction == Direction.DOWN) {
            setBusy(false);
            return;
        }
        float f = - direction.getOpposite().toYRot();
        stack.translate(.5, .06, .5);
        stack.mulPose(Axis.YP.rotationDegrees(f));
        stack.translate(-.5, 0, -.5);
        if (isAscending(shape)) {
            renderAscendingSegment(stack, consumer, leftBuffer, rightBuffer, tieBuffer);
        } else {
            renderSimpleSegment(stack, consumer, leftBuffer, rightBuffer, tieBuffer);
        }
        setBusy(false);
    }

    private void renderSimpleSegment(PoseStack stack, VertexConsumer consumer, SuperByteBuffer left, SuperByteBuffer right,
                                     SuperByteBuffer tie) {
        renderTrackSegment(stack, consumer, left, right, tie, offset);
        stack.translate(0, 0, .5);
        renderTrackSegment(stack, consumer, left, right, tie, offset);
    }

    private void renderAscendingSegment(PoseStack stack, VertexConsumer consumer, SuperByteBuffer left, SuperByteBuffer right,
                                        SuperByteBuffer tie) {
        stack.mulPose(Axis.XP.rotationDegrees(45f));
        stack.translate(0, 0.625, -.9);
        renderTrackSegment(stack, consumer, left, right, tie, offset);
        stack.translate(0, 0, .5);
        renderTrackSegment(stack, consumer, left, right, tie, offset);
        stack.translate(0, 0, .5);
        renderTrackSegment(stack, consumer, left, right, tie, offset);
    }

    private void renderTrackSegment(PoseStack stack, VertexConsumer consumer, SuperByteBuffer left, SuperByteBuffer right,
                                    SuperByteBuffer tie, TrackMaterialReg.TrackOffsetIdentifier identifier) {
        if (stack.clear()) stack.pushPose();
        left.translate(identifier.apply(ONE).add(.5, 0, 0));
        right.translate(identifier.apply(NEG_ONE).add(.5, 0, 0));
        left.renderInto(stack, consumer);
        right.renderInto(stack, consumer);
        tie.renderInto(stack, consumer);
    }

    private Direction getFacingMapping(TrackShape shape) {
        return switch (shape) {
            case AS, TS -> Direction.SOUTH;
            case AE, XO, TE -> Direction.EAST;
            case AN, ZO, TN -> Direction.NORTH;
            case AW, TW -> Direction.WEST;
            default -> Direction.DOWN;
        };
    }

    private boolean isAscending(TrackShape shape) {
        return switch (shape) {
            case AS, AE, AW, AN -> true;
            default -> false;
        };
    }
}
