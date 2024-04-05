package kasuga.lib.registrations.common;

import kasuga.lib.core.annos.Inner;
import kasuga.lib.core.annos.Mandatory;
import kasuga.lib.core.annos.Optional;
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
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * This is the registration for blocks of fluid.
 * For example, the source block of the liquid is a liquid block.
 * @param <T> The class of fluid.
 */
public class FluidBlockReg<T extends LiquidBlock> extends Reg {
    private MapColor color = MapColor.NONE;
    public BlockBehaviour.Properties properties = BlockBehaviour.Properties.of();
    private FluidBlockBuilder<T> builder;
    private BlockEntityReg<? extends BlockEntity> blockEntityReg = null;
    private MenuReg<?, ?, ?> menuReg = null;
    private BlockReg.PropertyIdentifier identifier;
    private Supplier<? extends ForgeFlowingFluid> fluid;
    private RegistryObject<T> registryObject;
    private final List<TagKey<?>> tags;

    /**
     * Create a fluid block reg.
     * @param registrationKey The registration key of your fluid.
     */
    public FluidBlockReg(String registrationKey) {
        super(registrationKey);
        this.tags = new ArrayList<>();
    }

    public FluidBlockReg<T> fluid(Supplier<? extends ForgeFlowingFluid> fluid) {
        this.fluid = fluid;
        return this;
    }

    /**
     * Pass a block instance lambda in here.
     * @param builder the block invoker lambda.
     * @return self.
     */
    @Mandatory
    public FluidBlockReg<T> blockType(FluidBlockBuilder<? extends LiquidBlock> builder) {
        this.builder = (FluidBlockBuilder<T>) builder;
        return this;
    }

    public FluidBlockReg<T> MapColor(MapColor color) {
        this.color = color;
        return this;
    }

    /**
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
        return this;
    }

    /**
     * bind your block with a block entity.
     * @param blockEntityReg the reg of your block entity.
     * @return self.
     */
    @Optional
    public FluidBlockReg<T> withBlockEntity(BlockEntityReg<? extends BlockEntity> blockEntityReg) {
        this.blockEntityReg = blockEntityReg.addBlock(() -> this.registryObject.get());
        return this;
    }

    /**
     * Maybe you need a block entity renderer for your liquid ?
     * @param builder the block entity renderer supplier lambda pf your block entity.
     * @return self.
     */
    @Optional
    public FluidBlockReg<T> withBlockEntityRenderer(BlockEntityReg.BlockEntityRendererBuilder builder) {
        blockEntityReg.withRenderer(builder);
        return this;
    }

    /**
     * See {@link BlockReg#withItemMenu(String, IContainerFactory, MenuScreens.ScreenConstructor)}
     * @param registrationKey the name of your menu.
     * @param menu the menu instance supplier.
     * @param screen the screen instance supplier.
     * @return self.
     * @param <F> the class of your menu.
     * @param <R> the class of your screen.
     * @param <U> the class of your screen.
     */
    @Optional
    public <F extends AbstractContainerMenu, R extends Screen, U extends Screen & MenuAccess<F>> FluidBlockReg<T>
    withMenu(String registrationKey, IContainerFactory<?> menu, MenuScreens.ScreenConstructor<?, ?> screen) {
        menuReg = new MenuReg<F, R, U>(registrationKey)
                .withMenuAndScreen((IContainerFactory<F>) menu, (MenuScreens.ScreenConstructor<F, U>) screen);
        return this;
    }

    /**
     * see {@link BlockReg#withItemMenu(MenuReg)}
     * @param menuReg the reg of your menu.
     * @return self.
     */
    @Optional
    public FluidBlockReg<T> withMenu(MenuReg<?, ?, ?> menuReg) {
        this.menuReg = menuReg;
        return this;
    }

    /**
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
     * Submit your config to forge and minecraft's registry.
     * @param registry the mod SimpleRegistry.
     * @return self.
     */
    @Mandatory
    public FluidBlockReg<T> submit(SimpleRegistry registry) {
        initProperties();
        registryObject = registry.block().register(registrationKey, () -> builder.build(fluid, properties));
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

    public MenuReg<?, ?, ?> getMenuReg() {
        return menuReg;
    }

    public BlockEntityReg<?> getBlockEntityReg() {
        return blockEntityReg;
    }

    @Inner
    private void initProperties() {
        properties = BlockBehaviour.Properties.of().mapColor(color);
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
