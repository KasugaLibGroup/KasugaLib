package kasuga.lib.core.channel.network;

import net.minecraft.network.FriendlyByteBuf;

import java.util.function.BiFunction;
import java.util.function.Function;

public class NetworkSeriaizableType<T extends NetworkSerializable> {
    Function<FriendlyByteBuf, T> deserializer;

    public NetworkSeriaizableType(Function<FriendlyByteBuf, T> deserializer){
        this.deserializer = deserializer;
    }
    public T read(FriendlyByteBuf byteBuf){
        return deserializer.apply(byteBuf);
    }

    protected static <T extends NetworkSerializable> NetworkSeriaizableType<T> createType(Function<FriendlyByteBuf, T> deserializer){
        NetworkSeriaizableType<T> type = new NetworkSeriaizableType<>(deserializer);
        return type;
    }

    protected static <T extends NetworkSerializable> NetworkSeriaizableType<T> createType(BiFunction<NetworkSeriaizableType<T>,FriendlyByteBuf, T> deserializer){
        NetworkSeriaizableType<T> type = new NetworkSeriaizableType<>(null);
        type.deserializer = byteBuf -> deserializer.apply(type, byteBuf);
        return type;
    }
}
