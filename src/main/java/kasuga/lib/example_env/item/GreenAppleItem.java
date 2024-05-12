package kasuga.lib.example_env.item;

import kasuga.lib.core.base.CustomRenderedItem;
import kasuga.lib.example_env.client.item.renderer.GreenAppleItemRenderer;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class GreenAppleItem extends CustomRenderedItem {

    public GreenAppleItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public BlockEntityWithoutLevelRenderer getCustomItemRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet) {
        return new GreenAppleItemRenderer(dispatcher, modelSet);
    }
}
