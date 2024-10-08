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
    public boolean hasContainerItem(ItemStack stack) {
        return super.hasContainerItem(stack) || craftingRemainder.get() != null;
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
