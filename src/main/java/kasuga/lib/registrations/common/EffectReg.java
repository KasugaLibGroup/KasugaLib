package kasuga.lib.registrations.common;

import kasuga.lib.core.annos.Mandatory;
import kasuga.lib.core.annos.Optional;
import kasuga.lib.registrations.Reg;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * This registration is used for poison effect registration. You could register your custom poison effect with it.
 * @param <T> The poison effect class.
 */
public class EffectReg<T extends MobEffect> extends Reg {
    private MobEffectCategory category = MobEffectCategory.NEUTRAL;
    private RegistryObject<T> registryObject = null;
    private EffectBuilder<T> builder = null;
    private ArrayList<Consumer<T>> attributes;
    private int color = 0xffffff;

    /**
     * Use this to create a poison effect reg.
     * @param registrationKey The registration key of your poison effect.
     */
    public EffectReg(String registrationKey) {
        super(registrationKey);
        this.attributes = new ArrayList<>();
    }

    /**
     * The constructor lambda of your poison effect.
     * @param builder constructor lambda.
     * @return self
     */
    @Mandatory
    public EffectReg<T> effectType(@Nonnull EffectBuilder<? extends MobEffect> builder) {
        this.builder = (EffectBuilder<T>) builder;
        return this;
    }

    /**
     * The type of your effect. It's an enum with 3 values : BENEFICIAL, HARMFUL and NEUTRAL.
     * see {@link MobEffectCategory} for more info.
     * @param category the type of your effect.
     * @return self.
     */
    @Mandatory
    public EffectReg<T> category(MobEffectCategory category) {
        this.category = category;
        return this;
    }

    /**
     * The color of your effect's particles. 0xffffff (white) in default.
     * @param r red (0-255).
     * @param g green (0-255).
     * @param b blue (0-255).
     * @return self.
     */
    @Optional
    public EffectReg<T> color(int r, int g, int b) {
        this.color = r * 256 * 256 + g * 256 + b;
        return this;
    }

    /**
     * The color of your effect's particles. 0xffffff (white) in default.
     * @param integerColor the color value (0x000000 - 0xffffff)
     * @return self.
     */
    @Optional
    public EffectReg<T> color(int integerColor) {
        this.color = integerColor;
        return this;
    }

    /**
     * Apply and Attributes for your effect.
     * @param attributeModifier your attribute modifier lambda
     * @return self.
     */
    @Optional
    public EffectReg<T> attritube(Consumer<T> attributeModifier) {
        this.attributes.add(attributeModifier);
        return this;
    }

    /**
     * Submit your config to forge and minecraft.
     * @param registry the mod SimpleRegistry.
     * @return self.
     */
    @Override
    public EffectReg<T> submit(SimpleRegistry registry) {
        if (builder == null) {
            crashOnNotPresent(EffectBuilder.class, "effectType", "submit");
        }
        registryObject = registry.mob_effect().register(registrationKey, () -> {
            T effect = builder.build(category, color);
            this.attributes.forEach(c -> c.accept(effect));
            return effect;
        });
        return this;
    }

    public RegistryObject<T> getRegistryObject() {
        return registryObject;
    }

    public T getEffect() {
        return registryObject == null ? null : registryObject.get();
    }

    public MobEffectCategory getCategory() {
        return category;
    }

    public int getThemeColor() {
        return color;
    }

    public String getIdentifier() {
        return "effect";
    }


    public interface EffectBuilder<T extends MobEffect> {
        T build(MobEffectCategory category, int color);
    }
}
