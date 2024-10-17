package kasuga.lib.core.menu;

import kasuga.lib.core.menu.packet.C2SConnectMenuPacket;
import kasuga.lib.core.util.Envs;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

public class GuiMenu {
    GuiBinding currentBinding;
    UUID uuid;
    HashSet<UUID> remotes = new HashSet<>();

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
    }

    public boolean addRemote(UUID remoteId, ServerPlayer player){
        this.remotes.add(remoteId);
        return true;
    }

    public boolean addRemote(UUID remoteId){
        this.remotes.add(remoteId);
        return true;
    }

    public UUID getID(){
        return this.uuid;
    }

    public GuiBinding getBinding() {
        return this.currentBinding;
    }

    public void removeConnecting(UUID remoteUUID) {

    }

}
