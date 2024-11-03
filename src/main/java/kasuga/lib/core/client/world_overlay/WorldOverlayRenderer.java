package kasuga.lib.core.client.world_overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.HashSet;

public class WorldOverlayRenderer {

    public static final WorldOverlayRenderer INSTANCE = new WorldOverlayRenderer();
    private static final HashSet<WorldOverlay> OVERLAYS = new HashSet<>();

    public static WorldOverlay add(WorldOverlay overlay) {
        OVERLAYS.add(overlay);
        return overlay;
    }

    public static boolean remove(WorldOverlay overlay) {
        return OVERLAYS.remove(overlay);
    }

    public void render(PoseStack poseStack, MultiBufferSource buffer, Level level, int overlay, float partial) {
        OVERLAYS.forEach((b) -> {
            if (!b.shouldRender()) return;
            int i;
            if (level != null) {
                Vec3 pos = b.getPosition();
                i = LevelRenderer.getLightColor(level, new BlockPos(pos));
            } else {
                i = 15728880;
            }
            LocalPlayer player = Minecraft.getInstance().player;
            poseStack.pushPose();
            Vec3 playerPos = player.getPosition(partial);
            poseStack.translate(-playerPos.x(), -playerPos.y(), -playerPos.z());
            b.render(poseStack, buffer, i, overlay, partial);
            poseStack.popPose();
        });

    }
}
