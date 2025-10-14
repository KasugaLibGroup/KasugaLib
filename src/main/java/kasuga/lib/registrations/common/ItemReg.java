package kasuga.lib.registrations.common;

import kasuga.lib.core.annos.Mandatory;
import kasuga.lib.core.annos.Optional;
import kasuga.lib.core.base.item_helper.ExternalProperties;
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
import java.util.function.Supplier;

/**
 * 物品是minecraft的重要基础元素。
 * @param <T> 你的物品类。
 * Item is an important basic element of minecraft.
 * @param <T> the class of your item.
 */
public class ItemReg<T extends Item> extends Reg {

    @Nullable public ResourceLocation model;
    private boolean customRender = false;
    private ItemBuilder<T> builder;

    public final Item.Properties properties = new ExternalProperties();
    private RegistryObject<T> registryObject = null;
    private MenuReg<?, ?> menuReg = null;
    private final List<TagKey<?>> tags;
    boolean registerMenu = false;

    /**
     * 创建一个物品注册机。
     * <p>
     * 注意: 如果你需要为你的物品设置合成遗留，请使用
     * <p>
     * {@link kasuga.lib.core.base.item_helper.ExternalRemainderItem} 或
     * <p>
     * {@link kasuga.lib.core.base.item_helper.ExternalRemainderBlockItem}，
     * <p>
     * 然后使用 {@link ExternalProperties#craftRemainder(Supplier)}
     * @param registrationKey 你的物品的注册名。
     * @param model 如果你的物品的模型不在 "namespace:models/item" 文件夹下，请在这里传入位置，
     *              注意你的模型必须在 "namespace:models" 文件夹下。如果你的物品模型像平常一样声明，请传入 'null'。
     * </p>
     * Create an item reg.
     * <p>
     * Note: If you want to set a Crafting Remainder for your item, please use
     * <p>
     * {@link kasuga.lib.core.base.item_helper.ExternalRemainderItem} or
     * <p>
     * {@link kasuga.lib.core.base.item_helper.ExternalRemainderBlockItem}
     * <p>
     * then use {@link ExternalProperties#craftRemainder(Supplier)}
     * @param registrationKey the registration key of your item.
     * @param model If your item's model doesn't lie under the "namespace:models/item" folder, pass the location here,
     *              Pay attention that your model must be under the "namespace:models" folder. If your item's model just
     *              declared as usual, pass 'null' into this.
     */
    public ItemReg(String registrationKey, @Nullable @Deprecated ResourceLocation model) {
        super(registrationKey);
        this.model = model;
        this.tags = new ArrayList<>();
    }

    /**
     * 如果你不需要为你的物品定制模型，请使用此方法。
     * @param registrationKey 你的物品的注册名。
     * If your don't need a customize model for your item, use this.
     * @param registrationKey the registration key of your item.
     */
    public ItemReg(String registrationKey) {
        super(registrationKey);
        this.model = null;
        this.tags = new ArrayList<>();
    }

    /**
     * 详见 {@link BlockReg#defaultBlockItem(ResourceLocation)}
     * @param blockReg 用于创建该物品的方块注册机。
     * @param model 该物品的模型位置。
     * @return 自身
     * See {@link BlockReg#defaultBlockItem(ResourceLocation)}
     * @param blockReg the block registration to create this item.
     * @param model the model location of this item.
     * @return self.
     */
    public static ItemReg<BlockItem> defaultBlockItem(BlockReg<?> blockReg, @Nullable ResourceLocation model) {
        ItemReg<BlockItem> reg = new ItemReg<BlockItem>(blockReg.registrationKey, model);
        reg.itemType((properties1 -> new BlockItem(blockReg.getBlock(), properties1)));
        return reg;
    }

    /**
     * 详见 {@link BlockReg#defaultBlockItem()}
     * @param blockReg 用于创建该物品的方块注册机。
     * @return 自身
     * See {@link BlockReg#defaultBlockItem()}
     * @param blockReg the block registration to create this item.
     * @return self.
     */
    public static ItemReg<BlockItem> defaultBlockItem(BlockReg<?> blockReg) {
        ItemReg<BlockItem> reg = new ItemReg<BlockItem>(blockReg.registrationKey);
        reg.itemType((properties1 -> new BlockItem(blockReg.getBlock(), properties1)));
        return reg;
    }

    /**
     * 传入你的物品的构造器lambda。
     * @param builder 你的物品的构造器lambda。
     * @return 自身
     * Pass the constructor lambda of your item here.
     * @param builder your item's constructor lambda.
     * @return self.
     */
    @Mandatory
    public ItemReg<T> itemType(ItemBuilder<? extends Item> builder) {
        this.builder = (ItemBuilder<T>) builder;
        return this;
    }

    /**
     * 如果你的物品的模型不在 "namespace:models/item" 文件夹下，请在这里传入位置，
     * 注意你的模型必须在 "namespace:models" 文件夹下。如果你的物品模型像平常一样声明，请传入 'null'。
     * @param location 你的物品模型的位置。
     * @return 自身
     * If your item's model doesn't lie under the "namespace:models/item" folder, pass the location here,
     * Pay attention that your model must be under the "namespace:models" folder. If your item's model just
     * declared as usual, pass 'null' into this.
     * @param location the resource location of your item's model.
     * @return self.
     */
    @Deprecated
    @Optional
    public ItemReg<T> model(ResourceLocation location) {
        this.model = location;
        return this;
    }

