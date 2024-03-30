package kasuga.lib.registrations.common;

import kasuga.lib.core.annos.Inner;
import kasuga.lib.core.annos.Mandatory;
import kasuga.lib.core.annos.Optional;
import kasuga.lib.registrations.Reg;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
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

/**
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
    private EntityRendererProvider<T> provider = null;
    private EntityPropertyIdentifier<T> identifier;
    public float height = 1f, width = 1f;

    /**
     * Create an entity reg.
     * @param registrationKey the name of your entity.
     */
    public EntityReg(String registrationKey) {
        super(registrationKey);
        externalArgs = new ArrayList<>();
    }

    /**
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
     * The supplier of your entity's renderer. For more info, see
     * {@link net.minecraft.client.renderer.entity.EntityRenderer}
     * @param provider the supplier of your renderer.
     * @return self.
     */
    @Mandatory
    public EntityReg<T> withRenderer(EntityRendererProvider<T> provider) {
        this.provider = provider;
        return this;
    }

    /**
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
     * Submit your config to minecraft and forge.
     * @param registry the mod SimpleRegistry.
     * @return self.
     */
    @Mandatory
    public EntityReg<T> submit(SimpleRegistry registry) {
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
        EntityRenderers.register(registryObject.get(), provider);
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
