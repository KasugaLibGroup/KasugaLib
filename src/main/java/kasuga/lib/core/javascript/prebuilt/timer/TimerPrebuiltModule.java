package kasuga.lib.core.javascript.prebuilt.timer;

import kasuga.lib.core.javascript.JavascriptContext;
import kasuga.lib.core.javascript.prebuilt.PrebuiltModule;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;

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
    @HostAccess.DisableMethodScoping
    public int requestTimeout(KasugaTimer.Callback callback, Value interval){
        return requestScheduled(KasugaTimer.TimerType.TIMEOUT,callback,interval);
    }

    @HostAccess.Export
    @HostAccess.DisableMethodScoping
    public int requestInterval(KasugaTimer.Callback callback, Value interval){
        return requestScheduled(KasugaTimer.TimerType.INTERVAL,callback,interval);
    }

    @HostAccess.Export
    @HostAccess.DisableMethodScoping
    public int requestScheduled(KasugaTimer.TimerType type, KasugaTimer.Callback callback, Value interval){
        if(!interval.isNumber())
            return -1;
        return timer.register(type, callback,Math.max(50,interval.asInt() / 50));
    }

    @HostAccess.Export
    @HostAccess.DisableMethodScoping
    public void clearSchedule(Value scheduleId){
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
