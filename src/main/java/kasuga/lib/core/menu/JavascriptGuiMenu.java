package kasuga.lib.core.menu;

import kasuga.lib.core.client.frontend.common.event.MessageEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.function.Function;

public class JavascriptGuiMenu extends GuiMenu{
    public JavascriptGuiMenu(Function<UUID, GuiBinding> bindingSupplier) {
        super(bindingSupplier);
    }

    @Override
    public void onMessage(UUID fromUUID, CompoundTag data) {
        if(isClient){
            this.forwardMessageIntoGuiInstance(data);
            return;
        }
    }

    private void forwardMessageIntoGuiInstance(CompoundTag data) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, ()->()->{
            MessageEvent messageEvent = new MessageEvent(true, data);
            BindingClient.dispatchGuiEvent(uuid, messageEvent);
        });
    }

    public boolean hasRemote() {
        return this.remotes.size() == 0;
    }
}
