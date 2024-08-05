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
    @Inject(method = "renderBatched(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/RandomSource;Lnet/minecraftforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)V", at = @At("HEAD"), remap = false)
    public void doRenderBatched(
            BlockState state, BlockPos pos, BlockAndTintGetter level, PoseStack stack,
            VertexConsumer consumer, boolean checkSides, RandomSource random,
            ModelData modelData, RenderType renderType, CallbackInfo ci) {
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
        if (!renderer.skipOriginalModelRenderering(state, pos, level, renderType)) shouldRenderOriginModel = true;
        if (!renderer.shouldRender(state, pos, level, renderType)) return;
        renderer.render(state, pos, level, stack, consumer, renderType, level.getBrightness(LightLayer.BLOCK, pos));
        shouldRenderOriginModel = false;
    }

    @Redirect(method = "renderBatched(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/RandomSource;Lnet/minecraftforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/block/ModelBlockRenderer;tesselateBlock(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/RandomSource;JILnet/minecraftforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)V"),
            remap = false)
    public void doTesslate(ModelBlockRenderer instance, BlockAndTintGetter crashreportcategory, BakedModel throwable, BlockState state, BlockPos pLevel, PoseStack pModel, VertexConsumer pState, boolean pPos, RandomSource pPoseStack, long pConsumer, int pCheckSides, ModelData pRandom, RenderType pSeed) {
        if (shouldRenderOriginModel) instance.tesselateBlock(crashreportcategory, throwable, state, pLevel, pModel, pState, pPos, pPoseStack, pConsumer, pCheckSides, pRandom, pSeed);
    }
}
