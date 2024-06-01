package kasuga.lib.core.base.commands.ArgumentTypes;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.function.Function;

public class BaseArgument implements ArgumentType<String> {
    public static final BaseArgument STRING = new BaseArgument(s->s);

    public Function<String, Object> parser;

    public BaseArgument(Function<String, Object> parser) {
        this.parser = parser;
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        final int start = reader.getCursor();
        while (reader.canRead() && reader.getString().charAt(reader.getCursor()) != ' ') {
            reader.skip();
        }
        try{
            parser.apply(reader.getString().substring(start, reader.getCursor()));
        } catch (Exception e){
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException().createWithContext(reader,
                    reader.getString().substring(start, reader.getCursor()));
        }
        return reader.getString().substring(start, reader.getCursor());
    }
}