    /**
     * 如果你的物品需要自定义渲染，请使用此方法。
     * 参见 {@link kasuga.lib.core.base.CustomRenderedItem}
     * @param flag 你的物品是否需要自定义渲染。
     * @return 自身
     * If you need your item to be custom rendered, use this.
     * See {@link kasuga.lib.core.base.CustomRenderedItem}
     * @param flag Should your item to be custom rendered.
     * @return self.
     */
    @Optional
    public ItemReg<T> shouldCustomRender(boolean flag) {
        customRender = flag;
        return this;
    }

    /**
     * 设置 你的物品的最大堆叠数量。
     * @param size 最大堆叠数量。
     * Set the max stack size of your item.
     * @param size max stack size.
     * @return self.
     */
    @Optional
    public ItemReg<T> stackTo(int size) {
        properties.stacksTo(size);
        return this;
    }

    /**
     * 你的物品将被展示在此栏中。
     * @param tab 需要展示的创造模式物品栏注册机。
     * @return 自身。
     * Your item would be displayed in this tab.
     * @param tab the creative mode tab registration you want your item in.
     * @return self.
     */
    @Optional
    public ItemReg<T> tab(CreativeTabReg tab) {
        properties.tab(tab.getTab());
        return this;
    }

    /**
     * 你的物品将被展示在此栏中。
     * @param tab 需要展示的创造模式物品栏。
     * @return 自身。
     * Your item would be displayed in this tab.
     * @param tab the creative mode tab you want your item in.
     * @return self.
     */
    @Optional
    public ItemReg<T> tab(CreativeModeTab tab) {
        properties.tab(tab);
        return this;
    }

    /**
     * 为你的物品提供一个菜单。菜单（和屏幕）用于GUI。
     * 要使用此功能，请确保你的物品是 {@link net.minecraft.world.MenuProvider} 的子类。
     * @param registrationKey 你的菜单的注册键。
     * @param menu 你的菜单的构造函数。
     * @param screen 你的屏幕的构造函数。
     * @return 自身
     * @param <F> 你的菜单类。
     * @param <U> 你的屏幕类。
     * Provide a menu for your item. Menus (and screens) are used for GUIs.
     * To use this, make sure your item is a subClass of {@link net.minecraft.world.MenuProvider}
     * @param registrationKey the registration key of your menu.
     * @param menu the constructor function of your menu.
     * @param screen the constructor function of your screen.
     * @return self.
     * @param <F> your menu class.
     * @param <U> your screen class.
     */
    @Optional
    public <F extends AbstractContainerMenu, U extends Screen & MenuAccess<F>> ItemReg<T>
    withMenu(String registrationKey, IContainerFactory<?> menu, Supplier<MenuReg.FullScreenInvoker<F, U>> screen) {
        menuReg = new MenuReg<F, U>(registrationKey)
                .withMenuAndScreen((IContainerFactory<F>) menu, screen);
        registerMenu = true;
        return this;
    }

    /**
     * 为你的物品添加一个菜单。详见{@link MenuReg}。请保证你的物品是 {@link net.minecraft.world.MenuProvider} 的子类以使用此方法。
     * @param menuReg 你的菜单注册机。
     * @return 自身
     * Provide a menu for your item. See {@link MenuReg} for more info. To use this, make sure that your item
     * is a subClass of {@link net.minecraft.world.MenuProvider}
     * @param menuReg your menu registration.
     * @return self.
     */
    @Optional
    public ItemReg<T> withMenu(MenuReg<?, ?> menuReg) {
        this.menuReg = menuReg;
        registerMenu = false;
        return this;
    }

    /**
     * 给你的物品添加itemTag。物品标签是你的物品的一个静态属性。
     * @param tag 你的物品标签。
     * @return 自身
     * Add an itemTag to your item. Item tag is a static attribute of your item.
     * @param tag Tags of your item.
     * @return self.
     */
    @Optional
    public ItemReg<T> withTag(TagKey<Item> tag) {
        tags.add(tag);
        return this;
    }

    /**
     * 自定义你的物品属性。
     * @param identifier 物品属性定制器。
     * @return 自身
     * Customize your item's property.
     * @param identifier Item property customizer.
     * @return self.
     */
    @Optional
    public ItemReg<T> withProperty(PropertyIdentifier identifier) {
        identifier.apply(properties);
        return this;
    }

    /**
     * 将你的配置提交到minecraft和forge注册表。
     * @param registry mod的SimpleRegistry。
     * @return 自身
     * Submit your config to minecraft and forge registry.
     * @param registry the mod SimpleRegistry.
     * @return self.
     */
    @Mandatory
    public ItemReg<T> submit(SimpleRegistry registry) {
        if (builder == null) {
            crashOnNotPresent(Item.class, "itemType", "submit");
        }
        if(model != null) {
            registry.modelMappings().addMapping(
                    new ResourceLocation(registry.namespace, "item/" + registrationKey + ".json"), model
            );
        }
        if(customRender)
            registry.stackCustomRenderedItemIn(this.registrationKey);
        registryObject = registry.item().register(registrationKey, () -> builder.build(properties));
        if(menuReg != null && registerMenu) {
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

    public String getIdentifier() {
        return "item";
    }

    public interface PropertyIdentifier {
        void apply(Item.Properties properties);
    }

    public interface ItemBuilder<T extends Item> {
        T build(Item.Properties properties);
    }
}
