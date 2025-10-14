package kasuga.lib.registrations.common;

import kasuga.lib.core.base.commands.ArgumentTypes.BaseArgument;
import kasuga.lib.core.util.data_type.Pair;
import kasuga.lib.registrations.Reg;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.resources.ResourceLocation;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.function.Function;

public class ArgumentTypeReg extends Reg {
    public static final HashMap<String, Pair<Class, BaseArgument>> types = new HashMap<>();
    //Singleton
    public static final ArgumentTypeReg INSTANCE = new ArgumentTypeReg("argument_types");

    //Some presets
    static {
        ArgumentTypeReg.INSTANCE.registerType(byte.class, Byte::parseByte);
        ArgumentTypeReg.INSTANCE.registerType(short.class, Short::parseShort);
        ArgumentTypeReg.INSTANCE.registerType(int.class, Integer::parseInt);
        ArgumentTypeReg.INSTANCE.registerType(char.class, s->s.charAt(0));
        ArgumentTypeReg.INSTANCE.registerType(float.class, Float::parseFloat);
        ArgumentTypeReg.INSTANCE.registerType(double.class, Double::parseDouble);
        ArgumentTypeReg.INSTANCE.registerType(long.class, Long::parseLong);
        ArgumentTypeReg.INSTANCE.registerType(String.class, s->s);
        ArgumentTypeReg.INSTANCE.registerType(ResourceLocation.class, ResourceLocation::new);
        ArgumentTypeReg.INSTANCE.registerType(URL.class, s -> {
            try {
                return new URL(s);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private ArgumentTypeReg(String registrationKey) {
        super(registrationKey);
    }

    /**
     * 调用此函数来注册你自己的类型，然后使用它们。
     * @return Reg自身
     * TODO
     * Call this to register your own type before using them.*
     * Use in tour registry.
     * @return The Reg itself
     */
    public ArgumentTypeReg registerType(Class target, Function<String, Object> parser){
        ArgumentTypeReg.types.put(target.getName(), Pair.of(target, new BaseArgument(parser)));
        return this;
    }

    /**
     * 讲你的字符串解析为一个注册的类型
     * 在你的处理器中使用它
     * @param value 你的字符串
     * @param target 目标类
     * @return BaseArgument
     * Parse your string with a registered type
     * Use this inside in you handler
     * @param value Your string
     * @param target Target class
     * @return BaseArgument
     */
    public <T> T parse(String value, Class<T> target){
        if(!types.containsKey(target.getName()))
            throw new NullPointerException("No such parser");
        return (T) types.get(target.getName()).getSecond().parser.apply(value);
    }

    @Override
    public ArgumentTypeReg submit(SimpleRegistry registry) {
        return this;
    }

    @Override
    public String getIdentifier() {
        return "argument_types";
    }
}
