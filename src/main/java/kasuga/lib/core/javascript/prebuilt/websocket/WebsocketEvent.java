package kasuga.lib.core.javascript.prebuilt.websocket;

import io.netty.buffer.ByteBuf;
import kasuga.lib.core.javascript.engine.annotations.HostAccess;

public class WebsocketEvent {

    public static class MessageEvent extends WebsocketEvent{
        @HostAccess.Export
        public final Object data;
        @HostAccess.Export
        public final String lastEventId = "";

        MessageEvent(String data){
            this.data = data;
        }

        MessageEvent(ByteBuf buf){
            this.data = buf.array();
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
