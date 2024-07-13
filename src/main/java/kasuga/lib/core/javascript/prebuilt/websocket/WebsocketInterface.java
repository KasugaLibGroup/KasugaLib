package kasuga.lib.core.javascript.prebuilt.websocket;

import io.netty.buffer.ByteBuf;
import kasuga.lib.core.util.data_type.Pair;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Value;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Queue;
import java.util.function.Consumer;

public class WebsocketInterface {
    private final WebsocketHandler handler;

    Queue<Pair<WebsocketEvent,Consumer<WebsocketEvent>>> queue = new ArrayDeque<>();

    public WebsocketInterface(String url){
        this.handler = new WebsocketHandler(url);
    }

    HashMap<Value,Consumer<?>> listeners = new HashMap<>();

    @HostAccess.Export
    public void addEventListener(Value eventName, Value eventConsumer){
        if(!eventName.isString())
            throw new IllegalArgumentException("Illegal argument: invalid type for event name : expected string");
        eventConsumer.pin();
        addEventListener(eventName.asString(),eventConsumer);
    }

    public void addEventListener(String eventName, Value eventConsumer){
        if(!eventConsumer.canExecute()){
            throw new IllegalArgumentException("Invalid argument event consumer: should can execute");
        }
        switch (eventName){
            case "open":
                Consumer<WebsocketEvent.OpenEvent> openEventConsumer = (event)->{
                    this.queue.add(Pair.of(event,(e)->{
                        eventConsumer.execute(event);
                    }));
                };
                this.handler.onOpen.add(openEventConsumer);
                this.listeners.put(eventConsumer,openEventConsumer);
                break;
            case "close":
                Consumer<WebsocketEvent.CloseEvent> closeEventConsumer = (event)->{
                    this.queue.add(Pair.of(event,(e)->{
                        eventConsumer.execute(event);
                    }));
                };
                this.handler.onClose.add(closeEventConsumer);
                this.listeners.put(eventConsumer,closeEventConsumer);
                break;
            case "error":
                Consumer<WebsocketEvent.ErrorEvent> errorEventConsumer = (event)->{
                    this.queue.add(Pair.of(event,(e)->{
                        eventConsumer.execute(event);
                    }));
                };
                this.handler.onError.add(errorEventConsumer);
                this.listeners.put(eventConsumer,errorEventConsumer);
                break;
            case "message":
                Consumer<WebsocketEvent.MessageEvent> messageEventConsumer = (event)->{
                    this.queue.add(Pair.of(event,(e)->{
                        eventConsumer.execute(event);
                    }));
                };
                this.handler.onMessage.add(messageEventConsumer);
                this.listeners.put(eventConsumer,messageEventConsumer);
                break;
            default:
                throw new IllegalArgumentException("Invalid event type: "+eventName);
        }
    }

    @HostAccess.Export
    public void removeEventListener(Value eventName, Value eventConsumer){
        if(!eventName.isString())
            throw new IllegalArgumentException("Illegal argument: invalid type for event name : expected string");
        removeEventListener(eventName.asString(),eventConsumer);
    }
    public void removeEventListener(String eventName, Value eventConsumer){
        if(!this.listeners.containsKey(eventConsumer))
            return;
        Consumer<?> eventFinalConsumer = this.listeners.get(eventConsumer);
        this.handler.onMessage.remove(eventFinalConsumer);
        this.handler.onError.remove(eventFinalConsumer);
        this.handler.onClose.remove(eventFinalConsumer);
        this.handler.onOpen.remove(eventFinalConsumer);
    }

    @HostAccess.Export
    public void send(Value value){
        if(value.isString()){
            this.handler.send(value.toString());
            return;
        }
        ByteBuf buffer;
        try{
            buffer = value.as(ByteBuf.class);
        }catch (ClassCastException | IllegalStateException | PolyglotException e){
            throw new IllegalArgumentException("Failed to cast type to buffer like");
        }
        this.handler.send(buffer);
    }

    @HostAccess.Export
    public void close(){
        this.handler.close();
    }


    @HostAccess.Export
    public void ping(){
        this.handler.ping();
    }

    public void tick(){
        while(!queue.isEmpty()){
            Pair<WebsocketEvent,Consumer<WebsocketEvent>> handler = queue.poll();
            handler.getSecond().accept(handler.getFirst());
        }
    }
}
