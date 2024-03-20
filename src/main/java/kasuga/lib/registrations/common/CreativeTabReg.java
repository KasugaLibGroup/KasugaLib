package kasuga.lib.registrations.common;

import kasuga.lib.core.util.SimpleCreativeTab;
import kasuga.lib.registrations.Reg;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class CreativeTabReg extends Reg {
    public SimpleCreativeTab tab = null;
    public Supplier<ItemStack> iconSupplier = null;
    public CreativeTabReg(String registrationKey) {
        super(registrationKey);
    }

    public CreativeTabReg icon(Supplier<ItemStack> icon) {
        iconSupplier = icon;
        return this;
    }

    public CreativeTabReg icon(ItemReg<?> reg) {
        iconSupplier = () -> new ItemStack(reg.getItem());
        return this;
    }

    public CreativeTabReg icon(RegistryObject<Item> itemRegistry) {
        iconSupplier = () -> new ItemStack(itemRegistry.get());
        return this;
    }

    @Override
    public CreativeTabReg submit(SimpleRegistry registry) {
        tab = new SimpleCreativeTab(registrationKey, iconSupplier);
        registry.tab().put(registrationKey, tab);
        return this;
    }

    @Override
    public String getIdentifier() {
        return "tab";
    }
}
