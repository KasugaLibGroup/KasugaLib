package kasuga.lib.core.base.item_helper;

import net.minecraft.world.item.Item;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class ExternalProperties extends Item.Properties {

    @Nonnull
    Supplier<Item> craftingRemainderItem = () -> null;

    public ExternalProperties craftRemainder(Supplier<Item> craftingReminderItem) {
        this.craftingRemainderItem = craftingReminderItem;
        return this;
    }

    /**
     * Don't use, use {@link ExternalProperties#craftRemainder(Supplier)} instead.
     * @param pCraftingRemainingItem Don't use.
     * @return self.
     */
    @Deprecated
    @Override
    public Item.Properties craftRemainder(Item pCraftingRemainingItem) {
        return this;
    }
}
