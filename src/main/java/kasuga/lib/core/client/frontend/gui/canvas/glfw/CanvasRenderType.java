package kasuga.lib.core.client.frontend.gui.canvas.glfw;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

import java.util.function.Function;

public class CanvasRenderType {
    // TODO: Needed to be checked.
    protected static final RenderStateShard.ShaderStateShard NEW_ENTITY_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeEntitySolidShader);
    protected static final RenderStateShard.TransparencyStateShard NO_TRANSPARENCY = new RenderStateShard.TransparencyStateShard("no_transparency", () -> {
        RenderSystem.disableBlend();
    }, () -> {
    });

    protected static final RenderStateShard.LightmapStateShard LIGHTMAP = new RenderStateShard.LightmapStateShard(true);
    protected static final RenderStateShard.OverlayStateShard OVERLAY = new RenderStateShard.OverlayStateShard(true);

    protected static final Function<Integer, RenderType> CANVAS = Util.memoize((target) -> {
        RenderType.CompositeState state = RenderType.CompositeState.builder()
                .setShaderState(NEW_ENTITY_SHADER)
                .setTextureState(new CanvasTextureState(target))
                .setTransparencyState(NO_TRANSPARENCY)
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .createCompositeState(true);
        return RenderType.create(
                "canvas",
                DefaultVertexFormat.NEW_ENTITY,
                VertexFormat.Mode.QUADS,
                256,
                true,
                true,
                state
        );
    });
}
