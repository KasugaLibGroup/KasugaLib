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
 * 这个注册机是用于注册药水效果。你可以用它来注册你自定义的药水效果。
 * @param <T> 药水效果类。
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
     * 调用此函数来创建一个药水效果注册机。
     * @param registrationKey 你的药水效果注册的名字。
     * Use this to create a poison effect reg.
     * @param registrationKey The registration key of your poison effect.
     */
    public EffectReg(String registrationKey) {
        super(registrationKey);
        this.attributes = new ArrayList<>();
    }

    /**
     * 你的药水效果的构造函数lambda。
     * @param builder 你的构造函数lambda。
     * @return 自身
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
     * 你的药水效果类型。它是一个有3个值的枚举：BENEFICIAL, HARMFUL 和 NEUTRAL。
     * 详见{@link MobEffectCategory}。
     * @param category 你的药水效果类型。
     * @return 自身
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
     * 你的药水效果粒子的颜色。默认为0xffffff（白色）。
     * @param r 红色 (0-255)。
     * @param g 绿色 (0-255)。
     * @param b 蓝色 (0-255)。
     * @return 自身
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
     * 你的药水效果粒子的颜色。默认为0xffffff（白色）。
     * @param integerColor 颜色值 (0x000000 - 0xffffff)。
     * @return 自身
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
     * 为你的药水效果应用属性。
     * @param attributeModifier 你的属性修改lambda。
     * @return 自身
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
     * 提交你的配置到forge和minecraft。
     * @param registry mod的SimpleRegistry。
     * @return 自身
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
