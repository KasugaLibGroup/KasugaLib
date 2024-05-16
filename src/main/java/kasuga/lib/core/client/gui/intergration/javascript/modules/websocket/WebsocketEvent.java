package kasuga.lib.core.client.gui.intergration.javascript.modules.websocket;

import io.netty.buffer.ByteBuf;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;

public class WebsocketEvent {

    public static class MessageEvent extends WebsocketEvent{
        @HostAccess.Export
        public final Value data;
        @HostAccess.Export
        public final String lastEventId = "";

        MessageEvent(String data){
            this.data = Value.asValue(data);
        }

        MessageEvent(ByteBuf buf){
            this.data = Value.asValue(buf);
        }
    }

    public static class CloseEvent extends WebsocketEvent {
        @HostAccess.Export
        public final int code;
        @HostAccess.Export
        public final String reason;

        public CloseEvent(int code, String reason) {
            this.code = code;
            this.reason = reason;
        }

        public int getCode() {
            return code;
        }

        public String getReason() {
            return reason;
        }
    }

    public static class ErrorEvent extends WebsocketEvent {
        public ErrorEvent(String message) {}
    }

    public static class OpenEvent extends WebsocketEvent {
    }
}
