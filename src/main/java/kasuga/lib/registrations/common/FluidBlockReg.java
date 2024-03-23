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

    public FluidBlockReg(String registrationKey) {
        super(registrationKey);
        this.tags = new ArrayList<>();
    }

    public FluidBlockReg<T> fluid(Supplier<? extends ForgeFlowingFluid> fluid) {
        this.fluid = fluid;
        return this;
    }

    public FluidBlockReg<T> blockType(FluidBlockBuilder<? extends LiquidBlock> builder) {
        this.builder = (FluidBlockBuilder<T>) builder;
        return this;
    }

    public FluidBlockReg<T> MapColor(MapColor color) {
        this.color = color;
        return this;
    }

    public FluidBlockReg<T> addProperty(BlockReg.PropertyIdentifier identifier) {
        this.identifier = identifier;
        return this;
    }

    public FluidBlockReg<T> withSound(SoundType sound) {
        this.properties.sound(sound);
        return this;
    }

    public <R extends BlockEntity> FluidBlockReg<T> withBlockEntity(String beRegistrationKey, BlockEntityType.BlockEntitySupplier<R> supplier) {
        this.blockEntityReg = new BlockEntityReg<R>(beRegistrationKey)
                .blockEntityType(supplier)
                .addBlock(() -> this.registryObject.get());
        return this;
    }

    public FluidBlockReg<T> withBlockEntity(BlockEntityReg<? extends BlockEntity> blockEntityReg) {
        this.blockEntityReg = blockEntityReg.addBlock(() -> this.registryObject.get());
        return this;
    }

    public FluidBlockReg<T> withBlockEntityRenderer(BlockEntityReg.BlockEntityRendererBuilder builder) {
        blockEntityReg.withRenderer(builder);
        return this;
    }

    public <F extends AbstractContainerMenu, R extends Screen, U extends Screen & MenuAccess<F>> FluidBlockReg<T>
    withMenu(String registrationKey, IContainerFactory<?> menu, MenuScreens.ScreenConstructor<?, ?> screen) {
        menuReg = new MenuReg<F, R, U>(registrationKey)
                .withMenuAndScreen((IContainerFactory<F>) menu, (MenuScreens.ScreenConstructor<F, U>) screen);
        return this;
    }

    public FluidBlockReg<T> withMenu(MenuReg<?, ?, ?> menuReg) {
        this.menuReg = menuReg;
        return this;
    }

    public FluidBlockReg<T> withTags(TagKey<Block> tag) {
        this.tags.add(tag);
        return this;
    }

    public MenuReg<?, ?, ?> getMenuReg() {
        return menuReg;
    }

    public BlockEntityReg<?> getBlockEntityReg() {
        return blockEntityReg;
    }

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
        return "block";
    }

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

    public T instance() {
        return registryObject == null ? null : registryObject.get();
    }

    public interface FluidBlockBuilder<T extends LiquidBlock> {
        T build(Supplier<? extends ForgeFlowingFluid> fluid, BlockBehaviour.Properties properties);
    }
}
