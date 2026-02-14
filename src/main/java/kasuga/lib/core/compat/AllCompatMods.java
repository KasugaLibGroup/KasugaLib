package kasuga.lib.core.compat;

import kasuga.lib.core.util.Envs;
import net.minecraftforge.fml.ModList;

public class AllCompatMods {
    public static boolean isIrisOculusPresent() {
        return !Envs.isDedicateServer() && (ModList.get().isLoaded("iris") || ModList.get().isLoaded("oculus"));
    }
}
