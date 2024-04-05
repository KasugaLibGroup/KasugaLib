package kasuga.lib.core.base;

import kasuga.lib.core.annos.Inner;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

/**
 * This class is created for any item which needs to customize rendering.
 * Customize rendering means that we would not use the vanilla renderer to finish our render process.
 * Needs more Examples? You could google "create" for their amazing custom rendered items.
 */
public abstract class CustomRenderedItem extends Item {

    /**
     * Use this to create your customRenderedItem.
     * @param pProperties The given itemProperties.
     */
    public CustomRenderedItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    @Inner
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return getCustomItemRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
            }
        });
    }

    /**
     * Override this to link to your custom renderer,
     * the renderer should be a subClass of {@link BlockEntityWithoutLevelRenderer}.
     * @param dispatcher the given render dispatcher.
     * @param modelSet the given model set.
     * @return your custom render renderer
     */
    public abstract BlockEntityWithoutLevelRenderer getCustomItemRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet);
}
