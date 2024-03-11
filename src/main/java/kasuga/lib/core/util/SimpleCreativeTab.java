package kasuga.lib.core.util;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class SimpleCreativeTab extends CreativeModeTab{

    public final ItemStack icon;
    public SimpleCreativeTab(String label, @Nonnull ItemStack icon) {
        super(label);
        this.icon = icon;
    }

    @Override
    public ItemStack makeIcon() {
        return icon;
    }
}
