package kasuga.lib.registrations.common;

import kasuga.lib.registrations.Reg;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

public class EntityReg<T extends Entity> extends Reg {

    EntityBuilder<T> builder;
    RegistryObject<EntityType<T>> registryObject;
    MobCategory mobCategory;
    public final List<Object> externalArgs;
    private EntityAttributeBuilder attributeBuilder;
    private EntityRendererProvider<T> provider = null;
    private EntityPropertyIdentifier<T> identifier;
    public float height = 1f, width = 1f;
    public EntityReg(String registrationKey) {
        super(registrationKey);
        externalArgs = new ArrayList<>();
    }

    public EntityReg<T> size(float width, float height) {
        this.width = width;
        this.height = height;
        return this;
    };

    public EntityReg<T> entityType(EntityBuilder<T> builder) {
        this.builder = builder;
        return this;
    }

    public EntityReg<T> withRenderer(EntityRendererProvider<T> provider) {
        this.provider = provider;
        return this;
    }

    public EntityReg<T> attribute(EntityAttributeBuilder builder) {
        this.attributeBuilder = builder;
        return this;
    }

    public EntityReg<T> addProperty(EntityPropertyIdentifier<? extends Entity> identifier) {
        this.identifier = (EntityPropertyIdentifier<T>) identifier;
        return this;
    }

    public AttributeSupplier.Builder getAttributeSupplier() {
        return attributeBuilder.get();
    }

    public String getIdentifier() {
        return "entity";
    }

    public EntityReg<T> submit(SimpleRegistry registry) {
        EntityType.Builder<T> tBuilder = EntityType.Builder.of(builder::build, mobCategory);
        if(identifier != null)
            identifier.apply(tBuilder);
        registryObject = registry.entity().register(registrationKey,() -> tBuilder.sized(width, height).build(registrationKey));
        if(this.provider != null) {
            registry.cacheEntityIn(this);
        }
        if(this.attributeBuilder != null) {
            registry.cacheLivingEntityIn((EntityReg<? extends LivingEntity>) this);
        }
        return this;
    }

    public EntityType<T> getType() {
        return registryObject == null ? null : registryObject.get();
    }

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
