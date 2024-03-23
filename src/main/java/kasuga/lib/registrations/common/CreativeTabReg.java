package kasuga.lib.registrations.common;

import kasuga.lib.registrations.Reg;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashSet;
import java.util.function.Supplier;

public class CreativeTabReg extends Reg {
    CreativeModeTab.Builder builder;
    private final HashSet<Supplier<Item>> items;
    public CreativeTabReg(String registrationKey) {
        super(registrationKey);
        builder = CreativeModeTab.builder();
        builder.title(Component.translatable("itemGroup." + registrationKey));
        items = new HashSet<>();
    }

    public CreativeTabReg icon(Supplier<ItemStack> icon) {
        builder.icon(icon);
        return this;
    }

    public CreativeTabReg icon(ItemReg<?> reg) {
        builder.icon(() -> new ItemStack(reg.getItem()));
        return this;
    }

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

    @Override
    public CreativeTabReg submit(SimpleRegistry registry) {
        builder.displayItems(
                (pParameters, pOutput) -> items.forEach(sup -> pOutput.accept(sup.get().getDefaultInstance()))
        );
        registry.tab().register(registrationKey,builder::build);
        return this;
    }

    @Override
    public String getIdentifier() {
        return "tab";
    }
}
