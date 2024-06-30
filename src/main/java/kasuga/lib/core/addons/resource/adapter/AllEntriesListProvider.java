package kasuga.lib.core.addons.resource.adapter;

import kasuga.lib.core.addons.resource.types.PackResources;

import java.util.List;
import java.util.stream.Stream;

public interface AllEntriesListProvider extends RawResourceAdapter {
    public List<String> getAllEntries();
    public Stream<String> getAllEntriesStream();
}
