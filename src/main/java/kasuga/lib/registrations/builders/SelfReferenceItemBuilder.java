package kasuga.lib.registrations.builders;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

@FunctionalInterface
public interface SelfReferenceItemBuilder<I extends Item, B extends Block> {
    I build(B block, Item.Properties properties);
}
