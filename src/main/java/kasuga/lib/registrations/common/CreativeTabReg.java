package kasuga.lib.registrations.common;

import kasuga.lib.core.annos.Mandatory;
import kasuga.lib.core.base.SimpleCreativeTab;
import kasuga.lib.registrations.Reg;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

/**
 * This is the registration for the creative mode tab. We firmly suggest that you should use this to register
 * your creative tab because the minecraft tab reg would be changed violently in 1.19.3. If you use the original
 * minecraft registration, it would be a big trouble for you to deal with your items with their tabs.
 * If you use this to create your tab, call {@link ItemReg#tab(CreativeTabReg)}, {@link BlockReg#tabTo(CreativeTabReg)}
 * or {@link FluidReg#tab(CreativeTabReg)} to register your item to this tab. This reg would create an
 * {@link SimpleCreativeTab} instance for you.
 */
public class CreativeTabReg extends Reg {
    public SimpleCreativeTab tab = null;
    public Supplier<ItemStack> iconSupplier = null;

    /**
     * Use this to create your tab registration.
     * @param registrationKey the name or translation key of your tab.
     */
    public CreativeTabReg(String registrationKey) {
        super(registrationKey);
    }

    /**
     * Pass an ItemStack in to use it as your tab's icon.
     * If the item has an itemReg, use that reg with {@link CreativeTabReg#icon(ItemReg)}
     * @param icon the icon itemStack.
     * @return self.
     */
    @Mandatory
    public CreativeTabReg icon(Supplier<ItemStack> icon) {
        iconSupplier = icon;
        return this;
    }

    /**
     * Pass an itemStack in to use it as your tab's icon.
     * If your item is registered in other way, use {@link CreativeTabReg#icon(Supplier)}
     * @param reg the item registration.
     * @return self.
     */
    @Mandatory
    public CreativeTabReg icon(ItemReg<?> reg) {
        iconSupplier = () -> new ItemStack(reg.getItem());
        return this;
    }

    /**
     * If your icon is registered from original forge registry, use this method to use it as icon.
     * For other usage, see {@link CreativeTabReg#icon(Supplier)} or {@link CreativeTabReg#icon(ItemReg)}
     * @param itemRegistry the registry object of your item.
     * @return self.
     */
    @Mandatory
    public CreativeTabReg icon(RegistryObject<Item> itemRegistry) {
        iconSupplier = () -> new ItemStack(itemRegistry.get());
        return this;
    }

    /**
     * Pass your configs to forge and minecraft registry.
     * @param registry the mod SimpleRegistry.
     * @return self.
     */
    @Override
    @Mandatory
    public CreativeTabReg submit(SimpleRegistry registry) {
        tab = new SimpleCreativeTab(registrationKey, iconSupplier);
        registry.tab().put(registrationKey, tab);
        return this;
    }

    public SimpleCreativeTab getTab() {
        return tab;
    }

    @Override
    public String getIdentifier() {
        return "tab";
    }
}
