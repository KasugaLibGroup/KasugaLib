package kasuga.lib.registrations.common;

import kasuga.lib.core.annos.Inner;
import kasuga.lib.core.annos.Mandatory;
import kasuga.lib.core.annos.Optional;
import kasuga.lib.registrations.BlockEntityRendererBuilder;
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
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * 这是用于注册块装流体的注册机。
 * 例如，这个液体的源块就是一个液体块。
 * @param <T> 你的流体类。
 * This is the registration for blocks of fluid.
 * For example, the source block of the liquid is a liquid block.
 * @param <T> The class of fluid.
 */
public class FluidBlockReg<T extends LiquidBlock> extends Reg {
    private Material material = Material.AIR;
    private MaterialColor color = MaterialColor.NONE;

    public BlockBehaviour.Properties properties = BlockBehaviour.Properties.of(Material.AIR);
    private FluidBlockBuilder<T> builder;
    private BlockEntityReg<? extends BlockEntity> blockEntityReg = null;
    private MenuReg<?, ?> menuReg = null;
    private BlockReg.PropertyIdentifier identifier;
    private Supplier<? extends ForgeFlowingFluid> fluid;
    private RegistryObject<T> registryObject;
    private final List<TagKey<?>> tags;
    boolean registerBe = false, registerMenu = false;
    BlockReg.BlockRendererBuilder<T> rendererBuilder;

    /**
     * 创建一个流体方块注册机
     * @param registrationKey 你的流体注册的名字。
     * Create a fluid block reg.
     * @param registrationKey The registration key of your fluid.
     */
    public FluidBlockReg(String registrationKey) {
        super(registrationKey);
        this.tags = new ArrayList<>();
    }

    /**
     * 选定你的流体方块对应的流体实例。传入一个返回有效流体的lambda。
     * @param fluid 你的流体实例的lambda。
     * @return 自身
     * Specifies the fluid instance tha corresponds to the block. Pass a lambda that returns an valid fluid here.
     * @param fluid the fluid supplier lambda.
     * @return self.
     */
    @Mandatory
    public FluidBlockReg<T> fluid(Supplier<? extends ForgeFlowingFluid> fluid) {
        this.fluid = fluid;
        return this;
    }

    /**
     * 传入一个方块实例lambda。
     * @param builder 方块构建器lambda。
     * @return 自身
     * Pass a block instance lambda in here.
     * @param builder the block invoker lambda.
     * @return self.
     */
    @Mandatory
    public FluidBlockReg<T> blockType(FluidBlockBuilder<? extends LiquidBlock> builder) {
        this.builder = (FluidBlockBuilder<T>) builder;
        return this;
    }

    /**
     * 就像方块的矿物属性方法一样，见{@link BlockReg#material(Material)}
     * @param material 你希望在地图上显示的方块材质。
     * @return 自身
     * As same as the block's material method, see {@link BlockReg#material(Material)}
     * @param material the material color you'd like to show in the map.
     * @return self.
     */
    @Mandatory
    public FluidBlockReg<T> material(Material material) {
        this.material = material;
        return this;
    }

    /**
     * 你希望在地图上显示的流体方块颜色。
     * @param color 颜色。
     * @return 自身
     * The color you would like to make your fluid block on the map.
     * @param color color on map.
     * @return self.
     */
    @Mandatory
    public FluidBlockReg<T> materialColor(MaterialColor color) {
        this.color = color;
        return this;
    }

    /**
     * 你的流体方块自定义属性。
     * @param identifier 方块属性定制lambda。
     * @return 自身
     * Your block's custom property.
     * @param identifier the block property customizer lambda.
     * @return self.
     */
    @Optional
    public FluidBlockReg<T> addProperty(BlockReg.PropertyIdentifier identifier) {
        this.identifier = identifier;
        return this;
    }

    /**
     * 你的流体方块破坏音效（你想干啥？）
     * @param sound 破坏音效。
     * @return 自身
     * Your fluid block's destroy sound. (Why you want to apply it?)
     * @param sound The destroy sound.
     * @return self.
     */
    @Optional
    public FluidBlockReg<T> withSound(SoundType sound) {
        this.properties.sound(sound);
        return this;
    }

    /**
     * 为你的流体方块创建一个方块实体。（你想干啥？）
     * @param beRegistrationKey 你的方块实体注册的名字。
     * @param supplier 提供方块实体实例的lambda。
     * @return 自身
     * @param <R> 你的实体类。
     *
     * create a block entity for your fluid. (Why you would need this?)
     * @param beRegistrationKey the registration key of your block entity.
     * @param supplier the supplier that provides a block entity instance.
     * @return self.
     * @param <R> The entity class.
     */
    @Optional
    public <R extends BlockEntity> FluidBlockReg<T> withBlockEntity(String beRegistrationKey, BlockEntityType.BlockEntitySupplier<R> supplier) {
        this.blockEntityReg = new BlockEntityReg<R>(beRegistrationKey)
                .blockEntityType(supplier)
                .addBlock(() -> this.registryObject.get());
        registerBe = true;
        return this;
    }

