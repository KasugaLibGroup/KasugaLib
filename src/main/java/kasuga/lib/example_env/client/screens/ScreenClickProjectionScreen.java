package kasuga.lib.example_env.client.screens;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.KasugaLibStacks;
import kasuga.lib.core.client.interaction.GuiOperatingPerspectiveScreen;
import kasuga.lib.core.projection.ScreenClickProjectionHelper;
import kasuga.lib.core.util.data_type.Pair;
import kasuga.lib.core.util.projectile.Ray;
import kasuga.lib.core.util.projectile.RayRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ScreenClickProjectionScreen extends GuiOperatingPerspectiveScreen {

    protected static Ray ray = new Ray(new Vec3(0,0,0), new Vec3(0,0,1));

    protected static RayRenderer rayRenderer = new RayRenderer(ray);
    public ScreenClickProjectionScreen() {
        super();
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if(pButton == 1){
            Pair<Vec3, Vec3> vec3Pair = ScreenClickProjectionHelper.getScreenClickProjection(pMouseX, pMouseY);
            ray.getSource().set((float) vec3Pair.getFirst().x, (float) vec3Pair.getFirst().y, (float) vec3Pair.getFirst().z);
            ray.getForward().set((float) vec3Pair.getSecond().x, (float) vec3Pair.getSecond().y, (float) vec3Pair.getSecond().z);
            ray.getForward().normalize();
            return true;
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @SubscribeEvent
    public static void renderToWorld(RenderLevelStageEvent event) {
        RenderBuffers buffers = Minecraft.getInstance().renderBuffers();
        MultiBufferSource.BufferSource bufferSource = buffers.bufferSource();
        Vec3 pos = event.getCamera().getPosition();
        event.getPoseStack().translate(-pos.x(), -pos.y(), -pos.z());
        rayRenderer.render(event.getPoseStack(), bufferSource, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
    }
}
