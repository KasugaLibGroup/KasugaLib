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
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.function.Supplier;

/**
 * Use this registration to register fluids like water of lava.
 * See {@link ForgeFlowingFluid}, {@link net.minecraft.world.level.material.WaterFluid} and
 * {@link net.minecraft.world.level.material.LavaFluid}
 * @param <E> the class of your fluid.
 */
public class FluidReg<E extends ForgeFlowingFluid> extends Reg {
    private RegistryObject<E> stillObject = null;
    private RegistryObject<E> flowingObject = null;
    private RegistryObject<? extends BucketItem> itemRegistryObject = null;
    private FluidAttributes.Builder properties;
    private ForgeFlowingFluid.Properties fluidProp = null;
    private FluidBuilder<E> stillBuilder = null, flowingBuilder = null;
    private PropertyBuilder propertyBuilder = null;
    private BucketItemReg<? extends BucketItem> itemReg = null;
    private final ArrayList<FluidPropertyBuilder> builders;
    private FluidBlockReg<? extends LiquidBlock> block;
    private FluidAttributes type = null;
    private String stillTexturePath = null;
    private String flowingTexturePath = null;
    private String overlayTexturePath = null;
    private MenuReg<?, ?, ?> menuReg = null;
    private int tintColor = 0xffffff;
    boolean registerItem = false, registerBlock = false, registerMenu = false;

    /**
     * Create a fluid registration.
     * @param registrationKey the registration key of your fluid.
     */
    public FluidReg(String registrationKey) {
        super(registrationKey);
        builders = new ArrayList<>();
        block = new FluidBlockReg<>(registrationKey);
    }

    /**
     * Pass your still fluid constructor here. "Still" means it is not flowing, and it's texture should be a still fluid
     * texture. See {@link net.minecraft.world.level.material.WaterFluid.Source} or {@link ForgeFlowingFluid.Source}
     * @param builder the constructor lambda of your still fluid.
     * @param stillTexPath the texture resource location path of the still fluid texture.
     * @return self.
     */
    @Mandatory
    public FluidReg<E> still(FluidBuilder<? extends E> builder, String stillTexPath) {
        stillBuilder = (FluidBuilder<E>) builder;
        this.stillTexturePath = stillTexPath;
        return this;
    }

    /**
     * Pass your flowing fluid constructor here. "Flowing" means it is moving, and it's texture should be a flowing fluid
     * texture. See {@link net.minecraft.world.level.material.WaterFluid} or {@link ForgeFlowingFluid}
     * @param builder the constructor lambda of your flowing fluid.
     * @param flowingTexPath the texture resource location path of the flowing fluid texture.
     * @return self.
     */
    @Mandatory
    public FluidReg<E> flow(FluidBuilder<? extends E> builder, String flowingTexPath) {
        flowingBuilder = (FluidBuilder<E>) builder;
        this.flowingTexturePath = flowingTexPath;
        return this;
    }

    /**
     * Your fluid must has a fluid block, pass your fluid-block's constructor lambda here.
     * @param builder The constructor lambda of your fluid block.
     * @return self.
     */
    @Mandatory
    public FluidReg<E> blockType(FluidBlockReg.FluidBlockBuilder<? extends LiquidBlock> builder) {
        block.blockType(builder);
        registerBlock = true;
        return this;
    }

    public FluidReg<E> blockType(FluidBlockReg<? extends LiquidBlock> reg) {
        block = reg;
        registerBlock = false;
        return this;
    }

    /**
     * Register your custom fluid type here.
     * @param type your fluid type.
     * @return self.
     */
    @Optional
    public FluidReg<E> type(FluidAttributes type) {
        this.type = type;
        return this;
    }

    /**
     * Pass a bucket constructor here. Player could use this bucket to get the fluid instance.
     * @param builder the constructor lambda.
     * @return self.
     */
    @Mandatory
    public <R extends BucketItem> FluidReg<E> bucketItem(BucketItemReg.BucketBuilder<? extends BucketItem> builder) {
        itemReg = new BucketItemReg<R>(registrationKey + ".bucket");
        itemReg.itemType((BucketItemReg.BucketBuilder<R>) builder);
        registerItem = true;
        return this;
    }