    /**
     * 将你的流体方块与一个方块实体绑定。
     * @param blockEntityReg 你的方块实体注册机。
     * @return 自身
     * bind your block with a block entity.
     * @param blockEntityReg the reg of your block entity.
     * @return self.
     */
    @Optional
    public FluidBlockReg<T> withBlockEntity(BlockEntityReg<? extends BlockEntity> blockEntityReg) {
        this.blockEntityReg = blockEntityReg.addBlock(() -> this.registryObject.get());
        registerBe = false;
        return this;
    }

    /**
     * 或许你需要为你的液体添加一个方块实体渲染器？
     * @param builder 你的方块实体渲染器构建器lambda。
     * @return 自身
     * Maybe you need a block entity renderer for your liquid ?
     * @param builder the block entity renderer supplier lambda pf your block entity.
     * @return self.
     */
    @Optional
    public FluidBlockReg<T> withBlockEntityRenderer(Supplier<BlockEntityRendererBuilder> builder) {
        if (blockEntityReg == null) {
            crashOnNotPresent(BlockEntityReg.class, "blockEntityReg", "withBlockEntityRenderer");
            return this;
        }
        blockEntityReg.withRenderer(builder::get);
        return this;
    }

    public FluidBlockReg<T> withBlockRenderer(BlockReg.BlockRendererBuilder<T> builder) {
        this.rendererBuilder = builder;
        return this;
    }

    /**
     * 详见{@link BlockReg#withMenu(String, IContainerFactory, Supplier)}
     * @param registrationKey 你的菜单注册的名字。
     * @param menu 你的菜单实例。
     * @param screen 你的屏幕实例。
     * @return 自身
     * @param <F> 你的菜单类。
     * @param <U> 你的屏幕类。
     *
     * See {@link BlockReg#withMenu(String, IContainerFactory, Supplier)}
     * @param registrationKey the name of your menu.
     * @param menu the menu instance supplier.
     * @param screen the screen instance supplier.
     * @return self.
     * @param <F> the class of your menu.
     * @param <U> the class of your screen.
     */
    @Optional
    public <F extends AbstractContainerMenu, U extends Screen & MenuAccess<F>> FluidBlockReg<T>
    withMenu(String registrationKey, IContainerFactory<?> menu, Supplier<MenuReg.FullScreenInvoker<F, U>> screen) {
        menuReg = new MenuReg<F, U>(registrationKey)
                .withMenuAndScreen((IContainerFactory<F>) menu, screen);
        registerMenu = true;
        return this;
    }

    /**
     * 详见{@link BlockReg#withMenu(MenuReg)}
     * @param menuReg 你的菜单注册机。
     * @return 自身
     * see {@link BlockReg#withMenu(MenuReg)}
     * @param menuReg the reg of your menu.
     * @return self.
     */
    @Optional
    public FluidBlockReg<T> withMenu(MenuReg<?, ?> menuReg) {
        this.menuReg = menuReg;
        registerMenu = false;
        return this;
    }

    /**
     * 详见{@link BlockReg#withTags(TagKey)}
     * @param tag 你的标签。
     * @return 自身
     * see {@link BlockReg#withTags(TagKey)}
     * @param tag the tagkey.
     * @return self.
     */
    @Optional
    public FluidBlockReg<T> withTags(TagKey<Block> tag) {
        this.tags.add(tag);
        return this;
    }

    /**
     * 将你的配置提交到forge和minecraft的注册表。
     * @param registry 你的mod的SimpleRegistry。
     * @return 自身
     * Submit your config to forge and minecraft's registry.
     * @param registry the mod SimpleRegistry.
     * @return self.
     */
    @Mandatory
    public FluidBlockReg<T> submit(SimpleRegistry registry) {
        initProperties();
        if (builder == null) {
            crashOnNotPresent(FluidBlockBuilder.class, "fluid", "submit");
        }
        registryObject = registry.block().register(registrationKey, () -> builder.build(fluid, properties));
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

    public MenuReg<?, ?> getMenuReg() {
        return menuReg;
    }

    public BlockEntityReg<?> getBlockEntityReg() {
        return blockEntityReg;
    }

    @Inner
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
        return "fluid_block";
    }

    public T instance() {
        return registryObject == null ? null : registryObject.get();
    }

    public interface FluidBlockBuilder<T extends LiquidBlock> {
        T build(Supplier<? extends ForgeFlowingFluid> fluid, BlockBehaviour.Properties properties);
    }
}
