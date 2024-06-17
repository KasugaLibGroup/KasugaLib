package kasuga.lib.core.javascript.prebuilt.timer;

import kasuga.lib.core.javascript.JavascriptContext;
import kasuga.lib.core.javascript.module.Tickable;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;

import java.io.Closeable;
import java.io.IOException;

public class TimerPrebuiltModule implements Tickable, Closeable {

    KasugaTimer timer = new KasugaTimer();
    public TimerPrebuiltModule(JavascriptContext javascriptContext) {

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
    public void close() throws IOException {
        timer.close();
    }
}
