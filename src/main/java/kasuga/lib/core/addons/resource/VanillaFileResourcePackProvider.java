package kasuga.lib.core.addons.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;
import java.util.zip.ZipFile;

public class VanillaFileResourcePackProvider implements ResourceProvider, FlatFilesystem {
    private final ZipFile zipFile;

    public VanillaFileResourcePackProvider(ZipFile zipFile) {
        this.zipFile = zipFile;
    }

    @Override
    public InputStream open(String path) throws IOException {
        return zipFile.getInputStream(zipFile.getEntry("script/" + ResourceProvider.firstSplash(path)));
    }

    @Override
    public boolean exists(String path) {
        return zipFile.getEntry("script/" + ResourceProvider.firstSplash(path)) != null;
    }

    @Override
    public Stream<String> listEntries() {
        return zipFile.stream()
                .filter(entry -> entry.getName().startsWith("script/"))
                .map(entry -> entry.getName().substring("script/".length()));
    }

    @Override
    public boolean isRegularFile(String path) {
        return !zipFile.getEntry("script/" + ResourceProvider.firstSplash(path)).isDirectory();
    }

    @Override
    public boolean isDirectory(String path) {
        return zipFile.getEntry("script/" + ResourceProvider.firstSplash(path)).isDirectory();
    }
}
