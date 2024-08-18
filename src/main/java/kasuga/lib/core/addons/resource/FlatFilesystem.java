package kasuga.lib.core.addons.resource;

import java.util.stream.Stream;

public interface FlatFilesystem extends ResourceProvider{
    public Stream<String> listEntries();
}
