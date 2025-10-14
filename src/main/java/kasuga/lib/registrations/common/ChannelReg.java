package kasuga.lib.registrations.common;

import kasuga.lib.core.annos.Inner;
import kasuga.lib.core.annos.Mandatory;
import kasuga.lib.core.annos.Optional;
import kasuga.lib.core.network.C2SPacket;
import kasuga.lib.core.network.Packet;
import kasuga.lib.core.network.S2CPacket;
import kasuga.lib.core.util.data_type.Pair;
import kasuga.lib.registrations.Reg;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 频道是用于网络数据包的。如果你有一些自定义数据需要在逻辑客户端和逻辑服务器之间传输，
 * 有关逻辑侧的更多信息，请参见{@link net.minecraftforge.fml.LogicalSide}
 * 注册后，您可以使用此频道来传输您的数据包。
 * 有关从客户端到服务器的数据包，请参见{@link C2SPacket}。
 * 有关从服务器到客户端的数据包，请参见{@link S2CPacket}
 * Channel is used for network packages. If you have some custom data need to be transmitted between
 * Logical Client and Logical Server, for more info about Logical Side, see {@link net.minecraftforge.fml.LogicalSide}
 * After registration, You could use this channel to transmit your packets.
 * For packets from client to server, see {@link C2SPacket}.
 * For packets from server to client, see {@link S2CPacket}
 */
public class ChannelReg extends Reg {
    private SimpleChannel channel = null;
    private String brand = registrationKey;
    private int id = 0;
    private final LinkedList<PacketBuilder<? extends Packet>> packetBuilders;
    Predicate<String> clientVersions = ((input) -> true), serverVersions = ((input) -> true);

    /**
     * 创建一个频道注册机。
     * @param registrationKey 你的频道注册机的键。
     * Create a registry.
     * @param registrationKey name of your channel reg.
     */
    public ChannelReg(String registrationKey) {
        super(registrationKey);
        this.packetBuilders = new LinkedList<>();
    }

    /**
     * 你的频道的版本。
     * @param brand 版本。
     * @return 自身
     * Your channel's version.
     * @param brand version.
     * @return self.
     */
    @Mandatory
    public ChannelReg brand(String brand) {
        this.brand = brand;
        return this;
    }

    /**
     * 传入一个接口方法。不同玩家可能使用不同版本的你的mod在联机游戏中。
     * 游戏会使用这个函数来检查不同版本的你的数据包。
     * 如果本方法返回false，游戏将拒绝这个数据包。
     * @param clientVersions 这个接口方法。通常是一个lambda，默认情况下只返回
     * @return 自身
     * Pass a predicate method here. Different player may use different version of your mod in multiplayer.
     * The game would use this function to examine different versions of your packet.
     * If this method return false, the game will reject the packet.
     * @param clientVersions the predicate method. Usually a lambda, just return True in default.
     * @return self.
     */
    @Optional
    public ChannelReg clientVersions(@Nonnull Predicate<String> clientVersions) {
        this.clientVersions = Objects.requireNonNull(clientVersions);
        return this;
    }

    /**
     * 传入一个接口方法。不同玩家可能使用不同版本的你的mod在联机游戏中。
     * 游戏会使用这个函数来检查不同版本的你的数据包。
     * 如果本方法返回false，游戏将拒绝这个数据包。
     * @param serverVersions 这个接口方法。通常是一个lambda，默认情况下只返回
     * @return 自身
     * Pass a predicate method here. Different player may use different version of your mod in multiplayer.
     * The game would use this function to examine different versions of your packet.
     * If this method return false, the game will reject the packet.
     * @param serverVersions the predicate method. Usually a lambda, just return True in default.
     * @return self.
     */
    @Optional
    public ChannelReg serverVersions(@Nonnull Predicate<String> serverVersions) {
        this.serverVersions = Objects.requireNonNull(serverVersions);
        return this;
    }

    /**
     * 注册一个数据包到这个频道。客户端到服务器的数据包，见{@link C2SPacket}，
     * 服务器到客户端的数据包，见{@link S2CPacket}
     * @param packetClass 你的数据包类。
     * @param decoder 解码器方法。
     * @return 自身
     * Register a packet into this channel. Client to server packet, see {@link C2SPacket},
     * server to client packet, see {@link S2CPacket}
     * @param packetClass Class of your packet.
     * @param decoder The decoder method of your packet.
     * @return self.
     */
    @Optional
    public ChannelReg loadPacket(Class<?> packetClass, Function<FriendlyByteBuf, ?> decoder) {
        boolean flag0 = C2SPacket.class.isAssignableFrom(packetClass);
        boolean flag1 = S2CPacket.class.isAssignableFrom(packetClass);
        if(!flag0 && !flag1) return this;
        loadPacket(Pair.of(packetClass, decoder));
        return this;
    }

