package kasuga.lib.core.channel.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;

public class NetworkSerializableRegistry<I extends NetworkSerializable,T extends NetworkSeriaizableType<? extends I>> {
    protected HashMap<ResourceLocation, NetworkSeriaizableType<?>> REGISTRY = new HashMap<>();
    protected HashMap<T, ResourceLocation> REVERSE_REGISTRY = new HashMap<>();
    public <P extends NetworkSeriaizableType<?>> P register(ResourceLocation name, P type){
        REGISTRY.put(name, type);
        REVERSE_REGISTRY.put((T) type, name);
        return type;
    }

    public T get(ResourceLocation name){
        return (T) REGISTRY.get(name);
    }

    public I read(ResourceLocation name, FriendlyByteBuf byteBuf){
        return get(name).read(byteBuf);
    }

    public I read(FriendlyByteBuf byteBuf){
        T type = get(byteBuf.readResourceLocation());
        if(type == null){
            throw new IllegalArgumentException("Unknown NetworkSerializable type: " + type);
        }
        return type.read(byteBuf);
    }

    public void write(I object, FriendlyByteBuf byteBuf){
        ResourceLocation type = REVERSE_REGISTRY.get(object.getType());
        if(type == null){
            throw new IllegalArgumentException("Unknown NetworkSerializable type: " + object.getType());
        }
        byteBuf.writeResourceLocation(type);
        object.write(byteBuf);
    }
}
