package kasuga.lib.core.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.commons.compress.archivers.sevenz.CLI;

import java.util.HashMap;

public class SimpleConfig {
    private final HashMap<String, ConfigContext<?>> values;
    private ForgeConfigSpec COMMON, CLIENT, SERVER;
    private final ForgeConfigSpec.Builder COMMON_BUILDER, CLIENT_BUILDER, SERVER_BUILDER;
    private boolean common_pushed = false, client_pushed = false, server_pushed = false;
    private ForgeConfigSpec.Builder cachedBuilder;
    public SimpleConfig() {
        COMMON_BUILDER = new ForgeConfigSpec.Builder();
        CLIENT_BUILDER = new ForgeConfigSpec.Builder();
        SERVER_BUILDER = new ForgeConfigSpec.Builder();
        values = new HashMap<>();
        cachedBuilder = COMMON_BUILDER;
    }

    public SimpleConfig client(String key) {
        popIfPushed();
        cachedBuilder = CLIENT_BUILDER;
        cachedBuilder.push(key);
        client_pushed = true;
        return this;
    }

    public SimpleConfig server(String key) {
        popIfPushed();
        cachedBuilder = SERVER_BUILDER;
        cachedBuilder.push(key);
        server_pushed = true;
        return this;
    }

    public SimpleConfig common(String key) {
        popIfPushed();
        cachedBuilder = COMMON_BUILDER;
        cachedBuilder.push(key);
        common_pushed = true;
        return this;
    }

    public SimpleConfig group(String key) {
        popIfPushed();
        cachedBuilder.push(key);
        if (cachedBuilder == COMMON_BUILDER) common_pushed = true;
        else if (cachedBuilder == CLIENT_BUILDER) client_pushed = true;
        else server_pushed = true;
        return this;
    }

    public SimpleConfig comment(String comment) {
        cachedBuilder.comment(comment);
        return this;
    }

    public SimpleConfig intConfig(String key, String comment, Integer defaultValue) {
        ForgeConfigSpec.ConfigValue<Integer> value = cachedBuilder.comment(comment).define(key, defaultValue);
        values.put(key, new ConfigContext<Integer>(value, Integer.class, key, defaultValue, Integer.MIN_VALUE, Integer.MAX_VALUE));
        return this;
    }

    public SimpleConfig intConfig(String key, Integer defaultValue) {
        ForgeConfigSpec.ConfigValue<Integer> value = cachedBuilder.define(key, defaultValue);
        values.put(key, new ConfigContext<Integer>(value, Integer.class, key, defaultValue, Integer.MIN_VALUE, Integer.MAX_VALUE));
        return this;
    }

    public SimpleConfig rangedIntConfig(String key, String comment, Integer defaultValue, Integer min, Integer max) {
        ForgeConfigSpec.IntValue value = cachedBuilder.comment(comment).defineInRange(key, defaultValue, min, max);
        values.put(key, new ConfigContext<Integer>(value, Integer.class, key, defaultValue, min, max));
        return this;
    }

    public SimpleConfig rangedIntConfig(String key, Integer defaultValue, Integer min, Integer max) {
        ForgeConfigSpec.IntValue value = cachedBuilder.defineInRange(key, defaultValue, min, max);
        values.put(key, new ConfigContext<Integer>(value, Integer.class, key, defaultValue, min, max));
        return this;
    }

    public SimpleConfig doubleConfig(String key, String comment, Double defaultValue) {
        ForgeConfigSpec.ConfigValue<Double> value = cachedBuilder.comment(comment).define(key, defaultValue);
        values.put(key, new ConfigContext<Double>(value, Double.class, key, defaultValue, Double.MIN_VALUE, Double.MAX_VALUE));
        return this;
    }

    public SimpleConfig doubleConfig(String key, Double defaultValue) {
        ForgeConfigSpec.ConfigValue<Double> value = cachedBuilder.define(key, defaultValue);
        values.put(key, new ConfigContext<Double>(value, Double.class, key, defaultValue, Double.MIN_VALUE, Double.MAX_VALUE));
        return this;
    }

    public SimpleConfig rangedDoubleConfig(String key, String comment, Double defaultValue, Double min, Double max) {
        ForgeConfigSpec.DoubleValue value = cachedBuilder.comment(comment).defineInRange(key, defaultValue, min, max);
        values.put(key, new ConfigContext<Double>(value, Double.class, key, defaultValue, min, max));
        return this;
    }

    public SimpleConfig rangedDoubleConfig(String key, Double defaultValue, Double min, Double max) {
        ForgeConfigSpec.DoubleValue value = cachedBuilder.defineInRange(key, defaultValue, min, max);
        values.put(key, new ConfigContext<Double>(value, Double.class, key, defaultValue, min, max));
        return this;
    }

    public SimpleConfig boolConfig(String key, String comment, Boolean defaultValue) {
        ForgeConfigSpec.ConfigValue<Boolean> value = cachedBuilder.comment(comment).define(key, defaultValue);
        values.put(key, new ConfigContext<Boolean>(value, Boolean.class, key, defaultValue, null, null));
        return this;
    }

