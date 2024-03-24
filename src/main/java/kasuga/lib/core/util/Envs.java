package kasuga.lib.core.util;

import kasuga.lib.core.annos.Util;
import net.minecraftforge.fml.loading.FMLLoader;

/**
 * This class is a utility for environment detecting.
 */
@Util
public class Envs {

    /**
     * Dev means the environment in IDEs like IDEA, Eclipse and so on.
     * @return is we are in dev environment?
     */
    @Util
    public static boolean isDevEnvironment() {
        return !FMLLoader.isProduction();
    }

    /**
     * Client means we are in the client game side. The client controls rendering or client ticks.
     * @return is the game a client?
     */
    @Util
    public static boolean isClient() {
        return FMLLoader.getDist().isClient();
    }

    /**
     * DedicateServer is a kind of server that used for multiplayer gaming. These servers have no client,
     * they only runs the logical side of your world.
     * @return is the game a dedicate server?
     */
    @Util
    public static boolean isDedicateServer() {
        return FMLLoader.getDist().isDedicatedServer();
    }
}
