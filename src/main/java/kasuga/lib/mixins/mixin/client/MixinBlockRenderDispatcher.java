package kasuga.lib.mixins.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.base.CustomBlockRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockRenderDispatcher.class)
public class MixinBlockRenderDispatcher {
    private static boolean shouldRenderOriginModel = false;
    @Inject(method = "renderBatched(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/RandomSource;Lnet/minecraftforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;Z)V", at = @At("HEAD"), remap = false)
    public void doRenderBatched(BlockState state, BlockPos pos, BlockAndTintGetter level, PoseStack pose, VertexConsumer consumer,
                               boolean checkSides, RandomSource random, ModelData modelData, RenderType renderType,
                               boolean queryModelSpecificData, CallbackInfo ci) {
        RenderShape shape = state.getRenderShape();
        if (shape == RenderShape.INVISIBLE) {
            return;
        }
        Block block = state.getBlock();
        CustomBlockRenderer renderer = KasugaLib.STACKS.getBlockRenderer(block);
        if (renderer == null) {
            shouldRenderOriginModel = true;
            return;
        }
        if (!renderer.shouldRender(state, pos, level, renderType)) return;
        renderer.render(state, pos, level, pose, consumer, renderType, level.getBrightness(LightLayer.BLOCK, pos));
        shouldRenderOriginModel = false;
    }

    @Redirect(method = "renderBatched(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/RandomSource;Lnet/minecraftforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/ModelBlockRenderer;tesselateBlock(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/RandomSource;JILnet/minecraftforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;Z)V"), remap = false)
    public void doTesslate(ModelBlockRenderer instance, BlockAndTintGetter crashreportcategory, BakedModel throwable, BlockState state, BlockPos p_111048_, PoseStack p_111049_, VertexConsumer p_111050_, boolean p_111051_, RandomSource p_111052_, long p_111053_, int p_111054_, ModelData p_111055_, RenderType p_111056_, boolean p_111057_) {
        if (shouldRenderOriginModel) instance.tesselateBlock(crashreportcategory, throwable, state, p_111048_, p_111049_, p_111050_, p_111051_, p_111052_, p_111053_, p_111054_, p_111055_, p_111056_, p_111057_);
    }
}