    public FluidReg<E> bucketItem(BucketItemReg<? extends BucketItem> reg) {
        itemReg = reg;
        registerItem = false;
        return this;
    }

    /**
     * If you bucket's model isn't lies under namespace:model/item, pass its location here.
     * @param resourceLocation the model location.
     * @return self.
     */
    @Optional
    public FluidReg<E> bucketModel(ResourceLocation resourceLocation) {
        if (itemReg == null) {
            crashOnNotPresent(ItemReg.class, "itemReg", "bucketModel");
            return this;
        }
        itemReg.model(resourceLocation);
        return this;
    }

    /**
     * Use this to customize your bucket's item property.
     * @param identifier Bucket property customizer lambda.
     * @return self.
     */
    @Optional
    public FluidReg<E> itemProperty(ItemReg.PropertyIdentifier identifier) {
        if (itemReg == null) {
            crashOnNotPresent(ItemReg.class, "itemReg", "itemProperty");
            return this;
        }
        itemReg.withProperty(identifier);
        return this;
    }

    /**
     * If you want your bucket to be custom rendered, use this.
     * @param flag Should your bucket to be custom rendered?
     * @return self.
     */
    @Optional
    public FluidReg<E> shouldCustomRenderItem(boolean flag) {
        if (itemReg == null) {
            crashOnNotPresent(ItemReg.class, "itemReg", "shouldCustomRenderItem");
            return this;
        }
        itemReg.shouldCustomRender(flag);
        return this;
    }

    /**
     * Which creative mode tab would your item contained in.
     * @param tab the creative mode tab.
     * @return self.
     */
    @Optional
    public FluidReg<E> tab(CreativeModeTab tab) {
        if (itemReg == null) {
            crashOnNotPresent(ItemReg.class, "itemReg", "tab");
            return this;
        }
        itemReg.tab(tab);
        return this;
    }

    /**
     * Which creative mode tab would your item contained in.
     * @param reg the creative mode tab registration.
     * @return self.
     */
    @Optional
    public FluidReg<E> tab(CreativeTabReg reg) {
        if (itemReg == null) {
            crashOnNotPresent(ItemReg.class, "itemReg", "tab");
            return this;
        }
        itemReg.tab(reg);
        return this;
    }

    /**
     * Your bucket's max stack size.
     * @param size max stack size.
     * @return self.
     */
    @Optional
    public FluidReg<E> stackTo(int size) {
        if (itemReg == null) {
            crashOnNotPresent(ItemReg.class, "itemReg", "stackTo");
            return this;
        }
        itemReg.stackTo(size);
        return this;
    }

    /**
     * If you want your bucket also has menus and screens, use this. For more info,
     * see {@link BlockReg#withItemMenu(MenuReg)}
     * @param registrationKey the registration key of your menu.
     * @param menu Your menu's constructor lambda.
     * @param screen Your screen's constructor lambda.
     * @return self.
     * @param <F> the class of your menu.
     * @param <R> the class of your screen.
     * @param <U> the class of your screen.
     */
    @Optional
    public <F extends AbstractContainerMenu, R extends Screen, U extends Screen & MenuAccess<F>> FluidReg<E>
    withMenu(String registrationKey, IContainerFactory<?> menu, MenuReg.ScreenInvoker<U> screen) {
        menuReg = new MenuReg<F, R, U>(registrationKey)
                .withMenuAndScreen((IContainerFactory<F>) menu, screen);
        registerMenu = true;
        return this;
    }

    /**
     * If you want your bucket to have menus and screens, use this.
     * @param menuReg the registration of your menu.
     * @return self.
     */
    @Optional
    public FluidReg<E> withMenu(MenuReg<?, ?, ?> menuReg) {
        this.menuReg = menuReg;
        registerMenu = false;
        return this;
    }

    /**
     * Customize the property of your fluid block.
     * @param identifier The property customizer of your fluid block.
     * @return self.
     */
    @Optional
    public FluidReg<E> withBlockProperty(BlockReg.PropertyIdentifier identifier) {
        if (block == null) {
            crashOnNotPresent(FluidBlockReg.class, "fluidBlockReg", "withBlockProperty");
            return this;
        }
        block.addProperty(identifier);
        return this;
    }

