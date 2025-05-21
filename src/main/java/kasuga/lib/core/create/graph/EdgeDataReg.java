package kasuga.lib.core.create.graph;

import kasuga.lib.registrations.Reg;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.nbt.CompoundTag;

import java.util.function.Function;
import java.util.function.Supplier;

public class EdgeDataReg<T extends EdgeExtraPayload> extends Reg {
    private EdgeExtraPayloadType.Builder<T> typeBuilder;
    private EdgeExtraPayloadType<T> type;

    public EdgeDataReg(String registrationKey) {
        super(registrationKey);
    }

    public EdgeDataReg<T> load(Function<CompoundTag, T> reader, Supplier<T> creater) {
        this.typeBuilder = new EdgeExtraPayloadType.Builder<>(creater, reader);
        return this;
    }

    @Override
    public EdgeDataReg<T> submit(SimpleRegistry registry) {
        if (typeBuilder == null) {
            throw new IllegalStateException("EdgeDataReg must be loaded before submission");
        }
        this.type = typeBuilder.build();
        EdgeExtraPayloadRegistry.register(registry.asResource(registrationKey), type);
        return this;
    }

    public EdgeExtraPayloadType<T> getType() {
        return type;
    }

    @Override
    public String getIdentifier() {
        return "edge_data";
    }
}
