package kasuga.lib.core.base.commands.ArgumentTypes;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import kasuga.lib.core.annos.Inner;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Function;

@Inner
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

    public static class Serializer implements ArgumentSerializer<BaseArgument> {
        public void serializeToNetwork(BaseArgument pArgument, FriendlyByteBuf pBuffer) {
        }

        public BaseArgument deserializeFromNetwork(FriendlyByteBuf pBuffer) {
            return BaseArgument.STRING;
        }

        public void serializeToJson(BaseArgument pArgument, JsonObject pJson) {
        }
    }
}