    /**
     * What color your fluid texture would be, 0xffffff(white) in default.
     * @param r red.
     * @param g green.
     * @param b blue.
     * @return self.
     */
    @Optional
    public FluidReg<E> tintColor(int r, int g, int b) {
        this.tintColor = r * 0xff * 0xff + g * 0xff + b;
        return this;
    }

    /**
     * What color your fluid texture would be, 0xffffff(white) in default.
     * @param color color value.
     * @return self.
     */
    @Optional
    public FluidReg<E> tintColor(int color) {
        this.tintColor = color;
        return this;
    }

    /**
     * If your fluid dose have an overlay, pass its resource location path here.
     * @param path The resource location path of your fluid's overlay texture.
     * @return self.
     */
    @Optional
    public FluidReg<E> overlayTexPath(String path) {
        this.overlayTexturePath = path;
        return this;
    }

    /**
     * Customize your fluid-type's property.
     * @param builder your fluid-type's property customizer lambda.
     * @return self.
     */
    @Optional
    public FluidReg<E> typeProperty(PropertyBuilder builder) {
        this.propertyBuilder = builder;
        return this;
    }

    /**
     * Customize your fluid's property.
     * @param builder your fluid's property customizer lambda.
     * @return self.
     */
    @Optional
    public FluidReg<E> fluidProperty(FluidPropertyBuilder builder) {
        builders.add(builder);
        return this;
    }

    /**
     * Submit your config to minecraft and forge registry.
     * @param registry the mod SimpleRegistry.
     * @return self.
     */
    @Mandatory
    @Override
    public FluidReg<E> submit(SimpleRegistry registry) {
        initDefaultType(registry);
        properties.translationKey(registrationKey);
        if(propertyBuilder != null) {
            propertyBuilder.build(properties);
        }
        if (flowingBuilder == null) {
            crashOnNotPresent(ForgeFlowingFluid.class, "flow", "submit");
        }
        if (stillBuilder == null) {
            crashOnNotPresent(ForgeFlowingFluid.class, "still", "submit");
        }
        block.fluid(() -> stillObject.get());
        block.submit(registry);
        // type = type == null ? initDefaultType(registry) : type;
        // RegistryObject<FluidAttributes> typeObj = registry.fluid_type().register(registrationKey, () -> properties.build());
        fluidProp = new ForgeFlowingFluid.Properties(() -> stillObject.get(), () -> flowingObject.get(), properties);
        for(FluidPropertyBuilder builder : builders)
            builder.build(fluidProp);
        if(stillBuilder != null)
            stillObject = registry.fluid().register(registrationKey + "_still", () -> stillBuilder.build(fluidProp));
        if(flowingBuilder != null)
            flowingObject = registry.fluid().register(registrationKey + "_flow", () -> flowingBuilder.build(fluidProp));
        if (registerItem) itemReg.submit(registry);
        if(menuReg != null && registerMenu) {
            if(!registry.hasMenuCache(this.toString()))
                registry.cacheMenuIn(menuReg);
        }
        return this;
    }

    @Inner
    private void initDefaultType(SimpleRegistry registry) {
        ResourceLocation stillLoc = stillTexturePath == null ? null : new ResourceLocation(registry.namespace, stillTexturePath);
        ResourceLocation flowingLoc = flowingTexturePath == null ? null : new ResourceLocation(registry.namespace, flowingTexturePath);
        ResourceLocation overlayLoc = overlayTexturePath == null ? null : new ResourceLocation(registry.namespace, overlayTexturePath);

        properties = FluidAttributes.builder(stillLoc, flowingLoc);
        if(overlayLoc != null) properties.overlay(overlayLoc);
        properties.color(tintColor);
    }

    public RegistryObject<E> still() {
        return stillObject;
    }

    public RegistryObject<E> flowing() {
        return flowingObject;
    }

    public FluidAttributes FluidAttributes() {

        return type;
    }

    public ForgeFlowingFluid stillFluid() {
        return stillObject.get();
    }

    public ForgeFlowingFluid flowingFluid() {
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
        void build(FluidAttributes.Builder properties);
    }

    public interface FluidPropertyBuilder {
        void build(ForgeFlowingFluid.Properties properties);
    }
}
