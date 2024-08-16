package kasuga.lib.core.client.frontend.rendering;

import kasuga.lib.core.util.data_type.Pair;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.function.Function;

public class ImageProviders {
    public static HashMap<ResourceLocation, Function<String, ImageProvider>> providers = new HashMap<>();

    public static ImageProvider get(Pair<ResourceLocation, String> location) {
        Function<String, ImageProvider> provider = providers.get(location.getFirst());
        return provider == null ? null : provider.apply(location.getSecond());
    }

    public static void init(){
        providers.put(new ResourceLocation("kasuga_lib","resource"), (name)->new ResourceImageProvider(new ResourceLocation(name)));
        providers.put(new ResourceLocation("kasuga_lib","assets"), (name)->new AssetImageProvider(name));
    }
}
