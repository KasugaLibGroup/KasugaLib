package kasuga.lib.core.channel.test;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.base.commands.CommandHandler;
import kasuga.lib.core.channel.NetworkSwitcher;
import kasuga.lib.core.channel.address.ConnectionInfo;
import kasuga.lib.core.channel.address.FeatureChannelPort;
import kasuga.lib.core.channel.network.address.MinecraftServerAddress;
import kasuga.lib.core.channel.network.address.NetworkAddressTypes;
import kasuga.lib.core.channel.network.address.PlainStringAddress;
import kasuga.lib.core.channel.peer.*;
import kasuga.lib.registrations.common.CommandReg;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import kasuga.lib.core.channel.route.*;
import kasuga.lib.core.channel.address.LabelType;

public class ChannelTest {
    public static void runAtClient(){
        ChannelPeer clientPeer = new ChannelPeer(PlainStringAddress.of("client"));
        SimpleRouter clientRouter = new SimpleRouter();
        clientRouter.setDefaultReciever(KasugaLib.STACKS.CHANNEL.CLIENT_ROUTER);
        clientPeer.setDistributor(clientRouter);

        ChannelSocket channel = clientPeer.createSocket(
                ConnectionInfo.of(
                        FeatureChannelPort.of(new ResourceLocation("kasuga_lib", "test")),
                        PlainStringAddress.of("server"),
                        MinecraftServerAddress.INSTANCE.get()
                ),
                new ChannelHandler(){
                    @Override
                    public void onChannelEstabilished(ChannelHandle channel) {
                        System.out.println("[Client] Channel Established");
                        channel.sendMessage(new CompoundTag());
                    }

                    @Override
                    public void onChannelMessage(ChannelHandle channel, CompoundTag payload) {
                        System.out.println("[Client] Channel Message Recieved");
                    }

                    @Override
                    public void onChannelClose(ChannelHandle channel) {
                        System.out.println("[Client] Channel closed");
                    }
                }
        );
    }

    public static boolean serverInitilized = false;

    public static void runAtServer(){
        if(serverInitilized){
            return;
        }
        serverInitilized = true;
        ChannelPeer serverPeer = new ChannelPeer(PlainStringAddress.of("server")){
            @Override
            protected boolean onConnect(ChannelPeerSocketServer server) {
                System.out.println("[Server] Channel Connected");
                server.setHandler(new ChannelHandler(){
                    @Override
                    public void onChannelEstabilished(ChannelHandle channel) {
                        System.out.println("[Server] Channel Established");
                    }

                    @Override
                    public void onChannelMessage(ChannelHandle channel, CompoundTag payload) {
                        System.out.println("[Server] Channel Message");
                        channel.sendMessage(new CompoundTag());
                    }

                    @Override
                    public void onChannelClose(ChannelHandle channel) {
                        System.out.println("[Server] Channel Closed");
                    }
                });
                return true;
            }
        };
        NetworkSwitcher switcher = new NetworkSwitcher();

        switcher.addPeer(serverPeer);

        KasugaLib.STACKS.CHANNEL.SERVER_ROUTER.addRule(
                TargetLabelMatchRule.create(
                        NetworkAddressTypes.PLAIN_STRING,
                        ForwardRouteTarget.create(switcher)
                )
        );
    }

    public static final CommandReg NET_CLIENT = new CommandReg("kasugalib")
            .addLiteral("networking", false)
            .addLiteral("client", false)
            .setHandler(new CommandHandler() {
                @Override
                public void run() {
                    runAtClient();
                }
            })
            .submit(KasugaLib.STACKS.REGISTRY);

    public static final CommandReg NET_SERVER = new CommandReg("kasugalib")
            .addLiteral("networking", false)
            .addLiteral("server", false)
            .setHandler(new CommandHandler() {
                @Override
                public void run() {
                    runAtServer();
                }
            })
            .submit(KasugaLib.STACKS.REGISTRY);


    public static void invoke(){}
}
