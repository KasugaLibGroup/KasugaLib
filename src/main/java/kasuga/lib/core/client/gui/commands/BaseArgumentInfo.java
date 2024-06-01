package kasuga.lib.core.client.gui.commands;

import com.google.gson.JsonObject;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;

public class BaseArgumentInfo implements ArgumentTypeInfo<BaseArgument, BaseArgumentInfo.Template> {
    public BaseArgumentInfo() {
    }

    public void serializeToNetwork(BaseArgumentInfo.Template pTemplate, FriendlyByteBuf pBuffer) {
    }

    public BaseArgumentInfo.Template deserializeFromNetwork(FriendlyByteBuf pBuffer) {
        return new Template();
    }

    public void serializeToJson(BaseArgumentInfo.Template pTemplate, JsonObject pJson) {
    }

    public BaseArgumentInfo.Template unpack(BaseArgument pArgument) {
        return new Template();
    }

    public final class Template implements ArgumentTypeInfo.Template<BaseArgument> {
        public Template() {
        }

        public BaseArgument instantiate(CommandBuildContext pContext) {
            return BaseArgument.STRING;
        }

        public ArgumentTypeInfo<BaseArgument, ?> type() {
            return BaseArgumentInfo.this;
        }
    }
}