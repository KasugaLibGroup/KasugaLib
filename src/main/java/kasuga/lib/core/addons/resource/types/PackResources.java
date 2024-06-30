package kasuga.lib.core.addons.resource.types;

import kasuga.lib.core.addons.resource.adapter.PackType;
import kasuga.lib.core.addons.resource.adapter.RawResourceAdapter;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;

public interface PackResources extends RawResourceAdapter {
    InputStream getResource(PackType packType, ResourceLocation path) throws IOException;

    Collection<ResourceLocation> getResources(PackType packType, String namespace, String path, Predicate<ResourceLocation> predicate);

    boolean hasResource(PackType packType, ResourceLocation path);

    Set<String> getNamespaces(PackType packType);

    String getName();
}
