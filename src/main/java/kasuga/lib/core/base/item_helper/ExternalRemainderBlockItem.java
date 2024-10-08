package kasuga.lib.core.base.item_helper;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class ExternalRemainderBlockItem extends BlockItem {

    @Nonnull
    private final Supplier<Item> craftingRemainder;
    public ExternalRemainderBlockItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
        if (pProperties instanceof ExternalProperties externalProperties)
            craftingRemainder = externalProperties.craftingRemainderItem;
        else
            craftingRemainder = () -> null;
    }

    @Nonnull
    public Supplier<Item> getCraftingRemainder() {
        return craftingRemainder;
    }

    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return hasContainerItem(stack);
    }
    @Override
    public boolean hasContainerItem(ItemStack stack) {
        return super.hasContainerItem(stack) || craftingRemainder.get() != null;
    }

    public ItemStack getCraftingRemainingItem(ItemStack stack) {
        return getContainerItem(stack);
    }

    @Override
    public ItemStack getContainerItem(ItemStack itemStack) {
        ItemStack remain = super.getContainerItem(itemStack);
        if ((remain == ItemStack.EMPTY || remain.is(Items.AIR)) && craftingRemainder.get() != null) {
            remain = craftingRemainder.get().getDefaultInstance();
        }
        return remain;
    }
}
