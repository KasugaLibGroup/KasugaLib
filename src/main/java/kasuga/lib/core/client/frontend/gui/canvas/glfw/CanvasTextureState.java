package kasuga.lib.core.client.frontend.gui.canvas.glfw;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.RenderStateShard;
import org.lwjgl.opengl.GL11;

public class CanvasTextureState extends RenderStateShard.EmptyTextureStateShard {
    static void bind(int colorTextureId){
        if (!RenderSystem.isOnRenderThreadOrInit()) {
            RenderSystem.recordRenderCall(() -> {
                GlStateManager._bindTexture(colorTextureId);
            });
        } else {
            GlStateManager._bindTexture(colorTextureId);
        }
    }
    public CanvasTextureState(int colorTextureId) {
        super(()->{
            RenderSystem.enableBlend();
            bind(colorTextureId);
            GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            RenderSystem.setShaderTexture(0, colorTextureId);
        },()->{});
    }
}
