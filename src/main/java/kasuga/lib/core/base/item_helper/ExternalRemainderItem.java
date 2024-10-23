package kasuga.lib.core.base.item_helper;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class ExternalRemainderItem extends Item {

    @Nonnull
    private final Supplier<Item> craftingRemainder;
    public ExternalRemainderItem(Properties pProperties) {
        super(pProperties);
        if (pProperties instanceof ExternalProperties externalProperties)
            craftingRemainder = externalProperties.craftingRemainderItem;
        else
            craftingRemainder = () -> null;
    }

    @Nonnull
    public Supplier<Item> getCraftingRemainder() {
        return craftingRemainder;
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return super.hasCraftingRemainingItem(stack) || craftingRemainder.get() != null;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack itemStack) {
        ItemStack remain = null;
        try {
            remain = super.getCraftingRemainingItem(itemStack);
            return remain;
        } catch (Exception ignored) {}
        if (craftingRemainder.get() != null) {
            remain = craftingRemainder.get().getDefaultInstance();
        }
        return remain;
    }
}
