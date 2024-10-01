package kasuga.lib.core.addons.resource;

import java.io.IOException;
import java.util.List;

public interface HierarchicalFilesystem extends ResourceProvider{
    public List<String> list(String path) throws IOException;
}
