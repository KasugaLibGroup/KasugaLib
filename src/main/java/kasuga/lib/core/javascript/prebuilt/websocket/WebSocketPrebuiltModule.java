package kasuga.lib.core.javascript.prebuilt.websocket;

import kasuga.lib.core.javascript.JavascriptContext;
import kasuga.lib.core.javascript.engine.annotations.HostAccess;
import kasuga.lib.core.javascript.engine.JavascriptValue;
import kasuga.lib.core.javascript.prebuilt.PrebuiltModule;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

public class WebSocketPrebuiltModule extends PrebuiltModule {
    public WebSocketPrebuiltModule(JavascriptContext runtime) {
        super(runtime);
    }

    protected Set<WeakReference<WebsocketInterface>> wsReferences = new HashSet<>();

    @HostAccess.Export
    public WebsocketInterface createWebSocket(JavascriptValue url){
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
    public void close() {
        for (WeakReference<WebsocketInterface> wsReference : wsReferences) {
            WebsocketInterface wsi = wsReference.get();
            if(wsi != null)
                wsi.close();
        }
    }

    @Override
    protected boolean isTickable() {
        return true;
    }
}
