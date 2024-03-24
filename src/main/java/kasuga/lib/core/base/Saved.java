package kasuga.lib.core.base;

import kasuga.lib.core.annos.Inner;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * This class is for SavedData. SavedData is a type of data that would be saved/load with the
 * game while the game paused, quit or load. The path of your file would be
 * ".minecraft/saves/save_name/data/resourceKey.dat".
 * All data should be serialized as nbt in order to be saved.
 * So you must prepare your SavedData class with valid serializer and deserializer method.
 * For more info, see {@link SavedData}
 * @param <T> the SavedData content Type
 */
public class Saved<T extends SavedData> {

    public final String resourceKey;
    @Nonnull
    Supplier<T> dataSupplier;
    @Nonnull
    LoadFunction<T> loadFunction;
    T data;

    /**
     * Use this to pack SavedData.
     * @param resourceKey The file name of your savedData.
     * @param data the data supplier, usually a constructor of your SavedData, we would use this to get your class instance.
     * @param loadFunction your constructor deserializer. it would deserialize nbt to your SavedData class.
     */
    public Saved(String resourceKey, @NotNull Supplier<T> data, @NotNull LoadFunction<T> loadFunction) {
        this.resourceKey = resourceKey;
        this.dataSupplier = data;
        this.loadFunction = loadFunction;
    }

    /**
     * This method could be used to load data from your disk. If there's no file in the folder. It will compute an empty
     * SavedData for you. Normally, we call this via
     * {@link net.minecraftforge.event.level.LevelEvent.Load} and
     * {@link net.minecraftforge.event.level.LevelEvent.Save}
     * @param level The ServerLevel we would use to load our file.
     * @return the loaded SavedData.
     */
    public T loadFromDisk(ServerLevel level) {
        data = level.getDataStorage().computeIfAbsent(this::load, dataSupplier, resourceKey);
        return data;
    }

    /**
     * This method could be used to save data to your disk. Tf there's already a file in the folder, we would overwrite it
     * with our new data. Normally, we call this via
     * {@link net.minecraftforge.event.level.LevelEvent.Save} and
     * {@link net.minecraftforge.event.level.LevelEvent.Unload}
     * @param level The ServerLevel we would use to save our file.
     */
    public void saveToDisk(ServerLevel level) {
        if(data == null) return;
        data.setDirty(true);
        level.getDataStorage().set(resourceKey, data);
    }

    @Inner
    public T load(CompoundTag nbt) {
        return loadFunction.load(nbt);
    }

    /**
     * the SavedData getter
     * @return the SavedData
     */
    public Optional<T> getData() {
        return Optional.of(data);
    }

    /**
     * This method is used to save additional data to the SavedData.
     * @param tag Data to be saved.
     */
    public void save(CompoundTag tag) {
        data.save(tag);
    }

    /**
     * save this SavedData to exact file.
     * @param file The data would be saved to this file.
     */
    public void saveToFile(File file) {
        data.save(file);
    }

    /**
     * The deserializer function interface.
     * @param <T> Your SavedData contents.
     */
    public interface LoadFunction<T extends SavedData> {
        T load(CompoundTag tag);
    }
}