    public <E extends Enum<?>> SimpleConfig enumConfig(String key, String comment, Class<E> clazz, E defaultValue) {
        ForgeConfigSpec.ConfigValue<E> value = cachedBuilder.comment(comment).define(key, defaultValue);
        values.put(key, new ConfigContext<E>(value, clazz, key, defaultValue, null, null));
        return this;
    }

    public <E extends Enum<?>> SimpleConfig enumConfig(String key, Class<E> clazz, E defaultValue) {
        ForgeConfigSpec.ConfigValue<E> value = cachedBuilder.define(key, defaultValue);
        values.put(key, new ConfigContext<E>(value, clazz, key, defaultValue, null, null));
        return this;
    }

    public SimpleConfig boolConfig(String key, Boolean defaultValue) {
        ForgeConfigSpec.ConfigValue<Boolean> value = cachedBuilder.define(key, defaultValue);
        values.put(key, new ConfigContext<Boolean>(value, Boolean.class, key, defaultValue, null, null));
        return this;
    }

    public SimpleConfig popCached() {
        popIfPushed();
        return this;
    }
    public SimpleConfig registerConfigs() {
        if (common_pushed) COMMON_BUILDER.pop();
        if (server_pushed) SERVER_BUILDER.pop();
        if (client_pushed) CLIENT_BUILDER.pop();
        common_pushed = false;
        server_pushed = false;
        client_pushed = false;
        COMMON = COMMON_BUILDER.build();
        CLIENT = CLIENT_BUILDER.build();
        SERVER = SERVER_BUILDER.build();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER);
        return this;
    }

    public boolean contains(String key) {
        return values.containsKey(key);
    }

    public boolean contains(String key, Class<?> clazz) {
        return values.containsKey(key) && values.get(key).clazz == clazz;
    }
    public boolean containsIntValue(String key) {
        return contains(key, Integer.class);
    }

    public boolean isRangedIntValue(String key) {
        if (!containsIntValue(key)) return false;
        return values.get(key).value instanceof ForgeConfigSpec.IntValue;
    }

    public boolean containsDoubleValue(String key) {
        return contains(key, Double.class);
    }

    public boolean containsBoolValue(String key) {
        return contains(key, Boolean.class);
    }

    public boolean isRangedDoubleValue(String key) {
        if (!containsDoubleValue(key)) return false;
        return values.get(key).value instanceof ForgeConfigSpec.DoubleValue;
    }

    public boolean containsEnum(String key) {
        return contains(key, Enum.class);
    }

    public Integer getIntValue(String key) {
        if (!containsIntValue(key)) return 0;
        return (Integer) values.get(key).value.get();
    }

    public Boolean getBoolValue(String key) {
        if (!containsBoolValue(key)) return false;
        return (Boolean) values.get(key).value.get();
    }

    public Double getDoubleValue(String key) {
        if (!containsDoubleValue(key)) return 0d;
        return (Double) values.get(key).value.get();
    }

    public <T extends Enum<?>> T getEnumValue(String key) {
        if (!containsEnum(key)) return null;
        return (T) values.get(key).value.get();
    }

    public Integer getDefaultInt(String key) {
        if (!containsIntValue(key)) return 0;
        return (Integer) values.get(key).defaultValue;
    }

    public Double getDefaultDouble(String key) {
        if (!containsDoubleValue(key)) return 0d;
        return (Double) values.get(key).defaultValue;
    }

    public Boolean getDefaultBool(String key) {
        if (!containsBoolValue(key)) return false;
        return (Boolean) values.get(key).defaultValue;
    }

    public <T extends Enum<?>> T getDefaultEnum(String key) {
        if (!containsEnum(key)) return null;
        return (T) values.get(key).defaultValue;
    }

    public Integer getMaxInt(String key) {
        if (!containsIntValue(key)) return Integer.MAX_VALUE;
        return (Integer) values.get(key).max;
    }

    public Integer getMinInt(String key) {
        if (!containsIntValue(key)) return Integer.MIN_VALUE;
        return (Integer) values.get(key).min;
    }

    public Double getMaxDouble(String key) {
        if (!containsDoubleValue(key)) return Double.MAX_VALUE;
        return (Double) values.get(key).max;
    }

    public Double getMinDouble(String key) {
        if (!containsDoubleValue(key)) return Double.MIN_VALUE;
        return (Double) values.get(key).min;
    }

    public ConfigContext<?> getContext(String key) {
        return values.getOrDefault(key, null);
    }

    private void popIfPushed() {
        if (cachedBuilder == COMMON_BUILDER && common_pushed) {
            cachedBuilder.pop();
            common_pushed = false;
        } else if (cachedBuilder == CLIENT_BUILDER && client_pushed) {
            cachedBuilder.pop();
            client_pushed = false;
        } else if (server_pushed) {
            cachedBuilder.pop();
            server_pushed = false;
        }
    }

    private record ConfigContext<T>(ForgeConfigSpec.ConfigValue<T> value, Class<T> clazz, String key, T defaultValue, T min, T max){}
}
