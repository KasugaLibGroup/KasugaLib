package kasuga.lib.registrations.common;

import kasuga.lib.core.annos.Beta;
import kasuga.lib.core.annos.Inner;
import kasuga.lib.core.annos.Mandatory;
import kasuga.lib.core.annos.Optional;
import kasuga.lib.core.base.CustomBlockRenderer;
import kasuga.lib.registrations.BlockEntityRendererBuilder;
import kasuga.lib.registrations.Reg;
import kasuga.lib.registrations.builders.SelfReferenceItemBuilder;
import kasuga.lib.registrations.exception.RegistryElementNotPresentException;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.CrashReport;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;


/**
 * 方块是游戏的基本元素。你可以很容易地将你的方块与物品、方块实体、方块实体渲染器或菜单绑定在一起，见
 * {@link BlockEntityReg} 了解更多关于方块实体注册的信息，{@link MenuReg} 了解更多关于菜单注册的信息，
 * {@link ItemReg} 了解更多关于物品注册的信息。见 {@link Block} 了解关于Minecraft方块的信息。
 * @param <T> 你的方块类。
 * Block is the base element of the game. You would bind your block with item, block entity,
 * block entity renderer or menus easily with this reg. See {@link BlockEntityReg} for more info
 * about block entity registration, {@link MenuReg} for more info about menu reg, {@link ItemReg}
 * for more info about item reg. See {@link Block} about minecraft blocks.
 * @param <T> Class of your block.
 */
public class BlockReg<T extends Block> extends Reg {
    private Material material = Material.AIR;
    private MaterialColor color = MaterialColor.NONE;

    public BlockBehaviour.Properties properties = BlockBehaviour.Properties.of(Material.AIR);
    private BlockBuilder<T> builder;
    private ItemReg<?> itemReg = null;
    private BlockEntityReg<? extends BlockEntity> blockEntityReg = null;
    private MenuReg<?, ?> menuReg = null;
    private final ArrayList<PropertyIdentifier> identifier;
    private RegistryObject<T> registryObject;
    private final List<TagKey<?>> tags;
    boolean registerItem = false, registerBe = false, registerMenu = false;
    private BlockRendererBuilder<T> rendererBuilder = null;

    /**
     * 用这个方法来创建一个BlockReg。
     * @param registrationKey 你的方块注册名字。
     * Use this to create a BlockReg.
     * @param registrationKey your block registration key.
     */
    public BlockReg(String registrationKey) {
        super(registrationKey);
        this.tags = new ArrayList<>();
        identifier = new ArrayList<>();
    }

    /**
     * 材料控制着你的方块在地图上显示的颜色。
     * @param material 你想用的材料。
     * @return 自身
     * Material controls what color the block would display on your map.
     * @param material The material you want to apply.
     * @return self.
     */
    @Mandatory
    public BlockReg<T> material(Material material) {
        this.material = material;
        return this;
    }

    /**
     * 材料颜色也控制着你的方块在地图上显示的颜色。
     * @param color 你的方块在地图上显示的颜色。
     * @return 自身
     * Meterial color also controls what color the block would display on your map.
     * @param color The color your block would have on the map.
     * @return self.
     */
    @Mandatory
    public BlockReg<T> materialColor(MaterialColor color) {
        this.color = color;
        return this;
    }

    /**
     * 你的方块的初始化器。你应该传入一个方块构造函数到这里。例如在这个方法内写 "ExampleBlock::new" 或
     * "prop -> new ExampleBlock(prop)"。
     * @param builder 你的方块初始化器。
     * @return 自身
     * The initializer of your block. You should pass a block constructor function into this. For example write
     * "ExampleBlock::new" or "prop -> new ExampleBlock(prop)" in this method.
     * @param builder initializer of your block.
     * @return self.
     */
    @Mandatory
    public BlockReg<T> blockType(BlockBuilder<T> builder) {
        this.builder = builder;
        return this;
    }

