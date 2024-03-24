package kasuga.lib.core.base;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * This class is for CreativeModeTab functioning. Without this class we have to override the {@link CreativeModeTab#makeIcon()}
 * method over and over again only for apply our icons, which makes no scene.
 */
public class SimpleCreativeTab extends CreativeModeTab {
    public final Supplier<ItemStack> icon;

    /**
     * Use this to get a SimpleCreativeTab
     * @param label the name of your tab, usually a translation key.
     * @param icon the icon supplier. We would use this to get the icon automatically.
     */
    public SimpleCreativeTab(String label, @Nonnull Supplier<ItemStack> icon) {
        super(label);
        this.icon = icon;
    }

    @Override
    public ItemStack makeIcon() {
        return icon.get();
    }
}
