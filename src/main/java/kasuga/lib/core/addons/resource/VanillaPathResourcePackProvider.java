package kasuga.lib.core.addons.resource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class VanillaPathResourcePackProvider implements ResourceProvider,HierarchicalFilesystem {
    private final Path source;

    public VanillaPathResourcePackProvider(Path source) {
        this.source = source;
    }

    @Override
    public InputStream open(String path) throws IOException {
        return Files.newInputStream(ResourceProvider.safeResolve(source, "script", ResourceProvider.firstSplash(path)));
    }

    @Override
    public boolean exists(String path) {
        return Files.exists(ResourceProvider.safeResolve(source, "script", ResourceProvider.firstSplash(path)));
    }

    public List<String> list(String path) throws IOException {
        try(var stream = Files.list(ResourceProvider.safeResolve(source, "script", ResourceProvider.firstSplash(path)))){
            return stream.map(p -> p.getFileName().toString()).toList();
        }
    }
}
