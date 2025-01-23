package kasuga.lib.registrations.common;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Vector3f;
import kasuga.lib.core.annos.Inner;
import kasuga.lib.core.annos.Mandatory;
import kasuga.lib.core.annos.Optional;
import kasuga.lib.core.client.model.NamedRenderTypeManager;
import kasuga.lib.registrations.Reg;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.client.Camera;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.function.Supplier;

/**
 * Use this registration to register fluids like water or lava.
 * See {@link ForgeFlowingFluid}, {@link net.minecraft.world.level.material.WaterFluid} and
 * {@link net.minecraft.world.level.material.LavaFluid}
 * @param <E> the class of your fluid.
 */
public class FluidReg<E extends ForgeFlowingFluid> extends Reg {
    private RegistryObject<E> stillObject = null;
    private RegistryObject<E> flowingObject = null;
    private FluidAttributes.Builder properties;
    private ForgeFlowingFluid.Properties fluidProp = null;
    private FluidBuilder<E> stillBuilder = null, flowingBuilder = null;
    private final ArrayList<PropertyBuilder> propertyBuilders;
    private BucketItemReg<? extends BucketItem> itemReg = null;
    private final ArrayList<FluidPropertyBuilder> builders;
    private FluidBlockReg<? extends LiquidBlock> block;
    private FluidAttributes type = null;
    private String stillTexturePath = null;
    private String flowingTexturePath = null;
    private String overlayTexturePath = null;
    private MenuReg<?, ?> menuReg = null;
    private int tintColor = 0xffffff;
    boolean registerItem = false, registerBlock = false, registerMenu = false;
    private final FluidTagReg tag;
    private String renderType = "solid";
    private CustomFillEvent customFillEvent;
    private Supplier<Item> bucketSupplier;
    private String stillRegKey, flowingRegKey;

