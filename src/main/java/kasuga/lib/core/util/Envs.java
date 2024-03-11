package kasuga.lib.core.util;

import net.minecraftforge.fml.loading.FMLLoader;

public class Envs {
    public static boolean isDevEnvironment() {
        return FMLLoader.isProduction();
    }

    public static boolean isClient() {
        return FMLLoader.getDist().isClient();
    }
    public static boolean isDedicateServer() {
        return FMLLoader.getDist().isDedicatedServer();
    }
}
