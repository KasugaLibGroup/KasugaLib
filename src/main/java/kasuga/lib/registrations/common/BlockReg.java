package kasuga.lib.registrations.common;

import kasuga.lib.core.annos.Beta;
import kasuga.lib.core.annos.Inner;
import kasuga.lib.core.annos.Mandatory;
import kasuga.lib.core.annos.Optional;
import kasuga.lib.core.base.CustomBlockRenderer;
import kasuga.lib.registrations.BlockEntityRendererBuilder;
import kasuga.lib.registrations.Reg;
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
import java.util.function.Supplier;


/**
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
    private PropertyIdentifier identifier;
    private RegistryObject<T> registryObject;
    private final List<TagKey<?>> tags;
    boolean registerItem = false, registerBe = false, registerMenu = false;
    private BlockRendererBuilder<T> rendererBuilder = null;

    /**
     * Use this to create a BlockReg.
     * @param registrationKey your block registration key.
     */
    public BlockReg(String registrationKey) {
        super(registrationKey);
        this.tags = new ArrayList<>();
    }

    /**
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
     * Add other custom property to your block.
     * @param identifier A function interface that provides the block property to you, you could apply your custom
     *                   config to the property in tha lambda.
     * @return self.
     */
    @Optional
    public BlockReg<T> addProperty(PropertyIdentifier identifier) {
        this.identifier = identifier;
        return this;
    }

    /**
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
    withMenu(String registrationKey, IContainerFactory<?> menu, MenuReg.ScreenInvoker<U> screen) {
        menuReg = new MenuReg<F, U>(registrationKey)
                .withMenuAndScreen((IContainerFactory<F>) menu, screen);
        registerMenu = true;
        return this;
    }

    /**
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
    withItemMenu(String registrationKey, IContainerFactory<?> menu, MenuReg.ScreenInvoker<U> screen) {
        if (itemReg == null) {
            crashOnNotPresent(ItemReg.class, "itemReg", "withItemMenu");
            return this;
        }
        itemReg.withMenu(registrationKey, menu, screen);
        return this;
    }

    /**
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

    /**
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
        if(identifier != null) identifier.apply(properties);
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