    /**
     * 提交你的注册到forge和minecraft。
     * @param registry mod的SimpleRegistry。
     * @return 自身
     * Submit your registration to forge and minecraft.
     * @param registry the mod SimpleRegistry.
     * @return self.
     */
    @Override
    @Mandatory
    public ChannelReg submit(SimpleRegistry registry) {
        channel = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(registry.namespace, registrationKey),
                () -> brand,
                clientVersions,
                serverVersions
        );
        if(!packetBuilders.isEmpty()){
            for (PacketBuilder<?> builder : packetBuilders) {
                builder.build();
            }
            packetBuilders.clear();
        }
        return this;
    }

    public SimpleChannel getChannel() {
        return channel;
    }

    /**
     * 你应当仅在逻辑客户端使用此方法。使用此方法向服务器发送{@link C2SPacket}。
     * @param msg 你的数据包。
     * You could use this method only in the logical client. Use this to send a {@link C2SPacket} to the server.
     * @param msg Your packet.
     */
    public void sendToServer(C2SPacket msg) {
        if(channel == null) return;
        channel.sendToServer(msg);
    }

    /**
     * 向特定侧通过给定连接发送数据包。
     * @param msg 你的数据包
     * @param connection 你使用的连接。
     * @param direction 发送到的网络方向。
     * Send your packet to specific side via given connection.
     * @param msg Your packet
     * @param connection The connection you use.
     * @param direction The network direction to send to.
     */
    public void sendTo(Packet msg, Connection connection, NetworkDirection direction) {
        if(channel == null) return;
        channel.sendTo(msg, connection, direction);
    }

    /**
     * 你应当仅在逻辑服务器使用此方法。使用此方法将{@link S2CPacket}从服务器发送到客户端。
     * @param msg 你的数据包。
     * @param connection 你使用的连接。
     * You could only use this method in the logical server. Send your {@link S2CPacket} from the server
     * to a client.
     * @param msg your packet.
     * @param connection Connection you use.
     */
    public void sendToClient(S2CPacket msg, Connection connection) {
        channel.sendTo(msg, connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    /**
     * 你应当仅在逻辑服务器使用此方法。使用此方法将{@link S2CPacket}从服务器发送到单个玩家的客户端。
     * @param msg 你的数据包。
     * @param player 你将发送的玩家。
     * You could only use this method in the logical server. Send your {@link S2CPacket} from the server
     * to a single player's client.
     * @param msg your packet.
     * @param player player you would send.
     */
    public void sendToClient(S2CPacket msg, ServerPlayer player) {
        sendToClient(msg, player.connection.getConnection());
    }

    /**
     * 你应当仅在逻辑服务器使用此方法。使用此方法将{@link S2CPacket}从服务器发送到所有连接的客户端。
     * @param msg 你的数据包。
     * @param level 服务器等级。
     * @param pos 你发送此数据包的方块位置。
     * You could only use this method in the logical server. Send your {@link S2CPacket} from the server
     * to all connected client.
     * @param msg your packet.
     * @param level The server level.
     * @param pos the block pos you sent this packet.
     */
    public void boardcastToClients(S2CPacket msg, ServerLevel level, BlockPos pos) {
        level.getChunkSource().chunkMap.getPlayers(level.getChunk(pos).getPos(), false)
                .forEach(player -> sendToClient(msg, player));
    }

    @Override
    public String getIdentifier() {
        return "channel";
    }

    @Inner
    private <T extends Packet> ChannelReg loadPacket(Pair<Class<?>, Function<FriendlyByteBuf, ?>> pair) {
        if(channel == null) {
            packetBuilders.add(() -> loadPacket(pair));
        } else {
            channel.messageBuilder((Class<T>) pair.getFirst(), id++)
                    .encoder(Packet::encode)
                    .decoder((Function<FriendlyByteBuf, T>) pair.getSecond())
                    .consumerNetworkThread((first, second) -> (first.onReach(second.get()) || false))
                    .add();
        }
        return this;
    }

    interface PacketBuilder<T extends C2SPacket> {
        void build();
    }
}
