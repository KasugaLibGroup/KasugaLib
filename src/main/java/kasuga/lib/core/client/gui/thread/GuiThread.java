package kasuga.lib.core.client.gui.thread;

import kasuga.lib.core.client.gui.intergration.javascript.JavascriptPlatform;
import kasuga.lib.core.client.gui.runtime.PlatformRuntime;

import java.util.concurrent.atomic.AtomicInteger;

public class GuiThread extends Thread {
    GuiContextManager contextManager = new GuiContextManager();

    final AtomicInteger shouldRunTicks  = new AtomicInteger(0);

    JavascriptPlatform platform = new JavascriptPlatform();;

    public boolean running = true;

    @Override
    public void run() {
        try{
            while(running) {
                try{
                    if(shouldRunTicks.get() <= 0){
                        synchronized (shouldRunTicks){
                            shouldRunTicks.wait();
                        }
                    }
                    if(shouldRunTicks.getAndUpdate((x)->x>0 ? x-1 : 0) > 0)
                        this.contextManager.tick();
                }catch (Exception e){
                    if(e instanceof InterruptedException ie){
                        throw ie;
                    }
                    System.err.println(e.toString());
                }
            }
        }catch (InterruptedException e){
            System.out.println(e.toString());
        }
    }

    public void dispatchTick(){
        synchronized (shouldRunTicks){
            shouldRunTicks.incrementAndGet();
            shouldRunTicks.notifyAll();
        }
    }

    public void attachContext(GuiContext context) {
        this.contextManager.addContext(context);
    }

    public PlatformRuntime<?> createRuntime() {
        return platform.createRuntime();
    }
}
