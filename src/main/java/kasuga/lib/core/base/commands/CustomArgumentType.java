package kasuga.lib.core.base.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

//public class CustomArgumentType implements ArgumentType<String> {
//    private final Function<String, ?> func;
//    private static final HashMap<Class<?>, CustomArgumentType> map = new HashMap<>();
//    private static final CustomArgumentType string = CustomArgumentType.getParser(String.class, s -> s);
//
//    private CustomArgumentType(Function<String, ?> f){
//        this.func = f;
//    }
//
//    public static  CustomArgumentType getParser(Class<?> clazz, Function<String, ?> func){
//        CustomArgumentType type = new CustomArgumentType(func);
//        map.put(clazz, type);
//        return type;
//    }
//
//    public static T getCustom(CommandContext<CommandSourceStack> pContext, String pName, Class clazz) {
//        String str = pContext.getArgument(pName, String.class);
//        return map.getOrDefault(clazz, string).func.apply(str);
//    }
//
//    @Override
//    public String parse(StringReader reader) throws CommandSyntaxException {
//        return func.apply(reader.readStringUntil(' '));
//    }
//
//    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
//        return ArgumentType.super.listSuggestions(context, builder);
//    }
//
//    @Override
//    public Collection<String> getExamples() {
//        return ArgumentType.super.getExamples();
//    }
//}
