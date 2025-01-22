package kasuga.lib.core.javascript.prebuilt.timer;

import kasuga.lib.core.javascript.JavascriptContext;
import kasuga.lib.core.javascript.engine.annotations.HostAccess;
import kasuga.lib.core.javascript.engine.JavascriptValue;
import kasuga.lib.core.javascript.prebuilt.PrebuiltModule;

import java.util.HashMap;

public class TimerPrebuiltModule extends PrebuiltModule {
    KasugaTimer timer = new KasugaTimer();
    private HashMap<Integer, Runnable> cancelHandler = new HashMap();

    public TimerPrebuiltModule(JavascriptContext runtime) {
        super(runtime);
    }

    @Override
    protected boolean isTickable() {
        return true;
    }

    @Override
    public void tick() {
        timer.onTick();
    }
    @HostAccess.Export
    public int requestTimeout(JavascriptValue callback, JavascriptValue interval){
        JavascriptValue finalCallback = callback.cloneValue();
        finalCallback.pin();
        int[] cancelHandler = new int[1];
        cancelHandler[0] = -1;
        return cancelHandler[0] = requestScheduled(KasugaTimer.TimerType.TIMEOUT,()->{
            this.cancelHandler.remove(cancelHandler[0]);
            finalCallback.executeVoid();
            finalCallback.unpin();
        }, ()->{finalCallback.unpin();},interval);
    }

    @HostAccess.Export
    public int requestInterval(JavascriptValue callback, JavascriptValue interval){
        JavascriptValue finalCallback = callback.cloneValue();
        finalCallback.pin();
        return requestScheduled(KasugaTimer.TimerType.INTERVAL,()-> finalCallback.executeVoid(), ()->finalCallback.unpin(), interval);
    }


    public int requestScheduled(KasugaTimer.TimerType type, KasugaTimer.Callback callback, Runnable cancelHandler, JavascriptValue interval){
        if(!interval.isNumber())
            return -1;
        int cancelId = timer.register(type, callback,Math.max(1,interval.asInt() / 50));
        this.cancelHandler.put(cancelId, cancelHandler);
        return cancelId;
    }

    @HostAccess.Export
    public void clearSchedule(JavascriptValue scheduleId){
        if(!scheduleId.isNumber()){
            return;
        }
        int x = scheduleId.asInt();
        timer.unregister(x);
        Runnable cancelHandler = this.cancelHandler.remove(x);
        cancelHandler.run();
    }

    @Override
    public void close(){
        timer.close();
    }
}
