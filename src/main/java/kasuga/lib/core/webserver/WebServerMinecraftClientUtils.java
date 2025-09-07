package kasuga.lib.core.webserver;

import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.server.MinecraftServer;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class WebServerMinecraftClientUtils {
    public static boolean isIntergratedServer(MinecraftServer server) {
        return server instanceof IntegratedServer;
    }

    public static String getRemoteConnectionURL() {
        try {
            SocketAddress address = Minecraft.getInstance().getConnection().getConnection().getRemoteAddress();
            if(address instanceof InetSocketAddress inetSocketAddress) {
                return inetSocketAddress.getHostName();
            }
            return address.toString();
        } catch (NullPointerException exception) {
            return "";
        }
    }
}
