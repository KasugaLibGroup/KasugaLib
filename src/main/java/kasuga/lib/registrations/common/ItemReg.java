package kasuga.lib.registrations.common;

import kasuga.lib.registrations.Reg;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ItemReg<T extends Item> extends Reg {

    @Nullable public ResourceLocation model;
    private boolean customRender = false;
    private ItemBuilder<T> builder;

    public final Item.Properties properties = new Item.Properties();
    private RegistryObject<T> registryObject = null;
    private MenuReg<?, ?, ?> menuReg = null;
    private final List<TagKey<?>> tags;

    public ItemReg(String registrationKey, @Nullable ResourceLocation model) {
        super(registrationKey);
        this.model = model;
        this.tags = new ArrayList<>();
    }

    public ItemReg<T> model(ResourceLocation location) {
        this.model = location;
        return this;
    }

    public ItemReg<T> shouldCustomRender(boolean flag) {
        customRender = flag;
        return this;
    }

    public ItemReg(String registrationKey) {
        super(registrationKey);
        this.model = null;
        this.tags = new ArrayList<>();
    }


    public static ItemReg<BlockItem> defaultBlockItem(BlockReg<?> blockReg, @Nullable ResourceLocation model) {
        ItemReg<BlockItem> reg = new ItemReg<BlockItem>(blockReg.registrationKey, model);
        reg.itemType((properties1 -> new BlockItem(blockReg.getBlock(), properties1)));
        return reg;
    }

    public static ItemReg<BlockItem> defaultBlockItem(BlockReg<?> blockReg) {
        ItemReg<BlockItem> reg = new ItemReg<BlockItem>(blockReg.registrationKey);
        reg.itemType((properties1 -> new BlockItem(blockReg.getBlock(), properties1)));
        return reg;
    }

    public ItemReg<T> itemType(ItemBuilder<? extends Item> builder) {
        this.builder = (ItemBuilder<T>) builder;
        return this;
    }

    public ItemReg<T> stackTo(int size) {
        properties.stacksTo(size);
        return this;
    }

    public ItemReg<T> tab(CreativeTabReg tab) {
        properties.tab(tab.getTab());
        return this;
    }

    public ItemReg<T> tab(CreativeModeTab tab) {
        properties.tab(tab);
        return this;
    }

    public <F extends AbstractContainerMenu, R extends Screen, U extends Screen & MenuAccess<F>> ItemReg<T>
    withMenu(String registrationKey, IContainerFactory<?> menu, MenuScreens.ScreenConstructor<?, ?> screen) {
        menuReg = new MenuReg<F, R, U>(registrationKey)
                .withMenuAndScreen((IContainerFactory<F>) menu, (MenuScreens.ScreenConstructor<F, U>) screen);
        return this;
    }

    public ItemReg<T> withMenu(MenuReg<?, ?, ?> menuReg) {
        this.menuReg = menuReg;
        return this;
    }

    public ItemReg<T> withTag(TagKey<Item> tag) {
        tags.add(tag);
        return this;
    }

    public String getIdentifier() {
        return "item";
    }

    public ItemReg<T> withProperty(PropertyIdentifier identifier) {
        identifier.apply(properties);
        return this;
    }

    public ItemReg<T> submit(SimpleRegistry registry) {
        if(model != null) {
            registry.modelMappings().addMapping(
                    new ResourceLocation(registry.namespace, "item/" + registrationKey + ".json"), model
            );
        }
        if(customRender)
            registry.stackCustomRenderedItemIn(this.registrationKey);
        registryObject = registry.item().register(registrationKey, () -> builder.build(properties));
        if(menuReg != null) {
            if(!registry.hasMenuCache(this.toString())) {
                registry.cacheMenuIn(menuReg);
            }
        }
        return this;
    }

    public T getItem() {
        return registryObject == null ? null : registryObject.get();
    }

    public RegistryObject<T> getRegistryObject() {
        return registryObject;
    }

    public ResourceLocation getModelLocation() {
        return model;
    }

    ItemBuilder<T> type(){return builder;}

    public interface PropertyIdentifier {
        void apply(Item.Properties properties);
    }

    public interface ItemBuilder<T extends Item> {
        T build(Item.Properties properties);
    }
}
