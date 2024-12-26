package kasuga.lib.registrations.common;

import kasuga.lib.core.annos.Mandatory;
import kasuga.lib.core.annos.Optional;
import kasuga.lib.core.base.SimpleCreativeTab;
import kasuga.lib.core.base.item_helper.ExternalProperties;
import kasuga.lib.registrations.Reg;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * BucketItemReg is the registration for bucket.
 * @param <T> Class of your bucket item.
 */
public class BucketItemReg<T extends BucketItem> extends ItemReg<T> {

    @Nullable public ResourceLocation model;
    private boolean customRender = false;
    private BucketBuilder<T> builder;

    public final Item.Properties properties = new ExternalProperties();
    private RegistryObject<T> registryObject = null;
    private MenuReg<?, ?> menuReg = null;
    private Supplier<? extends ForgeFlowingFluid> fluid = null;
    private final List<TagKey<?>> tags;

    /**
     * Create a bucket item reg.
     * <p>
     * Note: If you want to set a Crafting Remainder for your item, please use
     * <p>
     * {@link kasuga.lib.core.base.item_helper.ExternalRemainderItem} or
     * <p>
     * {@link kasuga.lib.core.base.item_helper.ExternalRemainderBlockItem}.
     * <p>
     * then use {@link ExternalProperties#craftRemainder(Supplier)}
     * @param registrationKey the registration key of your bucket.
     * @param model If your bucket's model doesn't lie under the "namespace:models/item" folder, pass the location here,
     *              Pay attention that your model must be under the "namespace:models" folder. If your bucket's model just
     *              declared as usual, pass 'null' into this.
     */
    public BucketItemReg(String registrationKey, @Nullable ResourceLocation model) {
        super(registrationKey);
        this.model = model;
        this.tags = new ArrayList<>();
    }

    /**
     * If your don't need a customize model for your bucket, use this.
     * @param registrationKey the registration key of your item.
     */
    public BucketItemReg(String registrationKey) {
        super(registrationKey);
        this.model = null;
        this.tags = new ArrayList<>();
    }

    /**
     * Get a default bucket item from your fluid.
     * @param fluidReg the fluid registration to create this bucket.
     * @param model the model location of this item.
     * @return self.
     */
    public static BucketItemReg<BucketItem> defaultBucketItem(FluidReg<?> fluidReg, @Nullable ResourceLocation model) {
        BucketItemReg<BucketItem> reg = new BucketItemReg<BucketItem>(fluidReg.registrationKey, model);
        reg.itemType(BucketItem::new);
        reg.fluidType(fluidReg::stillFluid);
        return reg;
    }

    /**
     * Get a default bucket item from your fluid.
     * @param fluidReg the fluid registration to create this bucket.
     * @return self.
     */
    public static BucketItemReg<BucketItem> defaultBucketItem(FluidReg<?> fluidReg) {
        BucketItemReg<BucketItem> reg = new BucketItemReg<BucketItem>(fluidReg.registrationKey);
        reg.itemType(BucketItem::new);
        reg.fluidType(fluidReg::stillFluid);
        return reg;
    }

    /**
     * Pass your fluid supplier here, get your fluid from any fluid registries.
     * @param fluid your fluid supplier.
     * @return self.
     */
    @Mandatory
    public BucketItemReg<T> fluidType(Supplier<? extends ForgeFlowingFluid> fluid) {
        this.fluid = fluid;
        return this;
    }

    /**
     * Pass the constructor lambda of your bucket here.
     * @param builder your bucket's constructor lambda.
     * @return self.
     */
    @Mandatory
    public BucketItemReg<T> itemType(BucketBuilder<? extends BucketItem> builder) {
        this.builder = (BucketBuilder<T>) builder;
        return this;
    }

    @Optional
    public ItemReg<T> tab(CreativeTabReg tab) {
        properties.tab(tab.getTab());
        return this;
    }

    @Optional
    public ItemReg<T> tab(SimpleCreativeTab tab) {
        properties.tab(tab);
        return this;
    }

    @Optional
    public ItemReg<T> stackTo(int size) {
        properties.stacksTo(size);
        return this;
    }

    /**
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
     * Submit your config to minecraft and forge registry.
     * @param registry the mod SimpleRegistry.
     * @return self.
     */
    public BucketItemReg<T> submit(SimpleRegistry registry) {
        if(model != null) {
            registry.modelMappings().addMapping(
                    new ResourceLocation(registry.namespace, "item/" + registrationKey + ".json"), model
            );
        }
        if(customRender)
            registry.stackCustomRenderedItemIn(this.registrationKey);
        registryObject = registry.item().register(registrationKey, () -> builder.build(fluid, properties));
        if(menuReg != null && registerMenu) {
            if(!registry.hasMenuCache(this.toString())) {
                registry.cacheMenuIn(menuReg);
            }
        }
        return this;
    }

    @Override
    public T getItem() {
        return registryObject.get();
    }

    @Override
    public RegistryObject<T> getRegistryObject() {
        return registryObject;
    }

    public String getIdentifier() {
        return "bucket_item";
    }

    @Override
    ItemBuilder<T> type() {
        throw new UnsupportedOperationException("Could not call \"type()\" in this registry, please call \"bucketType()\" instead.");
    }

    BucketBuilder<T> bucketType(){return builder;}

    public interface BucketBuilder<T extends BucketItem> {
        T build(Supplier<? extends ForgeFlowingFluid> fluid, Item.Properties properties);
    }
}
