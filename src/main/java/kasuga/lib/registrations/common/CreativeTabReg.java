package kasuga.lib.registrations.common;

import kasuga.lib.core.annos.Mandatory;
import kasuga.lib.registrations.Reg;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashSet;
import java.util.function.Supplier;

/**
 * This is the registration for the creative mode tab. We firmly suggest that you should use this to register
 * your creative tab because the minecraft tab reg would be changed violently in 1.19.3. If you use the original
 * minecraft registration, it would be a big trouble for you to deal with your items with their tabs.
 * If you use this to create your tab, call {@link ItemReg#tab(CreativeTabReg)}, {@link BlockReg#tabTo(CreativeTabReg)}
 * or {@link FluidReg#tab(CreativeTabReg)}.
 */
public class CreativeTabReg extends Reg {
    CreativeModeTab.Builder builder;
    RegistryObject<CreativeModeTab> tabRegistryObject;
    private final HashSet<Supplier<Item>> items;
    /**
     * Use this to create your tab registration.
     * @param registrationKey the name or translation key of your tab.
     */
    public CreativeTabReg(String registrationKey) {
        super(registrationKey);
        builder = CreativeModeTab.builder();
        builder.title(Component.translatable("itemGroup." + registrationKey));
        items = new HashSet<>();
    }

    /**
     * Pass an ItemStack in to use it as your tab's icon.
     * If the item has an itemReg, use that reg with {@link CreativeTabReg#icon(ItemReg)}
     * @param icon the icon itemStack.
     * @return self.
     */
    @Mandatory
    public CreativeTabReg icon(Supplier<ItemStack> icon) {
        builder.icon(icon);
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
        builder.icon(() -> new ItemStack(reg.getItem()));
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
        builder.icon(() -> new ItemStack(itemRegistry.get()));
        return this;
    }

    public CreativeTabReg item(Supplier<Item> input) {
        items.add(input);
        return this;
    }

    public CreativeTabReg item(ItemReg<?> input) {
        items.add(input::getItem);
        return this;
    }

    public CreativeTabReg item(RegistryObject<Item> input) {
        items.add(input);
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
        builder.displayItems(
                (pParameters, pOutput) -> items.forEach(sup -> pOutput.accept(sup.get().getDefaultInstance()))
        );
        tabRegistryObject = registry.tab().register(registrationKey,builder::build);
        return this;
    }

    public CreativeModeTab getTab() {
        return tabRegistryObject.get();
    }

    @Override
    public String getIdentifier() {
        return "tab";
    }
}
