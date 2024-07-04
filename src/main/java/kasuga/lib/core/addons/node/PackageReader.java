package kasuga.lib.core.addons.node;

import kasuga.lib.core.addons.resource.FlatFilesystem;
import kasuga.lib.core.addons.resource.HierarchicalFilesystem;
import kasuga.lib.core.addons.resource.ResourceProvider;

import java.io.IOException;
import java.io.InputStream;

public class PackageReader implements ResourceProvider {
    public String path;
    public ResourceProvider provider;

    public PackageReader(String path, ResourceProvider provider) {
        this.path = path;
        this.provider = provider;
    }

    @Override
    public InputStream open(String file) throws IOException {
        return provider.open(path + "/" + ResourceProvider.firstSplash(file));
    }

    @Override
    public boolean exists(String file) {
        return provider.exists(path + "/" + ResourceProvider.firstSplash(file));
    }

    public boolean isHierarchical(){
        return provider instanceof HierarchicalFilesystem;
    }

    public boolean isFlat(){
        return provider instanceof FlatFilesystem;
    }

    public HierarchicalFilesystem asRawHierarchical(){
        return (HierarchicalFilesystem) provider;
    }

    public FlatFilesystem asRawFlat(){
        return (FlatFilesystem) provider;
    }

    public String getPath() {
        return path;
    }
}
