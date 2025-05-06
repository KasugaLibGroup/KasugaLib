package kasuga.lib.registrations.create;

import com.tterrag.registrate.util.nullness.NonNullFunction;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraftforge.client.model.BakedModelWrapper;

import java.util.function.Function;

public class TrackModelDeduplicator {
    public static <T extends BakedModel> NonNullFunction<T, BakedModelWrapper<T>> simple(Function<T, BakedModelWrapper<T>> wrapped){
        return (i)-> i instanceof BakedModelWrapper<?> ?
                (BakedModelWrapper<T>) i :
                wrapped.apply(i);

    }
}
