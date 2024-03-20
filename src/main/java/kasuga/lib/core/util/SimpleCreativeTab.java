package kasuga.lib.core.util;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class SimpleCreativeTab extends CreativeModeTab{

    public final Supplier<ItemStack> icon;
    public SimpleCreativeTab(String label, @Nonnull Supplier<ItemStack> icon) {
        super(label);
        this.icon = icon;
    }

    @Override
    public ItemStack makeIcon() {
        return icon.get();
    }
}
