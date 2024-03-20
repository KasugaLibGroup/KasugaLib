package kasuga.lib.core.base;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.IItemRenderProperties;

import java.util.function.Consumer;

public abstract class CustomRenderedItem extends Item {

    public CustomRenderedItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
        // super.initializeClient(consumer);
        consumer.accept(
                new IItemRenderProperties() {
                    @Override
                    public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
                        return getCustomItemRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
                    }
                }
        );

    }

    public abstract BlockEntityWithoutLevelRenderer getCustomItemRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet);
}
