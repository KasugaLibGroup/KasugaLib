package kasuga.lib.core.menu.network;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.channel.NetworkSwitcher;
import kasuga.lib.core.channel.route.ForwardRouteTarget;
import kasuga.lib.core.channel.route.TargetLabelMatchRule;

public class GuiMenuNetworking {
    private static NetworkSwitcher clientSwitcher;
    private static NetworkSwitcher serverSwitcher;
    private static boolean initialized = false;

    public static NetworkSwitcher getClientSwitcher() {
        return clientSwitcher;
    }

    public static NetworkSwitcher getServerSwitcher() {
        return serverSwitcher;
    }

    public static void invoke() {
        if (initialized) {
            return;
        }
        initialized = true;

        clientSwitcher = new NetworkSwitcher();
        serverSwitcher = new NetworkSwitcher();

        KasugaLib.STACKS.CHANNEL.CLIENT_ROUTER.addRule(
            TargetLabelMatchRule.create(
                MenuAddressTypes.CLIENT,
                ForwardRouteTarget.create(clientSwitcher)
            )
        );

        KasugaLib.STACKS.CHANNEL.SERVER_ROUTER.addRule(
            TargetLabelMatchRule.create(
                MenuAddressTypes.SERVER,
                ForwardRouteTarget.create(serverSwitcher)
            )
        );
    }
}