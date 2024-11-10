package kasuga.lib.core.channel.test;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.base.commands.CommandHandler;
import kasuga.lib.core.channel.address.ConnectionInfo;
import kasuga.lib.core.channel.address.FeatureChannelPort;
import kasuga.lib.core.channel.network.address.MinecraftServerAddress;
import kasuga.lib.core.channel.network.address.PlainStringAddress;
import kasuga.lib.core.channel.peer.*;
import kasuga.lib.registrations.common.CommandReg;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;

public class ChannelTest {
    public static void runAtClient(){
        ChannelPeer clientPeer = new ChannelPeer(PlainStringAddress.of("client"));
        ChannelSocket channel = clientPeer.createSocket(
                ConnectionInfo.of(
                        FeatureChannelPort.of(new ResourceLocation("kasuga_lib", "test")),
                        PlainStringAddress.of("server"),
                        MinecraftServerAddress.INSTANCE.get()
                ),
                new ChannelHandler(){
                    @Override
                    public void onChannelEstabilished(ChannelHandle channel) {
                        System.out.println("Channel established");
                        channel.sendMessage(new CompoundTag());
                    }
                    @Override
                    public void onChannelClose(ChannelHandle channel) {
                        System.out.println("Channel closed");
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
                System.out.println("A new node connected to the server");
                server.setHandler(new ChannelHandler(){
                    @Override
                    public void onChannelEstabilished(ChannelHandle channel) {
                        System.out.println("Server Channel established");
                    }

                    @Override
                    public void onChannelMessage(ChannelHandle channel, CompoundTag payload) {
                        System.out.println("Received message from client");
                    }
                });
                return true;
            }
        };
        KasugaLib.STACKS.CHANNEL.SERVER_SWITCHER.addPeer(serverPeer);
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
