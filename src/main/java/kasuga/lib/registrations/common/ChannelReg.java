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
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
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
    private final HashSet<PacketBuilder<? extends Packet>> packetBuilders;
    Predicate<String> clientVersions = ((input) -> true), serverVersions = ((input) -> true);

    /**
     * Create a registry.
     * @param registrationKey name of your channel reg.
     */
    public ChannelReg(String registrationKey) {
        super(registrationKey);
        this.packetBuilders = new HashSet<>();
    }

    /**
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
     * You could use this method only in the logical client. Use this to send a {@link C2SPacket} to the server.
     * @param msg Your packet.
     */
    public void sendToServer(C2SPacket msg) {
        if(channel == null) return;
        channel.sendToServer(msg);
    }

    /**
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
     * You could only use this method in the logical server. Send your {@link S2CPacket} from the server
     * to a client.
     * @param msg your packet.
     * @param connection Connection you use.
     */
    public void sendToClient(S2CPacket msg, Connection connection) {
        channel.sendTo(msg, connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    /**
     * You could only use this method in the logical server. Send your {@link S2CPacket} from the server
     * to a single player's client.
     * @param msg your packet.
     * @param player player you would send.
     */
    public void sendToClient(S2CPacket msg, ServerPlayer player) {
        sendToClient(msg, player.connection.getConnection());
    }

    /**
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
            channel.messageBuilder((Class<T>) pair.getFirst(), id)
                    .encoder(Packet::encode)
                    .decoder((Function<FriendlyByteBuf, T>) pair.getSecond())
                    .consumer((first, second) -> (first.onReach(second.get()) || false))
                    .add();
        }
        return this;
    }

    interface PacketBuilder<T extends C2SPacket> {
        void build();
    }
}
