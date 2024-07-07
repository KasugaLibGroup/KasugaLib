package kasuga.lib.core.javascript;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class SynchronizedThread extends Thread{
    SynchronizedThread(String name){
        super(name);
    }
    final Queue<Runnable> pendingTasks = new ArrayDeque<>();
    AtomicBoolean shouldShutdown = new AtomicBoolean(false);
    final AtomicInteger shouldRunTicks = new AtomicInteger(0);
    @Override
    public void run() {
        do{
            try{
                while(!shouldShutdown.get()){
                    try{
                        if(shouldRunTicks.get() <= 0){
                            synchronized (shouldRunTicks){
                                shouldRunTicks.wait();
                            }
                        }
                        if(shouldRunTicks.getAndUpdate((x)->x>0 ? x-1 : 0) > 0){
                            this.tick();
                        }

                        if(!pendingTasks.isEmpty())
                            pendingTasks.poll().run();

                    }catch (RuntimeException e){
                        // TODO: output using logger
                        System.out.println("An exception occurs when running the tasks");
                        e.printStackTrace();
                    }
                }
            }catch (InterruptedException ignored){}
        }while(!shouldShutdown.get());
        this.beforeStop();
    }

    protected abstract void tick();
    protected abstract void beforeStop();

    protected void dispatchTick(){
        synchronized (shouldRunTicks){
            shouldRunTicks.incrementAndGet();
            shouldRunTicks.notifyAll();
        }
    }

    public void recordCall(Runnable runnable){
        this.pendingTasks.add(runnable);
    }

    public void revokeCall(Runnable runnable){
        this.pendingTasks.remove(runnable);
    }

}
