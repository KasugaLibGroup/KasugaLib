package kasuga.lib.core.addons.packagemanager.structure;

import java.io.IOException;
import java.io.InputStream;

public interface ResourceLoaderFunction {
    public boolean has(String path);
    public InputStream get(String path) throws IOException;
}
