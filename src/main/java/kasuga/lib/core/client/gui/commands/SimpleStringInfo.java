package kasuga.lib.core.client.gui.commands;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;

public class SimpleStringInfo implements ArgumentTypeInfo<SimpleStringParser, SimpleStringInfo.Template> {
    @Override
    public void serializeToNetwork(Template pTemplate, FriendlyByteBuf pBuffer) {

    }

    @Override
    public Template deserializeFromNetwork(FriendlyByteBuf pBuffer) {
        return null;
    }

    @Override
    public void serializeToJson(Template pTemplate, JsonObject pJson) {

    }

    @Override
    public Template unpack(SimpleStringParser pArgument) {
        return null;
    }

    public final class Template implements ArgumentTypeInfo.Template<SimpleStringParser> {
        final StringArgumentType.StringType type;

        public Template(StringArgumentType.StringType pType) {
            this.type = pType;
        }

        public SimpleStringParser instantiate(CommandBuildContext pContext) {
            return new SimpleStringParser();
        }

        public ArgumentTypeInfo<SimpleStringParser, ?> type() {
            return SimpleStringInfo.this;
        }
    }

}
