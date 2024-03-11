package kasuga.lib.registrations.common;

import kasuga.lib.registrations.Reg;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nonnull;
import java.util.Objects;

public class EffectReg<T extends MobEffect> extends Reg {
    private MobEffectCategory category = MobEffectCategory.NEUTRAL;
    private RegistryObject<T> registryObject = null;
    private EffectBuilder<T> builder = null;
    private int color = 0xffffff;

    public String getIdentifier() {
        return "effect";
    }

    public EffectReg(String registrationKey) {
        super(registrationKey);
    }

    public EffectReg<T> category(MobEffectCategory category) {
        this.category = category;
        return this;
    }

    public EffectReg<T> color(int integerColor) {
        this.color = integerColor;
        return this;
    }

    public EffectReg<T> effectType(@Nonnull EffectBuilder<? extends MobEffect> builder) {
        this.builder = (EffectBuilder<T>) builder;
        return this;
    }

    public EffectReg<T> color(int r, int g, int b) {
        this.color = r * 256 * 256 + g * 256 + b;
        return this;
    }

    @Override
    public Reg submit(SimpleRegistry registry) {
        Objects.requireNonNull(builder);
        registryObject = registry.mob_effect().register(registrationKey, () -> builder.build(category, color));
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

    public interface EffectBuilder<T extends MobEffect> {
        T build(MobEffectCategory category, int color);
    }
}
