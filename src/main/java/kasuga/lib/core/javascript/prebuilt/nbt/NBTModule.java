package kasuga.lib.core.javascript.prebuilt.nbt;

import kasuga.lib.core.javascript.CompoundTagWrapper;
import kasuga.lib.core.javascript.engine.annotations.HostAccess;

public class NBTModule {
    private static final NBTModule INSTANCE = new NBTModule();

    @HostAccess.Export
    public CompoundTagWrapper createCompoundTag(){
        return new CompoundTagWrapper();
    }

    public static NBTModule getInstance() {
        return INSTANCE;
    }
}
