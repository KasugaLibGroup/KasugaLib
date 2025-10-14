package kasuga.lib.registrations.common;

import kasuga.lib.core.annos.Inner;
import kasuga.lib.core.annos.Mandatory;
import kasuga.lib.core.annos.Optional;
import kasuga.lib.registrations.EntityRendererBuilder;
import kasuga.lib.registrations.Reg;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * 实体是Minecraft的基本元素。我们用它来创建生物、矿车和其他可移动的创造物。
 * 详见{@link Entity}。
 * @param <T> 你的实体的类。
 * Entity is a basic element of minecraft. We use it to create mobs, minecarts and other movable creators.
 * See {@link Entity} for more info.
 * @param <T> the class of your entity.
 */
public class EntityReg<T extends Entity> extends Reg {
    EntityBuilder<T> builder;
    RegistryObject<EntityType<T>> registryObject;
    MobCategory mobCategory;
    public final List<Object> externalArgs;
    private EntityAttributeBuilder attributeBuilder;
    private Supplier<EntityRendererBuilder<T>> provider = null;
    private EntityPropertyIdentifier<T> identifier;

    public float height = 1f, width = 1f;

    /**
     * 创建一个实体注册机。
     * @param registrationKey 你的实体注册的名字。
     * Create an entity reg.
     * @param registrationKey the name of your entity.
     */
    public EntityReg(String registrationKey) {
        super(registrationKey);
        externalArgs = new ArrayList<>();
    }

    /**
     * 你的实体实例的函数接口。
     * @param builder 实体实例接口。
     * @return 自身
     * The supplier of your entity instance.
     * @param builder entity instance supplier.
     * @return self.
     */
    @Mandatory
    public EntityReg<T> entityType(EntityBuilder<T> builder) {
        this.builder = builder;
        return this;
    }

    /**
     * 你的实体渲染的函数接口。详见{@link net.minecraft.client.renderer.entity.EntityRenderer}
     * @param provider 实体渲染接口。
     * @return 自身
     * The supplier of your entity's renderer. For more info, see
     * {@link net.minecraft.client.renderer.entity.EntityRenderer}
     * @param provider the supplier of your renderer.
     * @return self.
     */
    @Mandatory
    public EntityReg<T> withRenderer(Supplier<EntityRendererBuilder<T>> provider) {
        this.provider = provider;
        return this;
    }

    /**
     * 你的实体属性的函数接口。例如，所有生物实体必须有'MAX_HEALTH'属性，所以你必须在这里注册它的属性。
     * 注册示例，见{@link Sheep#createAttributes()}。
     * @param builder 你的实体属性的供应接口。
     * @return 自身
     * The supplier of your entity's attribute. For example, Any living entity must has the
     * property 'MAX_HEALTH', so you must register it's attribute here.
     * For registration example, see {@link Sheep#createAttributes()}.
     * @param builder the supplier of your entity's attributes.
     * @return self.
     */
    @Mandatory
    public EntityReg<T> attribute(EntityAttributeBuilder builder) {
        this.attributeBuilder = builder;
        return this;
    }

    /**
     * 你的实体的大小，通常是一个底部为正方形的长方体。
     * @param width 你底部正方形的宽度。默认为1.0。
     * @param height 你长方体的高度。默认为1.0。
     * @return 自身
     * The size of your entity, usually a cuboid with a square bottom.
     * @param width The width of your bottom square. 1.0 in default.
     * @param height The height of your cuboid. 1.0 in default.
     * @return self.
     */
    @Optional
    public EntityReg<T> size(float width, float height) {
        this.width = width;
        this.height = height;
        return this;
    }

    /**
     * 为你的实体添加自定义属性。
     * @param identifier 属性定制接口。
     * @return 自身
     * Add some custom property to your entity.
     * @param identifier the property customize lambda.
     * @return self.
     */
    @Optional
    public EntityReg<T> addProperty(EntityPropertyIdentifier<? extends Entity> identifier) {
        this.identifier = (EntityPropertyIdentifier<T>) identifier;
        return this;
    }

    /**
     * 提交你的实体配置至minecraft与forge
     * @param registry mod的SimpleRegistry。
     * @return 自身
     * Submit your config to minecraft and forge.
     * @param registry the mod SimpleRegistry.
     * @return self.
     */
    @Mandatory
    public EntityReg<T> submit(SimpleRegistry registry) {
        if (builder == null) {
            crashOnNotPresent(EntityBuilder.class, "entityType", "submit");
        }
        EntityType.Builder<T> tBuilder = EntityType.Builder.of(builder::build, mobCategory);
        if(identifier != null)
            identifier.apply(tBuilder);
        registryObject = registry.entity().register(registrationKey, () -> tBuilder.sized(width, height).build(registrationKey));
        if(this.provider != null) {
            registry.cacheEntityIn(this);
        }
        if(this.attributeBuilder != null) {
            registry.cacheLivingEntityIn((EntityReg<? extends LivingEntity>) this);
        }
        return this;
    }

    public AttributeSupplier.Builder getAttributeSupplier() {
        return attributeBuilder.get();
    }

    public String getIdentifier() {
        return "entity";
    }

    public EntityType<T> getType() {
        return registryObject == null ? null : registryObject.get();
    }

    @Inner
    public void registerRenderer() {
        EntityRenderers.register(registryObject.get(), provider.get()::build);
    }

    public interface EntityBuilder<T extends Entity> {
        T build(EntityType<T> type, Level level);
    }

    public interface EntityPropertyIdentifier<T extends Entity> {
        void apply(EntityType.Builder<T> builder);
    }

    public interface EntityAttributeBuilder {
        AttributeSupplier.Builder get();
    }
}
