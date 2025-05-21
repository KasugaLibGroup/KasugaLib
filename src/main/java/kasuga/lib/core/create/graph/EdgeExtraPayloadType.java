package kasuga.lib.core.create.graph;

import net.minecraft.nbt.CompoundTag;

import java.util.function.Function;
import java.util.function.Supplier;

public abstract class EdgeExtraPayloadType<T extends EdgeExtraPayload> {
    abstract T read(CompoundTag tag);
    abstract T create();

    public static class Builder<T extends EdgeExtraPayload> {
        private final Supplier<T> supplier;
        private final Function<CompoundTag, T> reader;

        public Builder(Supplier<T> supplier, Function<CompoundTag, T> reader) {
            this.supplier = supplier;
            this.reader = reader;
        }

        public EdgeExtraPayloadType<T> build() {
            return new EdgeExtraPayloadType<T>() {
                @Override
                T read(CompoundTag tag) {
                    return reader.apply(tag);
                }

                @Override
                T create() {
                    return supplier.get();
                }
            };
        }
    }
}
