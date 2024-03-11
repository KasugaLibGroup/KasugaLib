package kasuga.lib.registrations.common;

import kasuga.lib.core.Mandatory;
import kasuga.lib.core.Optional;
import kasuga.lib.registrations.Reg;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.Consumer;

public class FluidReg<E extends ForgeFlowingFluid> extends Reg {
    private RegistryObject<E> stillObject = null;
    private RegistryObject<E> flowingObject = null;
    private RegistryObject<? extends BucketItem> itemRegistryObject = null;
    private final FluidType.Properties properties;
    private ForgeFlowingFluid.Properties fluidProp = null;
    private FluidBuilder<E> stillBuilder = null, flowingBuilder = null;
    private PropertyBuilder propertyBuilder = null;
    private BucketBuilder<? extends BucketItem> bucketBuilder = null;
    private ResourceLocation bucketModelLocation = null;
    private final Item.Properties itemProperties;
    private boolean customRender = false;
    private final ArrayList<FluidPropertyBuilder> builders;
    private final ArrayList<ItemReg.PropertyIdentifier> identifiers;
    private FluidBlockReg<? extends LiquidBlock> block;
    private FluidType type = null;
    private String stillTexturePath = null;
    private String flowingTexturePath = null;
    private String overlayTexturePath = null;
    private MenuReg<?, ?, ?> menuReg = null;
    private int tintColor = 0xffffff;
    public FluidReg(String registrationKey) {
        super(registrationKey);
        properties = FluidType.Properties.create();
        itemProperties = new Item.Properties();
        builders = new ArrayList<>();
        identifiers = new ArrayList<>();
        block = new FluidBlockReg<>(registrationKey);
    }

    @Mandatory
    public FluidReg<E> still(FluidBuilder<? extends E> builder, String stillTexPath) {
        stillBuilder = (FluidBuilder<E>) builder;
        this.stillTexturePath = stillTexPath;
        return this;
    }

    @Mandatory
    public FluidReg<E> flow(FluidBuilder<? extends E> builder, String flowingTexPath) {
        flowingBuilder = (FluidBuilder<E>) builder;
        this.flowingTexturePath = flowingTexPath;
        return this;
    }

    @Optional
    public FluidReg<E> type(FluidType type) {
        this.type = type;
        return this;
    }

    public FluidReg<E> bucketItem(BucketBuilder<? extends BucketItem> builder) {
        this.bucketBuilder = builder;
        return this;
    }

    public FluidReg<E> bucketModel(ResourceLocation resourceLocation) {
        bucketModelLocation = resourceLocation;
        return this;
    }

    public FluidReg<E> itemProperty(ItemReg.PropertyIdentifier identifier) {
        identifiers.add(identifier);
        return this;
    }

    public FluidReg<E> shouldCustomRenderItem(boolean flag) {
        customRender = flag;
        return this;
    }

    public FluidReg<E> tab(CreativeModeTab tab) {
        itemProperties.tab(tab);
        return this;
    }

    public FluidReg<E> stacksTo(int size) {
        itemProperties.stacksTo(size);
        return this;
    }

    public <F extends AbstractContainerMenu, R extends Screen, U extends Screen & MenuAccess<F>> FluidReg<E>
    withMenu(String registrationKey, IContainerFactory<?> menu, MenuScreens.ScreenConstructor<?, ?> screen) {
        menuReg = new MenuReg<F, R, U>(registrationKey)
                .withMenuAndScreen((IContainerFactory<F>) menu, (MenuScreens.ScreenConstructor<F, U>) screen);
        return this;
    }

    public FluidReg<E> withMenu(MenuReg<?, ?, ?> menuReg) {
        this.menuReg = menuReg;
        return this;
    }

    public FluidReg<E> blockType(FluidBlockReg.FluidBlockBuilder<? extends LiquidBlock> builder) {
        block.blockType(builder);
        return this;
    }

    public FluidReg<E> withBlockProperty(BlockReg.PropertyIdentifier identifier) {
        block.addProperty(identifier);
        return this;
    }


    @Optional
    public FluidReg<E> tintColor(int r, int g, int b) {
        this.tintColor = r * 0xff * 0xff + g * 0xff + b;
        return this;
    }

    @Optional
    public FluidReg<E> tintColor(int color) {
        this.tintColor = color;
        return this;
    }

    @Mandatory
    public FluidReg<E> typeProperty(PropertyBuilder builder) {
        this.propertyBuilder = builder;
        return this;
    }

