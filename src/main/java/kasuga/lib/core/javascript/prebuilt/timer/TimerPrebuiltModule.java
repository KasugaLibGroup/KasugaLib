package kasuga.lib.core.javascript.prebuilt.timer;

import kasuga.lib.core.javascript.JavascriptContext;
import kasuga.lib.core.javascript.engine.HostAccess;
import kasuga.lib.core.javascript.engine.JavascriptValue;
import kasuga.lib.core.javascript.prebuilt.PrebuiltModule;

public class TimerPrebuiltModule extends PrebuiltModule {
    KasugaTimer timer = new KasugaTimer();
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
        return requestScheduled(KasugaTimer.TimerType.TIMEOUT,()->callback.executeVoid(),interval);
    }

    @HostAccess.Export
    public int requestInterval(JavascriptValue callback, JavascriptValue interval){
        return requestScheduled(KasugaTimer.TimerType.INTERVAL,()->callback.executeVoid(),interval);
    }


    public int requestScheduled(KasugaTimer.TimerType type, KasugaTimer.Callback callback, JavascriptValue interval){
        if(!interval.isNumber())
            return -1;
        return timer.register(type, callback,Math.max(50,interval.asInt() / 50));
    }

    @HostAccess.Export
    public void clearSchedule(JavascriptValue scheduleId){
        if(!scheduleId.isNumber()){
            return;
        }
        int x = scheduleId.asInt();
        timer.unregister(x);
    }

    @Override
    public void close(){
        timer.close();
    }
}
