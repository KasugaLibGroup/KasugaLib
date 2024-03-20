package kasuga.lib.registrations.common;

import kasuga.lib.registrations.Reg;
import kasuga.lib.registrations.registry.SimpleRegistry;
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

public class BlockReg<T extends Block> extends Reg {
    private Material material = Material.AIR;
    private MaterialColor color = MaterialColor.NONE;
    public BlockBehaviour.Properties properties = BlockBehaviour.Properties.of(Material.AIR);
    private BlockBuilder<T> builder;
    private ItemReg<?> itemReg = null;
    private BlockEntityReg<? extends BlockEntity> blockEntityReg = null;
    private MenuReg<?, ?, ?> menuReg = null;
    private PropertyIdentifier identifier;
    private RegistryObject<T> registryObject;
    private final List<TagKey<?>> tags;

    public BlockReg(String registrationKey) {
        super(registrationKey);
        this.tags = new ArrayList<>();
    }

    public BlockReg<T> material(Material material) {
        this.material = material;
        return this;
    }

    public BlockReg<T> blockType(BlockBuilder<T> builder) {
        this.builder = builder;
        return this;
    }

    public BlockReg<T> defaultBlockItem(ResourceLocation modelLocation) {
        this.itemReg = ItemReg.defaultBlockItem(this, modelLocation);
        return this;
    }

    public BlockReg<T> defaultBlockItem() {
        this.itemReg = ItemReg.defaultBlockItem(this);
        return this;
    }

    public BlockReg<T> materialColor(MaterialColor color) {
        this.color = color;
        return this;
    }

    public BlockReg<T> addProperty(PropertyIdentifier identifier) {
        this.identifier = identifier;
        return this;
    }

    public BlockReg<T> withSound(SoundType sound) {
        this.properties.sound(sound);
        return this;
    }

    public <R extends BlockEntity> BlockReg<T> withBlockEntity(String beRegistrationKey, BlockEntityType.BlockEntitySupplier<R> supplier) {
        this.blockEntityReg = new BlockEntityReg<R>(beRegistrationKey)
                .blockEntityType(supplier)
                .addBlock(() -> this.registryObject.get());
        return this;
    }

    public BlockReg<T> withBlockEntity(BlockEntityReg<? extends BlockEntity> blockEntityReg) {
        this.blockEntityReg = blockEntityReg.addBlock(() -> this.registryObject.get());
        return this;
    }

    public BlockReg<T> withBlockEntityRenderer(BlockEntityReg.BlockEntityRendererBuilder builder) {
        blockEntityReg.withRenderer(builder);
        return this;
    }

    public <F extends AbstractContainerMenu, R extends Screen, U extends Screen & MenuAccess<F>> BlockReg<T>
    withMenu(String registrationKey, IContainerFactory<?> menu, MenuScreens.ScreenConstructor<?, ?> screen) {
        menuReg = new MenuReg<F, R, U>(registrationKey)
                .withMenuAndScreen((IContainerFactory<F>) menu, (MenuScreens.ScreenConstructor<F, U>) screen);
        return this;
    }

    public BlockReg<T> withMenu(MenuReg<?, ?, ?> menuReg) {
        this.menuReg = menuReg;
        return this;
    }

    public <F extends AbstractContainerMenu, R extends Screen, U extends Screen & MenuAccess<F>> BlockReg<T>
    withItemMenu(String registrationKey, IContainerFactory<?> menu, MenuScreens.ScreenConstructor<?, ?> screen) {
        itemReg.withMenu(registrationKey, menu, screen);
        return this;
    }

    public BlockReg<T> withItemMenu(MenuReg<?, ?, ?> menuReg) {
        itemReg.withMenu(menuReg);
        return this;
    }

    public <R extends Item> BlockReg<T> withItem(ItemReg.ItemBuilder<R> builder, ResourceLocation itemModelLocation) {
        itemReg = new ItemReg<R>(registrationKey, itemModelLocation);
        itemReg.itemType(builder);
        return this;
    }

    public BlockReg<T> shouldCustomRenderItem(boolean flag) {
        itemReg.shouldCustomRender(flag);
        return this;
    }

    public BlockReg<T> itemProperty(ItemReg.PropertyIdentifier identifier) {
        itemReg = itemReg.withProperty(identifier);
        return this;
    }

    public BlockReg<T> tabTo(CreativeModeTab tab) {
        if(itemReg != null)
            itemReg.tab(tab);
        return this;
    }

    public BlockReg<T> tabTo(CreativeTabReg reg) {
        if(itemReg != null)
            itemReg.tab(reg);
        return this;
    }

    public BlockReg<T> stackSize(int size) {
        if(itemReg != null)
            itemReg.stackTo(size);
        return this;
    }

    public BlockReg<T> withTags(TagKey<Block> tag) {
        this.tags.add(tag);
        return this;
    }

    public ItemReg<?> getItemReg() {
        return itemReg;
    }

    public MenuReg<?, ?, ?> getMenuReg() {
        return menuReg;
    }

    public BlockEntityReg<?> getBlockEntityReg() {
        return blockEntityReg;
    }

    private void initProperties() {
        properties = BlockBehaviour.Properties.of(material, color);
        if(identifier != null) identifier.apply(properties);
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

    public BlockReg<T> submit(SimpleRegistry registry) {
        initProperties();
        registryObject = registry.block().register(registrationKey, () -> builder.build(properties));
        if(itemReg != null)
            itemReg = itemReg.submit(registry);
        if(blockEntityReg != null) {
            if(registry.hasBeCache(this.toString())) {
                registry.getBeCached(this.toString()).withBlocks(() -> this.registryObject.get());
            } else {
                registry.cacheBeIn(blockEntityReg);
            }
        }
        if(menuReg != null) {
            if(!registry.hasMenuCache(this.toString())) {
                registry.cacheMenuIn(menuReg);
            }
        }
        return this;
    }

    public T instance() {
        return registryObject == null ? null : registryObject.get();
    }

    public Item itemInstance() {
        return itemReg != null ? itemReg.getItem() : null;
    }

    public interface PropertyIdentifier {
        void apply(BlockBehaviour.Properties properties);
    }

    public interface BlockBuilder<T extends Block> {
        T build(BlockBehaviour.Properties properties);
    }
}