    /**
     * 为你的方块生成一个默认的方块物品。
     * @param modelLocation 你的物品模型位置，如果你的物品模型就在 namespace:models/item/ 文件夹下，你可以传入null或者使用下面的{@link BlockReg#defaultBlockItem()}方法。
     *                      如果你的模型在模型文件夹的其他位置，传入一个有效的资源位置。
     * @return 自身
     * Generate a default item for your block.
     * @param modelLocation your item model location, if your item's model is just under the namespace:models/item/
     *                      folder, you could pass null into this or use the method {@link BlockReg#defaultBlockItem()}
     *                      below. While the model lies in the other place under your model folder, pass a valid resource
     *                      location here.
     * @return self.
     */
    @Optional
    public BlockReg<T> defaultBlockItem(ResourceLocation modelLocation) {
        this.itemReg = ItemReg.defaultBlockItem(this, modelLocation);
        registerItem = true;
        return this;
    }

    /**
     * 生成一个默认的方块物品。
     * @return 自身
     * Generate a default item for your block.
     * @return self.
     */
    @Optional
    public BlockReg<T> defaultBlockItem() {
        this.itemReg = ItemReg.defaultBlockItem(this);
        registerItem = true;
        return this;
    }

    /**
     * 为你的方块添加一个自定义属性
     * @param identifier 一个函数接口，它为你提供方块属性，你可以在lambda中应用你的自定义配置。
     * @return 自身
     * Add other custom property to your block.
     * @param identifier A function interface that provides the block property to you, you could apply your custom
     *                   config to the property in tha lambda.
     * @return self.
     */
    @Optional
    public BlockReg<T> addProperty(PropertyIdentifier identifier) {
        this.identifier.add(identifier);
        return this;
    }

    /**
     * Minecraft将在方块破坏时播放这个声音。
     * @param sound 破坏声音。
     * @return 自身
     * The game would play this sound when it is broken by any source.
     * @param sound the breaking sound.
     * @return self
     */
    @Optional
    public BlockReg<T> withSound(SoundType sound) {
        this.properties.sound(sound);
        return this;
    }

    public BlockReg<T> withBlockRenderer(BlockRendererBuilder<T> builder) {
        this.rendererBuilder = builder;
        return this;
    }

    /**
     * 为方块提供一个方块实体。如果你想让你的方块生成一个方块实体，它必须是BaseEntityBlock的子类，见
     * {@link net.minecraft.world.level.block.BaseEntityBlock}。
     * @param beRegistrationKey 你的方块实体注册名字。
     * @param supplier 你的方块实体初始化器。
     * @return 自身
     * @param <R> 你的方块实体类。
     * Provide a block entity for the block. If you want your block to spawn a block entity, it must be a subClass of
     * BaseEntityBlock, see {@link net.minecraft.world.level.block.BaseEntityBlock}.
     * @param beRegistrationKey your block entity reg name.
     * @param supplier Your block entity initializer.
     * @return self.
     * @param <R> Type of your block entity.
     */
    @Optional
    public <R extends BlockEntity> BlockReg<T> withBlockEntity(String beRegistrationKey, BlockEntityType.BlockEntitySupplier<R> supplier) {
        this.blockEntityReg = new BlockEntityReg<R>(beRegistrationKey)
                .blockEntityType(supplier)
                .addBlock(() -> this.registryObject.get());
        registerBe = true;
        return this;
    }

    /**
     * 为方块提供方块实体。
     * @param blockEntityReg 你的方块实体注册。
     * @return 自身
     * Provide a block entity for your block.
     * @param blockEntityReg registration of your block entity.
     * @return self.
     */
    @Optional
    public BlockReg<T> withBlockEntity(BlockEntityReg<? extends BlockEntity> blockEntityReg) {
        this.blockEntityReg = blockEntityReg.addBlock(() -> this.registryObject.get());
        registerBe = false;
        return this;
    }

