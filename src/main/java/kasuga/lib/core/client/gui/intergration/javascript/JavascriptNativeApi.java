package kasuga.lib.core.client.gui.intergration.javascript;

import kasuga.lib.core.client.gui.KasugaTimer;
import kasuga.lib.core.client.gui.intergration.javascript.modules.websocket.WebsocketInterface;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Value;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class JavascriptNativeApi {
    private final JavascriptGuiBinding context;

    JavascriptNativeApi(JavascriptGuiBinding context){
        this.context = context;
    }

    HashSet<Integer> tracedSchedulers = new HashSet<>();

    @HostAccess.Export
    @HostAccess.DisableMethodScoping
    public JavascriptGuiContainer getGuiContainer(){
        return context.getContainer();
    }

    @HostAccess.Export
    @HostAccess.DisableMethodScoping
    public int requestInterval(Value callback,Value interval){
        if(!callback.canExecute())
            return -1;
        return requestScheduled(KasugaTimer.TimerType.INTERVAL,callback::executeVoid,interval);
    }

    Queue<Value> callbackQueue = new ArrayDeque<>();

    @HostAccess.Export
    @HostAccess.DisableMethodScoping
    public int requestTimeout(Value callback,Value interval){
        if(!callback.canExecute())
            return -1;
        AtomicInteger scheduleId = new AtomicInteger();
        scheduleId.set(requestScheduled(KasugaTimer.TimerType.TIMEOUT,()->{
            tracedSchedulers.remove(scheduleId.get());
            callbackQueue.add(callback);
        },interval));
        return scheduleId.get();
    }

    public int requestScheduled(KasugaTimer.TimerType type, KasugaTimer.Callback callback, Value interval){
        if(!interval.isNumber())
            return -1;
        int traced = this.context.getPlatform().getContext().getTimer().register(type, callback,Math.max(1,interval.asInt() / 50));
        tracedSchedulers.add(traced);
        return traced;
    }

    @HostAccess.Export
    @HostAccess.DisableMethodScoping
    public void clearSchedule(Value scheduleId){
        if(!scheduleId.isNumber() && !tracedSchedulers.contains(scheduleId.asInt())){
            return;
        }
        int x = scheduleId.asInt();
        tracedSchedulers.remove(x);
        this.context.getPlatform().getContext().getTimer().unregister(x);
    }

    public void close(){
        for (Integer tracedScheduler : this.tracedSchedulers) {
            this.context.getPlatform().getContext().getTimer().unregister(tracedScheduler);
        }
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

    void tick(){
        while(!callbackQueue.isEmpty()){
            try{
                callbackQueue.poll().executeVoid();
            }catch (PolyglotException e){
                System.err.println(e.toString());
            }
        }
        Set<WeakReference<WebsocketInterface>> shouldClean = new HashSet<>();
        for (WeakReference<WebsocketInterface> wsReference : wsReferences) {
            WebsocketInterface websocketInterface = wsReference.get();
            if(websocketInterface != null)
                websocketInterface.tick();
            else shouldClean.add(wsReference);
        }
        wsReferences.removeAll(shouldClean);
    }

    @HostAccess.Export
    public boolean isDebugging(){
        return true;
    }
}
