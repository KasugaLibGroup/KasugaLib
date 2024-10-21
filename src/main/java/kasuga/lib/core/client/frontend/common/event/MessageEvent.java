package kasuga.lib.core.client.frontend.common.event;

import com.caoccao.javet.annotations.V8Allow;
import com.caoccao.javet.annotations.V8Convert;
import com.caoccao.javet.enums.V8ConversionMode;
import com.caoccao.javet.enums.V8ProxyMode;
import kasuga.lib.core.javascript.CompoundTagWrapper;
import kasuga.lib.core.javascript.engine.HostAccess;
import net.minecraft.nbt.CompoundTag;

@V8Convert(mode = V8ConversionMode.AllowOnly)
public class MessageEvent extends Event{

    private CompoundTag data;

    public MessageEvent(boolean trusted, CompoundTag data) {
        super(trusted);
        this.data = data;
    }

    @HostAccess.Export
    @V8Allow
    public CompoundTagWrapper getValue(){
        return new CompoundTagWrapper(this.data);
    }

    @HostAccess.Export
    @V8Allow
    @Override
    public String getType() {
        return "message";
    }
}