    /**
     * 提供一个方块实体渲染器给你的方块实体（仅当你在此注册中有方块实体时）。
     * 要使用此功能，请确保你的方块是{@link net.minecraft.world.MenuProvider}的子类。
     * @param builder 你的方块实体渲染器提供者。
     * @return 自身
     * Provide a block entity renderer for your block entity (only if you have a block entity in this registration.)
     * To use this, make sure your block is a subClass of {@link net.minecraft.world.MenuProvider}
     * @param builder Your block entity renderer provider.
     * @return self.
     */
    @Optional
    public BlockReg<T> withBlockEntityRenderer(Supplier<BlockEntityRendererBuilder> builder) {
        if (blockEntityReg == null) {
            crashOnNotPresent(BlockEntityReg.class, "BlockEntityReg", "withBlockEntityRenderer");
            return this;
        }
        blockEntityReg.withRenderer(builder::get);
        return this;
    }

    /**
     * 为你的方块提供一个菜单。菜单（和屏幕）用于GUI。
     * 要使用此功能，请确保你的方块是{@link net.minecraft.world.MenuProvider}的子类。
     * @param registrationKey 你的菜单注册键。
     * @param menu 你的菜单构造函数。
     * @param screen 你的屏幕构造函数。
     * @return 自身
     * @param <F> 你的菜单类。
     * @param <U> 你的屏幕类。
     * Provide a menu for your block. Menus (and screens) are used for GUIs.
     * To use this, make sure your block is a subClass of {@link net.minecraft.world.MenuProvider}
     * @param registrationKey the registration key of your menu.
     * @param menu the constructor function of your menu.
     * @param screen the constructor function of your screen.
     * @return self.
     * @param <F> your menu class.
     * @param <U> your screen class.
     */
    @Optional
    public <F extends AbstractContainerMenu, U extends Screen & MenuAccess<F>> BlockReg<T>
    withMenu(String registrationKey, IContainerFactory<?> menu, Supplier<MenuReg.FullScreenInvoker<F, U>> screen) {
        menuReg = new MenuReg<F, U>(registrationKey)
                .withMenuAndScreen((IContainerFactory<F>) menu, screen);
        registerMenu = true;
        return this;
    }

    /**
     * 为你的方块提供一个菜单。详见{@link MenuReg}。
     * 要使用此功能，请确保你的方块是{@link net.minecraft.world.MenuProvider}的子类。
     * @param menuReg 你的菜单注册。
     * @return 自身
     * Provide a menu for your block. See {@link MenuReg} for more info. To use this, make sure that your block
     * is a subClass of {@link net.minecraft.world.MenuProvider}
     * @param menuReg your menu registration.
     * @return self.
     */
    @Optional
    public BlockReg<T> withMenu(MenuReg<?, ?> menuReg) {
        this.menuReg = menuReg;
        registerMenu = false;
        return this;
    }

    /**
     * 为你的物品提供一个菜单（仅当你有物品注册时）。
     * 要使用此功能，请确保你的物品是{@link net.minecraft.world.MenuProvider}的子类。
     * @param registrationKey 你的菜单注册键。
     * @param menu 你的菜单初始化器。
     * @param screen 你的屏幕初始化器。
     * @return 自身
     * @param <F> 你的菜单类。
     * @param <U> 你的屏幕类。
     * Provide a menu for your item (only if you have a item registration).
     * To use this, make sure your item is a subClass of {@link net.minecraft.world.MenuProvider}
     * @param registrationKey the registration key of your menu.
     * @param menu your menu initializer.
     * @param screen your screen initializer.
     * @return self.
     * @param <F> Your menu class.
     * @param <U> Your screen class.
     */
    @Optional
    public <F extends AbstractContainerMenu, U extends Screen & MenuAccess<F>> BlockReg<T>
    withItemMenu(String registrationKey, IContainerFactory<?> menu, Supplier<MenuReg.FullScreenInvoker<F, U>> screen) {
        if (itemReg == null) {
            crashOnNotPresent(ItemReg.class, "itemReg", "withItemMenu");
            return this;
        }
        itemReg.withMenu(registrationKey, menu, screen);
        return this;
    }

