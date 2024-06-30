package kasuga.lib.core.addons.resource.types;

import com.google.common.collect.Lists;
import kasuga.lib.core.addons.resource.adapter.PackType;
import kasuga.lib.core.addons.resource.adapter.QuickListProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.ResourcePackFileNotFoundException;
import org.apache.commons.io.filefilter.DirectoryFileFilter;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.minecraft.server.packs.FolderPackResources.validatePath;

public class FolderPackResources extends AbstractPackResources implements QuickListProvider {
    public FolderPackResources(File file){
        super(file);
    }

    public static FolderPackResources adapt(net.minecraft.server.packs.FolderPackResources origin){
        return new FolderPackResources(origin.file);
    }

    public Set<String> getNamespaces(PackType pType) {
        File directory = new File(this.file, pType.getDirectory());
        File[] files = directory.listFiles((FileFilter) DirectoryFileFilter.DIRECTORY);
        if(files == null){
            return Collections.emptySet();
        }
        Stream<File> subDirectory = Arrays.stream(files);
        return subDirectory
                .map((file)->getRelativePath(directory, file))
                .filter((path)->path.equals(path.toLowerCase(Locale.ROOT)))
                .map((path)->path.substring(0, path.length() - 1))
                .collect(Collectors.toSet());
    }

    @Nullable
    private File getFile(String pFilename) {
        try {
            File $$1 = new File(this.file, pFilename);
            if ($$1.isFile() && validatePath($$1, pFilename)) {
                return $$1;
            }
        } catch (IOException var3) {
        }

        return null;
    }

    protected InputStream getResource(String pResourcePath) throws IOException {
        File $$1 = this.getFile(pResourcePath);
        if ($$1 == null) {
            throw new ResourcePackFileNotFoundException(this.file, pResourcePath);
        } else {
            return new FileInputStream($$1);
        }
    }

    protected boolean hasResource(String pResourcePath) {
        return this.getFile(pResourcePath) != null;
    }


    public Collection<ResourceLocation> getResources(PackType pType, String pNamespace, String pPath, Predicate<ResourceLocation> pFilter) {
        File subFiles = new File(this.file, pType.getDirectory());
        List<ResourceLocation> directory = Lists.newArrayList();
        this.listResources(new File(new File(subFiles, pNamespace), pPath), pNamespace, directory, pPath + "/", pFilter);
        return directory;
    }

    private void listResources(File directory, String namespace, List<ResourceLocation> resourceLocations, String path, Predicate<ResourceLocation> filter) {
        File[] files = directory.listFiles();
        if(files == null){
            return;
        }

        Stream<File> subDirectory = Arrays.stream(files);
        subDirectory
                .filter((file)->{
                    if(!file.isDirectory())
                        return true;
                    listResources(file, namespace, resourceLocations, path + file.getName() + "/", filter);
                    return false;
                })
                .map(File::getName)
                .filter((name)->!name.endsWith(".mcmeta"))
                .map((name)->ResourceLocation.tryBuild(namespace, path + name))
                .filter(Objects::nonNull)
                .filter(filter)
                .forEach(resourceLocations::add);
    }


    @Override
    public Stream<String> list(PackType packType, String path) {
        File directory = new File(this.file, packType.getDirectory());
        File[] files = directory.listFiles();

        if(files == null){
            return Stream.empty();
        }

        Stream<File> subDirectory = Arrays.stream(files);
        return subDirectory
                .map(File::getName);
    }
}