    /**
     * Create a fluid registration.
     * @param registrationKey the registration key of your fluid.
     */
    public FluidReg(String registrationKey) {
        super(registrationKey);
        builders = new ArrayList<>();
        block = new FluidBlockReg<>(registrationKey);
        propertyBuilders = new ArrayList<>();
        tag = new FluidTagReg("forge", registrationKey, "fluids/" + registrationKey);
        stillRegKey = registrationKey;
        flowingRegKey = registrationKey + "_flow";
        customFillEvent = null;
        bucketSupplier = Items.BUCKET::asItem;
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

    @Optional
    public FluidReg<E> still(FluidBuilder<? extends E> builder, String registerName, String stillTexPath) {
        stillBuilder = (FluidBuilder<E>) builder;
        this.stillTexturePath = stillTexPath;
        this.stillRegKey = registerName;
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

    @Optional
    public FluidReg<E> flow(FluidBuilder<? extends E> builder, String registerName, String flowingTexPath) {
        flowingBuilder = (FluidBuilder<E>) builder;
        this.flowingTexturePath = flowingTexPath;
        this.flowingRegKey = registerName;
        return this;
    }

    public FluidReg<E> decreasePreBlock(int decrease) {
        return this.fluidProperty(prop -> prop.levelDecreasePerBlock(decrease));
    }

    public FluidReg<E> slopeFindDistance(int distance) {
        return this.fluidProperty(prop -> prop.slopeFindDistance(distance));
    }

    public FluidReg<E> explosionResistance(float resistance) {
        return this.fluidProperty(prop -> prop.explosionResistance(resistance));
    }

    public FluidReg<E> tickRate(int tickRate) {
        return this.fluidProperty(prop -> prop.tickRate(tickRate));
    }

    public FluidReg<E> numericProperties(int decrease, int distance, int tickRate, float resistance) {
        return decreasePreBlock(decrease)
                .slopeFindDistance(distance)
                .tickRate(tickRate)
                .explosionResistance(resistance);
    }

    /**
     * Your fluid must has a fluid block, pass your fluid-block's constructor lambda here.
     * @param builder The constructor lambda of your fluid block.
     * @return self.
     */
    @Mandatory
    public FluidReg<E> blockType(FluidBlockReg.FluidBlockBuilder<? extends LiquidBlock> builder) {
        block.blockType(builder);
        builders.add(prop -> prop.block(block::getBlock));
        registerBlock = true;
        return this;
    }

    public FluidReg<E> blockType(FluidBlockReg<? extends LiquidBlock> reg) {
        block = reg;
        builders.add(prop -> prop.block(block::getBlock));
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
        return bucketItem(registrationKey + "_bucket", builder);
    }

    public <R extends BucketItem> FluidReg<E> bucketItem(String itemRegistrationKey,
                                                         BucketItemReg.BucketBuilder<? extends BucketItem> builder) {
        itemReg = new BucketItemReg<R>(itemRegistrationKey);
        itemReg.itemType(builder);
        itemReg.fluidType(this::stillFluid);
        registerItem = true;
        return this;
    }

    public FluidReg<E> bucketItem(BucketItemReg<? extends BucketItem> reg) {
        itemReg = reg;
        registerItem = false;
        return this;
    }

    public FluidReg<E> setCustomFillEvent(CustomFillEvent event) {
        this.customFillEvent = event;
        return this;
    }

    public FluidReg<E> setEmptyBucket(Supplier<Item> bucket) {
        this.bucketSupplier = bucket;
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
     * @param <U> the class of your screen.
     */
    @Optional
    public <F extends AbstractContainerMenu, U extends Screen & MenuAccess<F>> FluidReg<E>
    withMenu(String registrationKey, IContainerFactory<?> menu, Supplier<MenuReg.FullScreenInvoker<F, U>> screen) {
        menuReg = new MenuReg<F, U>(registrationKey)
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
    public FluidReg<E> withMenu(MenuReg<?, ?> menuReg) {
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

    @Optional
    public FluidReg<E> noOcclusion() {
        return withBlockProperty(BlockBehaviour.Properties::noOcclusion);
    }

    @Optional
    public FluidReg<E> noCollision() {
        return withBlockProperty(BlockBehaviour.Properties::noCollission);
    }

    @Optional
    public FluidReg<E> noLoot() {
        return withBlockProperty(prop -> prop.lootFrom(() -> Blocks.AIR));
    }

    @Optional
    public FluidReg<E> noLootAndOcclusion() {
        return noLoot().noOcclusion();
    }

    /**
     * What color your fluid texture would be, 0xffffff(white) in default.
     * @param r red.
     * @param g green.
     * @param b blue.
     * @return self.
     */
    @Optional
    public FluidReg<E> tintColor(int r, int g, int b, int a) {
        Color color = new Color(r, g, b, a);
        this.tintColor = (a << 24) + color.getRGB();
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
        this.propertyBuilders.add(builder);
        return this;
    }

    @Mandatory
    public FluidReg<E> basicFluidProperties(int lightLevel, int density, int viscosity, boolean canSupportBoating) {
        return lightLevel(lightLevel).canSupportBoating(canSupportBoating).density(density).viscosity(viscosity);
    }

    public FluidReg<E> lightLevel(int lightLevel) {
        return this.typeProperty(prop -> prop.luminosity(lightLevel));
    }

    /**
     * This method has no corresponding inner function in 1.18.2.
     * @param flag dont use.
     * @return self.
     */
    @Deprecated
    public FluidReg<E> canSupportBoating(boolean flag) {
        // return this.typeProperty(prop -> prop.supportsBoating(flag));
        return this;
    }

    public FluidReg<E> density(int density) {
        return this.typeProperty(prop -> prop.density(density));
    }

    public FluidReg<E> viscosity(int viscosity) {
        return this.typeProperty(prop -> prop.viscosity(viscosity));
    }

    @Optional
    public FluidReg<E> sound(SoundEvent fillSound, SoundEvent emptySound) {
        return this.typeProperty(prop -> prop.sound(fillSound, emptySound));
    }

    @Optional
    public FluidReg<E> blockSound(SoundType sound) {
        block.withSound(sound);
        return this;
    }

    @Optional
    public FluidReg<E> defaultSounds() {
        return sound(SoundEvents.BUCKET_FILL, SoundEvents.BUCKET_EMPTY);
    }

    @Optional
    public FluidReg<E> setRenderType(String type) {
        this.renderType = type;
        return this;
    }

    @Optional
    public FluidReg<E> setTranslucentRenderType() {
        this.renderType = "translucent";
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
        registry.cacheFluidRenderIn(this);
        propertyBuilders.forEach(b -> b.build(properties));
        if (flowingBuilder == null) {
            crashOnNotPresent(ForgeFlowingFluid.class, "flow", "submit");
        }
        if (stillBuilder == null) {
            crashOnNotPresent(ForgeFlowingFluid.class, "still", "submit");
        }
        block.fluid(() -> stillObject.get());
        if (registerBlock) block.submit(registry);
        // type = type == null ? initDefaultType(registry) : type;
        // RegistryObject<FluidAttributes> typeObj = registry.fluid_type().register(registrationKey, () -> properties.build());
        fluidProp = new ForgeFlowingFluid.Properties(() -> stillObject.get(), () -> flowingObject.get(), properties);
        fluidProp.bucket(this::bucket);
        for(FluidPropertyBuilder builder : builders)
            builder.build(fluidProp);
        if(stillBuilder != null)
            stillObject = registry.fluid().register(stillRegKey, () -> stillBuilder.build(fluidProp));
        if(flowingBuilder != null)
            flowingObject = registry.fluid().register(flowingRegKey, () -> flowingBuilder.build(fluidProp));
        if (registerItem) itemReg.submit(registry);
        if(menuReg != null && registerMenu) {
            if(!registry.hasMenuCache(this.toString()))
                registry.cacheMenuIn(menuReg);
        }
        MinecraftForge.EVENT_BUS.addListener(this::getFillResult);
        tag.submit(registry);
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

    public FluidAttributes fluidAttributes() {
        return stillObject.get().getAttributes();
    }

    public ForgeFlowingFluid stillFluid() {
        return stillObject.get();
    }

    public ForgeFlowingFluid flowingFluid() {
        return flowingObject.get();
    }

    public RegistryObject<? extends BucketItem> itemRegistryObject() {
        return itemReg.getRegistryObject();
    }

    public BucketItem bucket() {
        return itemReg.getItem();
    }

    public LiquidBlock legacyBlock() {
        return block.getBlock();
    }

    public String getRenderType() {
        return renderType;
    }

    @OnlyIn(Dist.CLIENT)
    public RenderType genRenderType() {
        return NamedRenderTypeManager.get(new ResourceLocation(renderType));
    }

    @Override
    public String getIdentifier() {
        return "fluid";
    }

    @SubscribeEvent
    public ItemStack getFillResult(FillBucketEvent event) {
        if (customFillEvent != null)
            return customFillEvent.get(event, this);
        if (event.getTarget() == null ||
                event.getTarget().getType() != HitResult.Type.BLOCK) return event.getEmptyBucket();
        Player player = event.getPlayer();
        if (!player.getItemInHand(InteractionHand.MAIN_HAND).is(bucketSupplier.get()))
            return player.getItemInHand(InteractionHand.MAIN_HAND);
        BlockHitResult bhr = (BlockHitResult) event.getTarget();
        BlockPos pos = new BlockPos(bhr.getBlockPos());
        BlockState state = event.getWorld().getBlockState(pos);
        if (!state.is(this.block.getBlock())) return event.getEmptyBucket();
        LiquidBlock bp = (LiquidBlock) state.getBlock();
        if (!bp.getFluid().isSource(bp.getFluidState(state))) return event.getEmptyBucket();
        event.setResult(Event.Result.ALLOW);
        Level level = event.getWorld();
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        ItemStack result = this.bucket().getDefaultInstance();
        event.setFilledBucket(result);
        SoundEvent sound = fluidAttributes().getFillSound();
        if (sound != null) {
            level.playSound(player, player.getOnPos(), sound,
                    SoundSource.PLAYERS, 1, 1);
        }
        return result;
    }

    public interface CustomFillEvent {
        ItemStack get(FillBucketEvent event, FluidReg<?> reg);
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

    public interface BucketBuilder<T extends BucketItem> {
        T build(Supplier<ForgeFlowingFluid> fluid, Item.Properties properties);
    }
}
