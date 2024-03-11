package kasuga.lib.example_env.client.item.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import kasuga.lib.core.client.render.model.SimpleModel;
import kasuga.lib.example_env.AllExampleElements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.ItemStack;

public class GreenAppleItemRenderer extends BlockEntityWithoutLevelRenderer {
    SimpleModel model;
    public GreenAppleItemRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet) {
        super(dispatcher, modelSet);
        model = AllExampleElements.greenAppleModel.getModel().clone();
        model.renderType(RenderType::solid);
    }

    @Override
    public void renderByItem(ItemStack item, ItemTransforms.TransformType transform,
                             PoseStack pose, MultiBufferSource buffer, int light, int overlay) {
        // super.renderByItem(pStack, pTransformType, pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
        model.render(pose, buffer, 0, 0, 0, light, overlay);
    }
}
