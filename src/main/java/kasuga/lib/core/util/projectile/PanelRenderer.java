package kasuga.lib.core.util.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.KasugaLibClient;
import kasuga.lib.core.client.model.AnimModelLoader;
import kasuga.lib.core.client.model.anim_model.AnimModel;
import kasuga.lib.core.client.model.model_json.BedrockModel;
import kasuga.lib.core.client.render.SimpleColor;
import kasuga.lib.core.client.render.texture.Vec2f;
import kasuga.lib.core.util.Envs;
import kasuga.lib.core.util.LazyRecomputable;
import lombok.Getter;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.awt.*;
import java.util.ArrayList;

@Getter
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class PanelRenderer {

    private final Panel panel;
    private final Grid grid;
    private final Vec3 pos;
    private final CameraTracker tracker;

    private static final LazyRecomputable<AnimModel> panelModel = LazyRecomputable.of(
            () -> {
                AnimModel model = AnimModelLoader.INSTANCE.getModel(new ResourceLocation(KasugaLib.MOD_ID, "panel"));
                model.init();
                return model;
            }
    );

    private static final LazyRecomputable<AnimModel> arrowModel = LazyRecomputable.of(
            () -> {
                AnimModel model = AnimModelLoader.INSTANCE.getModel(new ResourceLocation(KasugaLib.MOD_ID, "arrow"));
                model.init();
                return model;
            }
    );

    private static final LazyRecomputable<AnimModel> arrow2Model = LazyRecomputable.of(
            () -> {
                AnimModel model = AnimModelLoader.INSTANCE.getModel(new ResourceLocation(KasugaLib.MOD_ID, "arrow_2"));
                model.init();
                return model;
            }
    );

    public static Vector3f BASE_OFFSET = new Vector3f(.5f, .5f, .5f);
    public static Vec3 BASE_OFFSET_2 = new Vec3(0, -1.61875, 0);
    public static Vec2f testRayPos = new Vec2f(.5f, .5f);

    private final ArrayList<Ray> rays;

    @SubscribeEvent
    public static void renderToWorld(RenderLevelStageEvent event) {
        if (!Envs.isDevEnvironment()) return;
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) return;
        PoseStack pose = event.getPoseStack();
        float partial = event.getPartialTick();
        RenderBuffers buffers = Minecraft.getInstance().renderBuffers();
        MultiBufferSource.BufferSource bufferSource = buffers.bufferSource();
        pose.pushPose();
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        Vec3 pos = player.getPosition(partial);
        pose.translate(- pos.x(), - pos.y(), - pos.z());
        KasugaLibClient.PANEL_RENDERERS.forEach(
                renderer -> {
                    pose.pushPose();
                    renderer.render(pose, bufferSource, LightTexture.FULL_BLOCK, 0, partial);
                    pose.popPose();
                }
        );
        pose.popPose();
    }

    public PanelRenderer(Vec3 normal, Vec3 pos) {
        panel = new Panel(pos, normal);
        grid = new Grid(panel, Vector3f.ZERO);
        this.pos = pos;
        this.rays = new ArrayList<>();
        tracker = new CameraTracker(Minecraft.getInstance().gui, Minecraft.getInstance().gameRenderer.getMainCamera());
    }

    public PanelRenderer(Panel panel, Vec3 pos, Grid grid, ArrayList<Ray> rays) {
        this.panel = panel;
        this.grid = grid;
        this.pos = pos;
        this.rays = rays;
        tracker = new CameraTracker(Minecraft.getInstance().gui, Minecraft.getInstance().gameRenderer.getMainCamera());
    }

    public void render(PoseStack pose, MultiBufferSource buffer, int light, int overlay, float partial) {
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        Vector3f lookVec = camera.getLookVector();
        panel.setNormal(new Vec3(lookVec));
        Vec3 position = camera.getPosition().add(BASE_OFFSET_2);
        // panel.moveTo(position);
        rays.add(grid.getNormalRay(testRayPos));
        grid.setO(new Vector3f(position));
        Quaternion quaternion = panel.getQuaternion();
        pose.pushPose();
        // pose.translate(position.x(), position.y(), position.z());
        pose.mulPose(quaternion);
        pose.translate(-BASE_OFFSET.x(), BASE_OFFSET.y(), -BASE_OFFSET.z());
        if (!panel.valid())
            panelModel.get().setColor(SimpleColor.fromRGBInt(Color.RED.getRGB()));
        else
            panelModel.get().setColor(SimpleColor.fromRGBInt(Color.WHITE.getRGB()));
        panelModel.get().render(pose, buffer, light, overlay);

        if (!panel.valid()) {
            pose.popPose();
            return;
        }
        pose.translate(0, .1f, 0);
        arrowModel.get().render(pose, buffer, light, overlay);

        Vec2f x = grid.getXAxis2d();
        Vec2f y = grid.getYAxis2d();

        arrow2Model.get().setAnimRot(new Vector3f(0, x.getRotation() * 180 / (float) Math.PI, 0));
        arrow2Model.get().render(pose, buffer, light, overlay);

        arrow2Model.get().setAnimRot(new Vector3f(0, y.getRotation() * 180 / (float) Math.PI, 0));
        arrow2Model.get().render(pose, buffer, light, overlay);

        pose.popPose();
        rays.clear();
    }
}
