package kasuga.lib.core.menu.base;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.channel.address.ConnectionInfo;
import kasuga.lib.core.channel.address.FeatureChannelPort;
import kasuga.lib.core.channel.network.address.MinecraftServerAddress;
import kasuga.lib.core.channel.peer.*;
import kasuga.lib.core.client.frontend.common.event.MessageEvent;
import kasuga.lib.core.menu.network.GuiClientMenuAddress;
import kasuga.lib.core.menu.network.GuiServerMenuAddress;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import java.util.HashMap;
import java.util.UUID;

public class GuiMenu {
    UUID id;
    protected GuiBinding binding;
    protected GuiMenuType<?> type;
    protected ChannelPeer peer;
    protected ChannelHandle handle;
    protected HashMap<Channel, ChannelHandle> handles = new HashMap<>();
    protected boolean isDifferentiated = false;
    protected boolean isServer = false;
    private ConnectionInfo remoteInfo;
    private boolean isGuiInstanceCreated = false;

    protected GuiMenu(GuiMenuType<?> type, GuiBinding binding){
        this.id = UUID.randomUUID();
        this.type = type;
        this.binding = binding;
    }

    protected void createGuiInstance(){
        if(isGuiInstanceCreated){
            return;
        }
        isGuiInstanceCreated = true;
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                ()->()-> BindingClient.createInstance(this,this.id,binding.sourceCodeLocation)
        );
    }

    protected void closeGuiInstance(){
        if(!isGuiInstanceCreated){
            return;
        }
        isGuiInstanceCreated = false;
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT,()->()->BindingClient.closeInstance(this.id));
    }

    public UUID asServer(){
        if(isDifferentiated){
            if(!isServer){
                throw new IllegalStateException("This is not a server instance");
            }
            return id;
        }
        this.isDifferentiated = true;
        this.isServer = true;
        FeaturedChannelPeer peer = new FeaturedChannelPeer(GuiServerMenuAddress.of(this.id));
        peer.openPort(
                FeatureChannelPort.of(KasugaLib.STACKS.REGISTRY.asResource("menu")),
                (channelPeerSocketServer) -> this.createServerHandler(channelPeerSocketServer.getChannel())
            );
        this.peer = peer;
        return id;
    }

    public void asClient(UUID serverId){
        if(isDifferentiated) {
            throw new IllegalStateException("This instance is already differentiated");
        }
        this.isDifferentiated = true;
        this.isServer = false;
        this.peer = new ChannelPeer(GuiClientMenuAddress.of(this.id));
        this.remoteInfo = ConnectionInfo.of(
            FeatureChannelPort.of(KasugaLib.STACKS.REGISTRY.asResource("menu")),
            GuiServerMenuAddress.of(serverId),
            MinecraftServerAddress.INSTANCE.get()
        );
        this.peer.createSocket(remoteInfo, this.createClientHandler());
    }

    private void clientForwardMessageToGuiInstance(CompoundTag message){
        if(isGuiInstanceCreated){
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT,()->()->{
                BindingClient.dispatchGuiEvent(this.id,new MessageEvent(true, message));
            });
        }
    }

    protected void setChannelHandle(ChannelHandle handle){
        this.handle = handle;
    }

    private ChannelHandler createClientHandler() {
        return new ChannelHandler() {
            @Override
            public void onChannelEstabilished(ChannelHandle channel) {
                setChannelHandle(channel);
                createGuiInstance();
            }

            @Override
            public void onChannelClose(ChannelHandle channel) {
                if(isGuiInstanceCreated){
                    closeGuiInstance();
                }
                setChannelHandle(null);
            }

            @Override
            public void onChannelMessage(ChannelHandle channel, CompoundTag payload) {
                clientForwardMessageToGuiInstance(payload);
            }
        };
    }

    private ChannelHandler createServerHandler(Channel channel){
        return new ChannelHandler() {
            @Override
            public void onChannelEstabilished(ChannelHandle handle) {
                handles.put(channel, handle);
                onInit(channel, handle);
            }

            @Override
            public void onChannelClose(ChannelHandle handle) {
                handles.remove(channel);
                onClose(channel, handle);
            }

            @Override
            public void onChannelMessage(ChannelHandle handle, CompoundTag payload) {
                onMesssage(channel, handle, payload);
            }
        };
    }


    public ChannelHandle getChannel() {
        return handle;
    }

    public ChannelHandle getChannel(ServerPlayer player){
        return null;
    }

    public ChannelHandle getChannel(UUID clientId){
        return null;
    }

    // Server Message APIs
    public void onInit(Channel channel, ChannelHandle handle){}
    public void onClose(Channel channel, ChannelHandle handle){}
    public void onMesssage(Channel channel, ChannelHandle handle, CompoundTag payload){}

    public void close(){
        if(isDifferentiated){
            if(isServer){
                for(ChannelHandle handle : handles.values()){
                    handle.close();
                }
            }else{
                handle.close();
            }
        }
    }

    public void broadcast(CompoundTag message){
        if(isDifferentiated){
            if(isServer){
                for(ChannelHandle handle : handles.values()){
                    handle.sendMessage(message);
                }
            }else{
                handle.sendMessage(message);
            }
        }
    }

    public GuiBinding getBinding() {
        return binding;
    }
}
