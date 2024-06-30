package kasuga.lib.core.addons.resource.adapter;

import java.io.IOException;
import java.io.InputStream;

public interface RawResourceAdapter {
    InputStream getResource(PackType packType, String path) throws IOException;
    boolean hasResource(PackType packType, String path) throws IOException;
}
