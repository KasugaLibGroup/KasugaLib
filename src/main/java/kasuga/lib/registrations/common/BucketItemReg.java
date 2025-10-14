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
 * BucketItemReg是用于桶的注册类。
 * @param <T> 你的桶物品的类。
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
     * 创建一个桶物品注册机。
     * 注意：如果你想为你的物品设置合成残留，请使用
     * {@link kasuga.lib.core.base.item_helper.ExternalRemainderItem} 或
     * {@link kasuga.lib.core.base.item_helper.ExternalRemainderBlockItem}，
     * 然后使用 {@link ExternalProperties#craftRemainder(Supplier)}
     * @param registrationKey 你的桶的注册键。
     * @param model 如果你的桶模型不在 "namespace:models/item" 文件夹下，请在此处传递位置，
     *              注意你的模型必须在 "namespace:models" 文件夹下。如果你的桶模型只是
     *              按照通常的方式声明，请传递 'null'。
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
     * 如果你不需要为你的桶自定义模型，请使用此函数。
     * @param registrationKey 你的桶的注册键。
     * If your don't need a customize model for your bucket, use this.
     * @param registrationKey the registration key of your item.
     */
    public BucketItemReg(String registrationKey) {
        super(registrationKey);
        this.model = null;
        this.tags = new ArrayList<>();
    }

    /**
     * 获取一个你的流体的默认桶物品。
     * @param fluidReg 用于创建此桶的流体注册机。
     * @param model 此物品的模型位置。
     * @return 自身。
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
     * 获取一个你的流体的默认桶物品。
     * @param fluidReg 用于创建此桶的流体注册机。
     * @return 自身。
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
     * 传入你的流体提供者。从任何流体注册机获取你的流体。
     * @param fluid 你的流体提供者。
     * @return 自身
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
     * 传入你的桶物品的构造器lambda。
     * @param builder 你的桶物品的构造器lambda。
     * @return 自身
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
     * 自定义你的物品属性
     * @param identifier 物品属性定制器
     * @return 自身
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
     * 将你的配置提交至minecraft和forge注册机。
     * @param registry mod的SimpleRegistry。
     * @return 自身
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