    @Mandatory
    public FluidReg<E> fluidProperty(FluidPropertyBuilder builder) {
        builders.add(builder);
        return this;
    }

    @Optional
    public FluidReg<E> overlayTexPath(String path) {
        this.overlayTexturePath = path;
        return this;
    }

    @Mandatory
    @Override
    public FluidReg<E> submit(SimpleRegistry registry) {
        properties.descriptionId(registrationKey);
        if(propertyBuilder != null) {
            propertyBuilder.build(properties);
        }
        if(bucketModelLocation != null) {
            registry.modelMappings().addMapping(
                    new ResourceLocation(registry.namespace, "item/" + registrationKey), bucketModelLocation
            );
        }
        if(customRender)
            registry.stackCustomRenderedItemIn(this.registrationKey);
        block.fluid(() -> stillObject.get());
        block.submit(registry);
        type = type == null ? initDefaultType(registry) : type;
        RegistryObject<FluidType> typeObj = registry.fluid_type().register(registrationKey, () -> type);
        fluidProp = new ForgeFlowingFluid.Properties(typeObj, () -> stillObject.get(), () -> flowingObject.get());
        for(FluidPropertyBuilder builder : builders)
            builder.build(fluidProp);
        if(stillBuilder != null)
            stillObject = registry.fluid().register(registrationKey + "_still", () -> stillBuilder.build(fluidProp));
        if(flowingBuilder != null)
            flowingObject = registry.fluid().register(registrationKey + "_flow", () -> flowingBuilder.build(fluidProp));
        for(ItemReg.PropertyIdentifier identifier : identifiers) {identifier.apply(itemProperties);}
        itemRegistryObject = registry.item().register(registrationKey,
                () -> this.bucketBuilder.build(stillObject.get(), itemProperties));
        if(menuReg != null) {
            if(!registry.hasMenuCache(this.toString())) {
                registry.cacheMenuIn(menuReg);
            }
        }
        return this;
    }



    private FluidType initDefaultType(SimpleRegistry registry) {
        ResourceLocation stillLoc = stillTexturePath == null ? null : new ResourceLocation(registry.namespace, stillTexturePath);
        ResourceLocation flowingLoc = flowingTexturePath == null ? null : new ResourceLocation(registry.namespace, flowingTexturePath);
        ResourceLocation overlayLoc = overlayTexturePath == null ? null : new ResourceLocation(registry.namespace, overlayTexturePath);

        FluidType type  = new FluidType(properties) {
            @Override
            public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                consumer.accept(
                        new IClientFluidTypeExtensions() {
                            @Override
                            public int getTintColor() {
                                return tintColor;
                            }

                            @Override
                            public ResourceLocation getStillTexture() {
                                if(stillLoc != null)
                                    return stillLoc;
                                return IClientFluidTypeExtensions.super.getStillTexture();
                            }

                            @Override
                            public ResourceLocation getFlowingTexture() {
                                if(flowingLoc != null)
                                    return flowingLoc;
                                return IClientFluidTypeExtensions.super.getFlowingTexture();
                            }

                            @Override
                            public @Nullable ResourceLocation getOverlayTexture() {
                                if(overlayLoc != null)
                                    return overlayLoc;
                                return IClientFluidTypeExtensions.super.getOverlayTexture();
                            }
                        }
                );
            }
        };
        return type;
    }

    public RegistryObject<E> still() {
        return stillObject;
    }
    public RegistryObject<E> flowing() {
        return flowingObject;
    }

    public FluidType fluidType() {
        return type;
    }

    public Fluid stillFluid() {
        return stillObject.get();
    }
    public Fluid flowingFluid() {
        return flowingObject.get();
    }

    public RegistryObject<? extends BucketItem> itemRegistryObject() {
        return itemRegistryObject;
    }

    public BucketItem bucket() {
        return itemRegistryObject.get();
    }

    public LiquidBlock legacyBlock() {
        return block.getBlock();
    }


    @Override
    public String getIdentifier() {
        return "fluid";
    }

    public interface FluidBuilder<T extends Fluid> {
        T build(ForgeFlowingFluid.Properties properties);
    }
    public interface PropertyBuilder {
        void build(FluidType.Properties properties);
    }

    public interface FluidPropertyBuilder {
        void build(ForgeFlowingFluid.Properties properties);
    }

    public interface BucketBuilder<T extends BucketItem> {
        T build(ForgeFlowingFluid fluid, Item.Properties properties);
    }
}
