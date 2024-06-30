package kasuga.lib.core.addons.resource.adapter;

import kasuga.lib.core.addons.resource.types.PackResources;

import java.util.stream.Stream;

public interface QuickListProvider extends RawResourceAdapter {
    public Stream<String> list(PackType packType, String path);
}