    /**
     * 为你的物品提供一个菜单（仅当你有物品注册时）。
     * 要使用此功能，请确保你的物品是{@link net.minecraft.world.MenuProvider}的子类。
     * @param menuReg 你的菜单注册。
     * @return 自身
     * Provide a menu for your item (only if you ha a item registration).
     * To use this, make sure your item is a subClass of {@link net.minecraft.world.MenuProvider}
     * @param menuReg the menu you would apply.
     * @return self.
     */
    @Optional
    public BlockReg<T> withItemMenu(MenuReg<?, ?> menuReg) {
        if (itemReg == null){
            crashOnNotPresent(ItemReg.class, "itemReg", "withItemMenu");
            return this;
        }
        itemReg.withMenu(menuReg);
        return this;
    }

    /**
     * 为你的方块提供一个物品。你提供的物品必须是 {@link net.minecraft.world.item.BlockItem} 的子类。
     * 玩家可以使用这个物品来放置你的方块。
     * @param builder 你的物品提供者。
     * @param itemModelLocation 你的物品模型位置，如果你的物品模型就在 namespace:models/item/ 文件夹下，你可以传入null或者使用下面的{@link BlockReg#defaultBlockItem()}方法。
     *                          如果你的模型在模型文件夹的其他位置，传入一个有效的资源位置。
     * @return 自身
     * @param <R> 物品类。
     * Provide an item for your block. The item you provide must be a subClass of {@link net.minecraft.world.item.BlockItem}
     * Player could use this item to place your block down.
     * @param builder your item provider.
     * @param itemModelLocation your item model location, if your item's model is just under the namespace:models/item/
     *                          folder, you could pass null into this or use the method {@link BlockReg#defaultBlockItem()}
     *                          below. While the model lies in the other place under your model folder, pass a valid resource
     *                          location here.
     * @return self.
     * @param <R> the item class.
     */
    @Optional
    public <R extends Item> BlockReg<T> withItem(ItemReg.ItemBuilder<R> builder, ResourceLocation itemModelLocation) {
        itemReg = new ItemReg<R>(registrationKey, itemModelLocation);
        itemReg.itemType(builder);
        registerItem = true;
        return this;
    }

    @Optional
    public <R extends Item> BlockReg<T> withItem(SelfReferenceItemBuilder<R,T> builder, ResourceLocation itemModelLocation) {
        itemReg = new ItemReg<R>(registrationKey, itemModelLocation);
        itemReg.itemType((p)->builder.build(this.registryObject.get(),p));
        registerItem = true;
        return this;
    }

    /**
     * 你的物品渲染器是你的自定义渲染器吗？如果你想创建一个自定义渲染的物品，见
     * {@link kasuga.lib.core.base.CustomRenderedItem}
     * @param flag 你的物品是否应该被自定义渲染。
     * @return 自身
     * Is your item renderer by your custom renderer? If you want to create a custom rendered item, see
     * {@link kasuga.lib.core.base.CustomRenderedItem}
     * @param flag should your item be custom rendered.
     * @return self.
     */
    @Optional
    public BlockReg<T> shouldCustomRenderItem(boolean flag) {
        if (itemReg == null) {
            crashOnNotPresent(ItemReg.class, "itemReg", "shouldCustomRenderItem");
            return this;
        }
        itemReg.shouldCustomRender(flag);
        return this;
    }

    /**
     * 为你的方块物品应用自定义属性(仅当你有物品注册时)。
     * @param identifier 在这里传递你的自定义物品配置。
     * @return 自身
     * Apply custom property to your block item(only if there's a item reg)
     * @param identifier pass your custom item config here.
     * @return self.
     */
    @Optional
    public BlockReg<T> itemProperty(ItemReg.PropertyIdentifier identifier) {
        if (itemReg == null) {
            crashOnNotPresent(ItemReg.class, "itemReg", "itemProperty");
        }
        itemReg = itemReg.withProperty(identifier);
        return this;
    }


