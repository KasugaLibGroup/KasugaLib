package kasuga.lib.core.util.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import kasuga.lib.core.KasugaLibClient;
import kasuga.lib.core.client.animation.neo_neo.VectorUtil;
import kasuga.lib.core.client.model.anim_model.AnimModel;
import kasuga.lib.core.client.model.model_json.UnbakedBedrockModel;
import kasuga.lib.core.client.render.SimpleColor;
import kasuga.lib.core.client.render.texture.Vec2f;
import kasuga.lib.core.util.LazyRecomputable;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.awt.*;

@Getter
public class PanelRenderer {

    private final Panel panel;
    private final Vec3 pos;

    private static final LazyRecomputable<AnimModel> panelModel = LazyRecomputable.of(() -> {
        UnbakedBedrockModel model = KasugaLibClient.panel.get();
        return new AnimModel(model.getGeometries().get(0), model.getMaterial(), RenderType.translucent());
    });

    private static final LazyRecomputable<AnimModel> arrowModel = LazyRecomputable.of(() -> {
        UnbakedBedrockModel model = KasugaLibClient.arrow.get();
        return new AnimModel(model.getGeometries().get(0), model.getMaterial(), RenderType.translucent());
    });

    public static Vector3f BASE_OFFSET = new Vector3f(11f / 16f, 0, 15f / 16f);

    public PanelRenderer(Vec3 normal, Vec3 pos) {
        panel = new Panel(pos, normal);
        this.pos = pos;
    }

    public PanelRenderer(Panel panel, Vec3 pos) {
        this.panel = panel;
        this.pos = pos;
    }

    public void render(PoseStack pose, MultiBufferSource buffer, int light, int overlay, float partial) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        panel.setNormal(player.getForward());
        Vec3 playerPos = player.getEyePosition(partial);
        panel.moveTo(playerPos);
        Quaternion quaternion = panel.getQuaternion();
        Vector3f position = new Vector3f(playerPos);
        pose.translate(position.x(), position.y(), position.z());
        pose.translate(-BASE_OFFSET.x(), 0, -BASE_OFFSET.z());
        pose.mulPose(quaternion);
        // pose.translate(BASE_OFFSET.x(), 0, BASE_OFFSET.z());
        if (!panel.valid())
            panelModel.get().setColor(SimpleColor.fromRGBInt(Color.RED.getRGB()));
        else
            panelModel.get().setColor(SimpleColor.fromRGBInt(Color.WHITE.getRGB()));
        panelModel.get().render(pose, buffer, light, overlay);

        if (!panel.valid()) return;
        arrowModel.get().render(pose, buffer, light, overlay);
    }
}
