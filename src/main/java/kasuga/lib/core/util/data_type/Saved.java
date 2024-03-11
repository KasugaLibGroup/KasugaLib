package kasuga.lib.core.util.data_type;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.Optional;
import java.util.function.Supplier;

public class Saved<T extends SavedData> {
    public final String resourceKey;
    @Nonnull
    Supplier<T> dataSupplier;
    @Nonnull
    LoadFunction<T> loadFunction;
    T data;

    public Saved(String resourceKey, @NotNull Supplier<T> data, @NotNull LoadFunction<T> loadFunction) {
        this.resourceKey = resourceKey;
        this.dataSupplier = data;
        this.loadFunction = loadFunction;
    }

    public T loadFromDisk(ServerLevel level) {
        data = level.getDataStorage().computeIfAbsent(this::load, dataSupplier, resourceKey);
        return data;
    }

    public T load(CompoundTag nbt) {
        return loadFunction.load(nbt);
    }

    public Optional<T> getData() {
        return Optional.of(data);
    }

    public void saveToDisk(ServerLevel level) {
        if(data == null) return;
        data.setDirty(true);
        level.getDataStorage().set(resourceKey, data);
    }

    public void save(CompoundTag tag) {
        data.save(tag);
    }

    public void saveToFile(File file) {
        data.save(file);
    }

    public interface LoadFunction<T extends SavedData> {
        public T load(CompoundTag tag);
    }
}
