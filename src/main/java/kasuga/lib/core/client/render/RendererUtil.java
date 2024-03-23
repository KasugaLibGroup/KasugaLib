package kasuga.lib.core.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;

public class RendererUtil {

    public static void moveToBlockHorizontalCenter(PoseStack stack, BlockState state) {
        if(stack.clear()) stack.popPose();
        stack.pushPose();
        stack.scale(1, -1, 1);
        stack.translate(.5, 0, .5);
        if(state.hasProperty(BlockStateProperties.FACING)) {
            float f = -state.getValue(BlockStateProperties.FACING).getOpposite().toYRot();
            stack.mulPose(Axis.YP.rotationDegrees(f));
        } else if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            float f = -state.getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite().toYRot();
            stack.mulPose(Axis.YP.rotationDegrees(f));
        }
    }

    public static Vec3 blockPos2Vec3(BlockPos pos) {
        return new Vec3(pos.getX(), pos.getY(), pos.getZ());
    }

    public static void facePlayer(PoseStack stack, Vec3 myPosition, Player player) {
        if(player == null) return;
        if(stack.clear()) stack.popPose();
        stack.pushPose();
        stack.scale(1, -1, 1);
        stack.translate(myPosition.x(), myPosition.y(), myPosition.z());
        stack.mulPose(Axis.YP.rotationDegrees((float) getVecHorizontalAngles(myPosition, player.getEyePosition())));
    }

    public static double getVecHorizontalAngles(Vec3 pos1, Vec3 pos2) {

        double horizontal_distance = distancexz(pos2, pos1);

        double dz = pos2.z() - pos1.z();

        double dx = pos2.x() - pos1.x();

        double degrees = Math.toDegrees(Math.asin(dz / horizontal_distance));

        degrees = degrees < 0 ? degrees + 360 : degrees;

        double result = dx < 0 ? 180 + degrees :360 - degrees;

        result = (result > 360 ? result - 360 : result);

        // float y_rotation = (float) Math.asin(dy/distance);

        return result - 90;
    }

    public static double distancexz(Vec3 point1, Vec3 point2) {

        double x1 = point1.x();
        double z1 = point1.z();

        double x2 = point2.x();
        double z2 = point2.z();

        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(z1 - z2, 2));
    }
}
