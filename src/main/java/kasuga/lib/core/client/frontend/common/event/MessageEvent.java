package kasuga.lib.core.client.frontend.common.event;

import kasuga.lib.core.javascript.CompoundTagWrapper;
import kasuga.lib.core.javascript.engine.HostAccess;
import net.minecraft.nbt.CompoundTag;

public class MessageEvent extends Event{

    private CompoundTag data;

    public MessageEvent(boolean trusted, CompoundTag data) {
        super(trusted);
        this.data = data;
    }

    @HostAccess.Export
    public CompoundTagWrapper getValue(){
        return new CompoundTagWrapper(this.data);
    }

    @HostAccess.Export
    @Override
    public String getType() {
        return "message";
    }
}