    /**
     * 你的方块物品会堆叠到这个创造模式物品栏。
     * @param tab 你想把你的物品放进去的物品栏。
     * @return 自身
     * Your block item would stack to this tab,
     * @param tab The tab you'd like to put your item in.
     * @return self.
     */
    @Optional
    public BlockReg<T> tabTo(CreativeModeTab tab) {
        if(itemReg != null)
            itemReg.tab(tab);
        else
            crashOnNotPresent(ItemReg.class, "itemReg", "tabTo");
        return this;
    }

    /**
     * 你的方块物品会堆叠到这个创造模式物品栏。
     * @param reg 你想把你的物品放进去的物品栏。
     * @return 自身
     * Your block item would stack to this tab.
     * @param reg The tab you'd like to put your item in.
     * @return self.
     */
    @Optional
    public BlockReg<T> tabTo(CreativeTabReg reg) {
        if(itemReg != null)
            itemReg.tab(reg);
        else
            crashOnNotPresent(ItemReg.class, "itemReg", "tabTo");
        return this;
    }

    /**
     * 为你的方块物品设置最大堆叠数量。
     * @param size 最大堆叠数量。
     * @return 自身
     * Set the max stack size of your block item.
     * @param size max stack size.
     * @return self.
     */
    @Optional
    public BlockReg<T> stackSize(int size) {
        if(itemReg != null)
            itemReg.stackTo(size);
        else
            crashOnNotPresent(ItemReg.class, "itemReg", "stackSize");
        return this;
    }

    /**
     * 为你的方块应用方块标签。
     * @param tag 方块标签。
     * @return 自身
     * Apply block tags to your block.
     * @param tag block tag.
     * @return self.
     */
    @Optional
    public BlockReg<T> withTags(TagKey<Block> tag) {
        this.tags.add(tag);
        return this;
    }

    /**
     * 在所有的配置都应用后调用此方法。
     * @param registry 你的mod的SimpleRegistry。
     * @return 自身
     * Call this after all config has been applied.
     * @param registry the mod SimpleRegistry.
     * @return self.
     */
    @Mandatory
    @Override
    public BlockReg<T> submit(SimpleRegistry registry) {
        initProperties();
        if (builder == null) {
            crashOnNotPresent(Block.class, "blockType", "submit");
        }
        registryObject = registry.block().register(registrationKey, () -> builder.build(properties));
        if(itemReg != null && registerItem)
            itemReg = itemReg.submit(registry);
        if(blockEntityReg != null && registerBe) {
            if(registry.hasBeCache(this.toString())) {
                registry.getBeCached(this.toString()).withBlocks(() -> this.registryObject.get());
            } else {
                registry.cacheBeIn(blockEntityReg);
            }
        }
        if(menuReg != null && registerMenu) {
            if(!registry.hasMenuCache(this.toString())) {
                registry.cacheMenuIn(menuReg);
            }
        }
        if (rendererBuilder != null) registry.cacheBlockRendererIn(this, rendererBuilder);
        return this;
    }

    public ItemReg<?> getItemReg() {
        return itemReg;
    }

    public MenuReg<?, ?> getMenuReg() {
        return menuReg;
    }

    public BlockEntityReg<?> getBlockEntityReg() {
        return blockEntityReg;
    }

    public T instance() {
        return registryObject == null ? null : registryObject.get();
    }

    public Item itemInstance() {
        return itemReg != null ? itemReg.getItem() : null;
    }

    public RegistryObject<T> getRegistryObject() {
        return registryObject;
    }

    public T getBlock() {
        return registryObject == null ? null : registryObject.get();
    }
    public String getIdentifier() {
        return "block";
    }

    @Inner
    private void initProperties() {
        properties = BlockBehaviour.Properties.of(material, color);
        identifier.forEach(i -> i.apply(properties));
    }

    public interface PropertyIdentifier {
        void apply(BlockBehaviour.Properties properties);
    }

    public interface BlockBuilder<T extends Block> {
        T build(BlockBehaviour.Properties properties);
    }


    public interface BlockRendererBuilder<T extends Block> {
        Supplier<CustomBlockRenderer> build(Supplier<T> block);
    }
}
