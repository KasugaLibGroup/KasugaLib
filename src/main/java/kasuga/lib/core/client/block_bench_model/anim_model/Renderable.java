package kasuga.lib.core.client.block_bench_model.anim_model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import kasuga.lib.core.client.render.SimpleColor;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface Renderable {

    void render(PoseStack pose, VertexConsumer consumer, SimpleColor color, int light, int overlay);
}
