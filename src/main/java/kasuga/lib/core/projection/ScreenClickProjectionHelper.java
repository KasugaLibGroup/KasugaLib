package kasuga.lib.core.projection;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import org.joml.Matrix4f;


import kasuga.lib.core.util.data_type.Pair;
import kasuga.lib.mixins.mixin.client.MixinGameRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.ViewportEvent;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class ScreenClickProjectionHelper {
    public static Pair<Vec3, Vec3> getScreenClickProjection(double mouseX, double mouseY) {
        Minecraft mc = Minecraft.getInstance();
        GameRenderer renderer = mc.gameRenderer;
        Camera camera = renderer.getMainCamera();
        Window window = mc.getWindow();
        float pt = mc.getPartialTick();

        Entity camEntity = mc.getCameraEntity() != null ? mc.getCameraEntity() : mc.player;
        camera.setup(mc.level, camEntity,
                !mc.options.getCameraType().isFirstPerson(),
                mc.options.getCameraType().isMirrored(), pt);

        var camAngles = net.minecraftforge.client.ForgeHooksClient.onCameraSetup(renderer, camera, pt);


        Matrix4f proj = renderer.getProjectionMatrix(
                ((MixinGameRenderer) renderer).kasugalib$invokeGetFov(camera, pt, true)
        );

        PoseStack viewPS = new PoseStack();

        viewPS.mulPose(Axis.ZP.rotationDegrees(camAngles.getRoll()));
        viewPS.mulPose(Axis.XP.rotationDegrees(camera.getXRot()));
        viewPS.mulPose(Axis.YP.rotationDegrees(camera.getYRot() + 180.0F));
        Vec3 camPos = camera.getPosition();
        viewPS.translate((float) -camPos.x, (float) -camPos.y, (float) -camPos.z);
        Matrix4f view = viewPS.last().pose();

        Matrix4f vp = new Matrix4f().set(proj);
        // 等效于vp.multiply 方法
        vp.mul(view);
        Matrix4f invVP = new Matrix4f().set(vp);
        invVP.invert();

        double guiScale = window.getGuiScale();
        double w = window.getWidth();
        double h = window.getHeight();
        boolean isGuiCoords = mouseX <= w / guiScale + 1 && mouseY <= h / guiScale + 1;
        double px = isGuiCoords ? mouseX * guiScale : mouseX;
        double py = isGuiCoords ? mouseY * guiScale : mouseY;

        float ndcX = (float) (px / w * 2.0 - 1.0);
        float ndcY = (float) (-(py / h * 2.0 - 1.0));

        Vector4f near = new Vector4f(ndcX, ndcY, -1.0f, 1.0f);
        invVP.transform(near);
        near.mul(1.0f / near.w());

        Vector4f far = new Vector4f(ndcX, ndcY, 1.0f, 1.0f);
        invVP.transform(far);
        far.mul(1.0f / far.w());

        Vec3 origin = camPos;
        Vec3 dir = new Vec3(far.x() - near.x(), far.y() - near.y(), far.z() - near.z()).normalize();

        return Pair.of(origin, dir);
    }

}
