package kasuga.lib.core.addons.resource.types;

import com.google.common.collect.Lists;
import kasuga.lib.core.addons.resource.adapter.PackType;
import net.minecraft.resources.ResourceLocation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static net.minecraft.server.packs.FilePackResources.SPLITTER;

public abstract class AbstractPackResources implements PackResources {
    protected final File file;

    public AbstractPackResources(File pFile) {
        this.file = pFile;
    }

    private static String getPathFromLocation(PackType pType, ResourceLocation pLocation) {
        return String.format(
                Locale.ROOT,
                "%s/%s/%s",
                pType.getDirectory(),
                pLocation.getNamespace(),
                pLocation.getPath()
        );
    }

    protected static String getRelativePath(File pFile1, File pFile2) {
        return pFile1.toURI().relativize(pFile2.toURI()).getPath();
    }

    @Override
    public String getName() {
        return this.file.getName();
    }

    protected abstract InputStream getResource(String resource) throws IOException;
    protected abstract boolean hasResource(String resource);

    @Override
    public InputStream getResource(PackType packType, ResourceLocation path) throws IOException {
        return this.getResource(getPathFromLocation(packType, path));
    }

    @Override
    public boolean hasResource(PackType packType, ResourceLocation path) {
        return this.hasResource(getPathFromLocation(packType, path));
    }

    public static Stream<String> entryToNamespaces(PackType packType,Stream<String> originalStream){
        return originalStream.filter(entryName -> entryName.startsWith(packType.getDirectory() + "/"))
                .map(entryName -> Lists.newArrayList(SPLITTER.split(entryName)))
                .filter(splitEntries -> splitEntries.size() > 1)
                .map(splitEntries -> splitEntries.get(1))
                .filter(namespace -> namespace.equals(namespace.toLowerCase(Locale.ROOT)));
    }

    public static Stream<ResourceLocation> entryToResources(PackType packType,String namespace,String path,Predicate<ResourceLocation> pFilter,Stream<String> stream){
        String base = packType.getDirectory() + "/" + namespace + "/";
        String actualPath = base + path + "/";
        return stream
                .filter(entryName -> entryName.startsWith(actualPath))
                .filter(entryName -> !entryName.endsWith(".mcmeta"))
                .map(entryName -> entryName.substring(base.length()))
                .map(resourceLocationPath -> ResourceLocation.tryBuild(namespace, resourceLocationPath))
                .filter(Objects::nonNull)
                .filter(pFilter);
    }

    @Override
    public InputStream getResource(PackType packType, String path) throws IOException {
        return this.getResource(packType.getDirectory() + "/" + path);
    }

    @Override
    public boolean hasResource(PackType packType, String path) throws IOException {
        return this.hasResource(packType.getDirectory() + "/" + path);
    }
}
