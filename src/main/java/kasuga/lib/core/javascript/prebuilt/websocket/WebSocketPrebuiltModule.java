package kasuga.lib.core.javascript.prebuilt.websocket;

import kasuga.lib.core.javascript.JavascriptContext;
import kasuga.lib.core.javascript.module.Tickable;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;

import java.io.Closeable;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

public class WebSocketPrebuiltModule implements Tickable, Closeable {
    public WebSocketPrebuiltModule(JavascriptContext javascriptContext) {

    }



    protected Set<WeakReference<WebsocketInterface>> wsReferences = new HashSet<>();

    @HostAccess.Export
    @HostAccess.DisableMethodScoping
    public WebsocketInterface createWebSocket(Value url){
        if(!url.isString())
            throw new IllegalArgumentException("Invalid URL");
        WebsocketInterface websocketInterface = new WebsocketInterface(url.asString());
        wsReferences.add(new WeakReference<>(websocketInterface));
        return websocketInterface;
    }

    @Override
    public void tick() {
        Set<WeakReference<WebsocketInterface>> shouldClean = new HashSet<>();
        for (WeakReference<WebsocketInterface> wsReference : wsReferences) {
            WebsocketInterface websocketInterface = wsReference.get();
            if(websocketInterface != null)
                websocketInterface.tick();
            else shouldClean.add(wsReference);
        }
        wsReferences.removeAll(shouldClean);
    }

    @Override
    public void close() throws IOException {
        for (WeakReference<WebsocketInterface> wsReference : wsReferences) {
            WebsocketInterface wsi = wsReference.get();
            if(wsi != null)
                wsi.close();
        }
    }
}
