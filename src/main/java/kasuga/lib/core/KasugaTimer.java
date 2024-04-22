package kasuga.lib.core;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class KasugaTimer {

    static public enum TimerType{
        TIMEOUT,
        INTERVAL
    }

    @FunctionalInterface
    static public interface Callback{
        void tick();
    }

    static public class CallbackEntry implements Comparable<CallbackEntry>{
        final int id;
        final TimerType type;
        final Callback callback;
        long nextTicks;

        long durations;

        CallbackEntry(int timerId,TimerType type,Callback callback,long durations,long ticks){
            this.id = timerId;
            this.type = type;
            this.callback = callback;
            this.nextTicks = ticks;
        }

        @Override
        public int compareTo(@NotNull CallbackEntry target) {
            return Long.compare(this.nextTicks,target.nextTicks);
        }
    }

    public static final KasugaTimer CLIENT = new KasugaTimer();

    public final PriorityQueue<CallbackEntry> queue = new PriorityQueue<>();

    public final HashMap<Integer,CallbackEntry> queryMap = new HashMap<>();

    private final AtomicInteger counter = new AtomicInteger(1);

    private long ticksCounter = 0;

    public void onTick(){
        ticksCounter++;
        while(!queue.isEmpty() && queue.peek().nextTicks <= ticksCounter){
            CallbackEntry entry = queue.poll();
            if(entry.type == TimerType.INTERVAL){
                entry.nextTicks = ticksCounter + Math.max(entry.durations,1);
                this.queue.add(entry);
            }else{
                this.queryMap.remove(entry.id);
            }
            entry.callback.tick();
        }
    }

    public int register(TimerType type, Callback callback, int ticks){
        int id = counter.incrementAndGet();
        wanted(new CallbackEntry(id,type,callback,ticks,ticksCounter + ticks));
        return id;
    }

    public void unregister(int id){
        CallbackEntry entry = this.queryMap.get(id);
        this.queue.remove(entry);
    }

    protected void wanted(CallbackEntry entry){
        this.queryMap.put(entry.id,entry);
        this.queue.add(entry);
    }
}
