package kasuga.lib.core.menu.javascript;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.channel.peer.Channel;
import kasuga.lib.core.channel.peer.ChannelHandle;
import kasuga.lib.core.channel.peer.ChannelPeer;
import kasuga.lib.core.client.animation.neo_neo.base.Movement;
import kasuga.lib.core.client.animation.neo_neo.key_frame.KeyFrameHolder;
import kasuga.lib.core.javascript.CompoundTagWrapper;
import kasuga.lib.core.javascript.JavascriptContext;
import kasuga.lib.core.menu.api.ChannelHandlerProxy;
import kasuga.lib.core.menu.api.ChannelProxy;
import kasuga.lib.core.menu.base.GuiMenu;
import kasuga.lib.core.menu.base.GuiMenuType;
import kasuga.lib.core.util.data_type.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class JavascriptMenu extends GuiMenu {
    private final ResourceLocation location;
    JavascriptMenuHandle handle = new JavascriptMenuHandle(this);
    private Runnable sideEffect;
    private JavascriptContext context;
    private HashMap<Channel, ChannelHandle> connections = new HashMap<>();

    protected JavascriptMenu(GuiMenuType<?> type) {
        super(type);
        this.location = getServerScriptLocation();
    }

    public JavascriptMenuHandle getHandle(){
        return handle;
    }

    abstract protected ResourceLocation getServerScriptLocation();

    @Override
    protected void initServer() {
        super.initServer();
        openJavascriptServer();
    }

    @Override
    public void close() {
        super.close();
        closeJavascriptServer();
    }

    private void openJavascriptServer() {
        if(sideEffect != null){
            return;
        }
        Pair<JavascriptMenuHandler, JavascriptContext> handler = KasugaLib.STACKS.MENU.getJavascriptRegistry().get(location);
        KasugaLib.STACKS.MENU.getJavascriptRegistry().listenReload(location ,this);
        if(handler == null){
            return;
        }
        this.context = handler.getSecond();
        ArrayList<Map.Entry<Channel, ChannelHandle>> connections = new ArrayList<>(this.connections.entrySet());
        handler.getSecond().runTask(()->{
            this.sideEffect = handler.getFirst().open(handle);
            connections.forEach((entry)->{
                handle.dispatchEvent("connection", ChannelProxy.wrap(entry.getKey(), false), ChannelHandlerProxy.wrap(entry.getValue()));
            });
        });
    }

    private CompletableFuture closeJavascriptServer(){
        CompletableFuture future = new CompletableFuture();
        KasugaLib.STACKS.MENU.getJavascriptRegistry().unlistenReload(location ,this);
        if(sideEffect != null && context!=null){
            final Runnable _sideEffect = sideEffect;
            this.context.runTask(()->_sideEffect.run());
            sideEffect = null;
            context = null;
            future.complete(new Object());
        } else {
            if(context != null){
                this.context.runTask(()->{
                    connections.forEach((channel, socketHandle)->{
                        handle.dispatchEvent("disconnection", ChannelProxy.wrap(channel, false), ChannelHandlerProxy.wrap(socketHandle));
                    });
                    closeJavascriptServer().thenRun(()->future.complete(new Object()));
                });
            }else {
                future.complete(new Object());
            }
        }
        return future;
    }

    public void reload() {
        if(context != null){
            closeJavascriptServer().thenRun(()->{
                for(ChannelHandle handle : handles.values()){
                    handle.close();
                }
                openJavascriptServer();
            });
        } else {
            openJavascriptServer();
        }
    }

    protected void provide(String key, Object value){
        handle.nativeObjects.put(key, value);
    }

    protected boolean hasProvide(String key){
        return handle.nativeObjects.containsKey(key);
    }

    @Override
    public void onInit(Channel channel, ChannelHandle socketHandle) {
        super.onInit(channel, socketHandle);
        if(context!= null){
            context.runTask(()->{
                handle.dispatchEvent("connection", ChannelProxy.wrap(channel, false), ChannelHandlerProxy.wrap(socketHandle));
            });
        }
        this.connections.put(channel, socketHandle);
    }

    @Override
    public void onMesssage(Channel channel, ChannelHandle socketHandle, CompoundTag payload) {
        super.onMesssage(channel, socketHandle, payload);
        if(context != null){
            context.runTask(()->{
                handle.dispatchEvent("message", new CompoundTagWrapper(payload), ChannelProxy.wrap(channel, false), ChannelHandlerProxy.wrap(socketHandle));
            });
        }
    }

    @Override
    public void onClose(Channel channel, ChannelHandle socketHandle) {
        super.onClose(channel, socketHandle);
        if(context != null){
            context.runTask(()->{
                handle.dispatchEvent("disconnection", ChannelProxy.wrap(channel, false), ChannelHandlerProxy.wrap(socketHandle));
            });
        }
        this.connections.remove(channel);
    }
}
