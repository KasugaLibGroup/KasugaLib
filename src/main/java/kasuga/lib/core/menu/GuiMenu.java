package kasuga.lib.core.menu;

import kasuga.lib.core.menu.packet.*;
import kasuga.lib.core.packets.AllPackets;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import java.util.*;
import java.util.function.Function;

public class GuiMenu {
    GuiBinding currentBinding;
    UUID uuid;
    HashSet<UUID> remotes = new HashSet<>();
    HashMap<UUID, ServerPlayer> remotePlayers = new HashMap<>();

    boolean isClient = false;

    public GuiMenu(Function<UUID, GuiBinding> bindingSupplier){
        this(bindingSupplier, UUID.randomUUID());
    }

    public GuiMenu(Function<UUID, GuiBinding> bindingSupplier, UUID uuid){
        this.uuid = uuid;
        this.currentBinding = bindingSupplier.apply(this.uuid);
    }

    public void createGuiInstance(){
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT,()->()->BindingClient.createInstance(this.uuid,currentBinding.sourceCodeLocation));
    }

    public void createConnection(UUID remoteId){
        if(remotes.contains(remoteId) || uuid == remoteId)
            return;
        C2SConnectMenuPacket connectMenuPacket = new C2SConnectMenuPacket(
                uuid,
                remoteId
        );
        AllPackets.channel.sendToServer(connectMenuPacket);
    }

    public boolean addRemote(UUID remoteId, ServerPlayer player){
        if(this.remotes.contains(remoteId))
            return false;
        this.remotes.add(remoteId);
        this.remotePlayers.put(remoteId, player);
        return true;
    }

    public boolean addRemote(UUID remoteId){
        if(this.remotes.contains(remoteId))
            return false;
        this.remotes.add(remoteId);
        return true;
    }

    public UUID getID(){
        return this.uuid;
    }

    public GuiBinding getBinding() {
        return this.currentBinding;
    }

    public void removeConnecting(UUID remoteUUID) {}

    public void listen(boolean isClient){
        if(isClient){
            this.isClient = true;
            GuiMenuManager.listenFromClient(this);
        }else{
            this.isClient = false;
            GuiMenuManager.listenFromServer(this);
        }
    }

    public void unlisten(boolean isClient){
        if(isClient){
            GuiMenuManager.unlistenFromClient(this);
        }else{
            GuiMenuManager.unlistenFromServer(this);
        }
    }

    public void close(){
        this.remotes.forEach((remote)->close(remote));
    }

    public void close(UUID remote){
        if(isClient){
            AllPackets.channel.sendToServer(new C2SChannelClosedPacket(remote, uuid));
        }else{
            AllPackets.channel.sendToClient(new S2CChannelClosedPacket(remote, uuid), this.remotePlayers.get(remote));
        }
    }

    public void onClose(UUID remoteId){
        this.remotes.remove(remoteId);
        this.remotePlayers.remove(remoteId);
    }

    public void closeByPlayer(ServerPlayer player) {
        // @TODO: Improve Performance
        HashSet<UUID> needToDelete = new HashSet<>();

        this.remotePlayers.forEach((i,j)->{
            if(j==player){
                needToDelete.add(i);
            }
        });

        for (UUID id : needToDelete) {
            remotePlayers.remove(id);
        }
    }

    public void onMessage(UUID fromUUID, CompoundTag data) {

    }

    public void send(CompoundTag data){
        this.remotes.forEach((remote) -> this.send(remote, data));
    }

    public void send(UUID remote, CompoundTag data) {
        if(this.isClient){
            AllPackets.channel.sendToServer(new C2SChannelMessagePacket(uuid, remote, data));
        } else {
            AllPackets.channel.sendToClient(new S2CChannelMessagePacket(uuid, remote, data), remotePlayers.get(remote));
        }
    }
}
