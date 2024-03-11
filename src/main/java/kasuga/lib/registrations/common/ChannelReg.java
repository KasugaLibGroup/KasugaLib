package kasuga.lib.registrations.common;

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

public class ChannelReg extends Reg {
    private SimpleChannel channel = null;
    private String brand = registrationKey;
    private int id = 0;
    private final HashSet<PacketBuilder<? extends C2SPacket>> packetBuilders;
    Predicate<String> clientVersions = ((input) -> true), serverVersions = ((input) -> true);
    public ChannelReg(String registrationKey) {
        super(registrationKey);
        this.packetBuilders = new HashSet<>();
    }

    public ChannelReg clientVersions(@Nonnull Predicate<String> clientVersions) {
        this.clientVersions = Objects.requireNonNull(clientVersions);
        return this;
    }

    public ChannelReg serverVersions(@Nonnull Predicate<String> serverVersions) {
        this.serverVersions = Objects.requireNonNull(serverVersions);
        return this;
    }

    public ChannelReg brand(String brand) {
        this.brand = brand;
        return this;
    }

    @Override
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

    public <T extends Packet> ChannelReg loadPacket(Pair<Class<?>, Function<FriendlyByteBuf, ?>> pair) {
        if(channel == null) {
            packetBuilders.add(() -> loadPacket(pair));
        } else {
            channel.messageBuilder((Class<T>) pair.getFirst(), id)
                    .encoder(Packet::encode)
                    .decoder((Function<FriendlyByteBuf, T>) pair.getSecond())
                    .consumerNetworkThread((first, second) -> (first.onReach(second.get()) || false))
                    .add();
        }
        return this;
    }

    public ChannelReg loadPacket(Class<?> packetClass, Function<FriendlyByteBuf, ?> decoder) {
        boolean flag0 = C2SPacket.class.isAssignableFrom(packetClass);
        boolean flag1 = S2CPacket.class.isAssignableFrom(packetClass);
        if(!flag0 && !flag1) return this;
        loadPacket(Pair.of(packetClass, decoder));
        return this;
    }

    public SimpleChannel getChannel() {
        return channel;
    }

    // Client Only
    public void sendToServer(C2SPacket msg) {
        if(channel == null) return;
        channel.sendToServer(msg);
    }

    public void sendTo(Packet msg, Connection connection, NetworkDirection direction) {
        if(channel == null) return;
        channel.sendTo(msg, connection, direction);
    }

    // Server Only
    public void sendToClient(S2CPacket msg, Connection connection) {
        channel.sendTo(msg, connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    // Server Only
    public void sendToClient(S2CPacket msg, ServerPlayer player) {
        sendToClient(msg, player.connection.getConnection());
    }

    // Server Only
    public void boardcastToClients(S2CPacket msg, ServerLevel level, BlockPos pos) {
        level.getChunkSource().chunkMap.getPlayers(level.getChunk(pos).getPos(), false)
                .forEach(player -> sendToClient(msg, player));
    }

    @Override
    public String getIdentifier() {
        return "channel";
    }

    interface PacketBuilder<T extends C2SPacket> {
        void build();
    }
}
