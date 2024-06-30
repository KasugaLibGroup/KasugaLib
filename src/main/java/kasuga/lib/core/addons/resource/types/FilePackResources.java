package kasuga.lib.core.addons.resource.types;

import com.google.common.collect.Lists;
import kasuga.lib.core.addons.resource.adapter.AllEntriesListProvider;
import kasuga.lib.core.addons.resource.adapter.PackType;
import kasuga.lib.core.addons.resource.adapter.QuickListProvider;
import kasuga.lib.mixins.mixin.resources.FilePackResourceMixin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.ResourcePackFileNotFoundException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FilePackResources extends AbstractPackResources implements AllEntriesListProvider {
    private final ZipFile zipFile;

    public FilePackResources(File file, ZipFile zipFile) {
        super(new File("dummy"));
        this.zipFile = zipFile;
    }

    public static FilePackResources adapt(net.minecraft.server.packs.FilePackResources origin){
        return new FilePackResources(
                origin.file,
                ((FilePackResourceMixin)origin).invokeGetOrCreateZipFile()
        );
    }

    @Override
    protected boolean hasResource(String resource) {
        return zipFile.getEntry(resource) != null;
    }

    @Override
    protected InputStream getResource(String resource) throws IOException {
        ZipEntry entry = zipFile.getEntry(resource);
        if (entry == null) {
            throw new ResourcePackFileNotFoundException(this.file, resource);
        } else {
            return zipFile.getInputStream(entry);
        }
    }

    @Override
    public Collection<ResourceLocation> getResources(PackType packType, String namespace, String path, Predicate<ResourceLocation> pFilter) {
        Stream<? extends ZipEntry> stream = zipFile.stream();
        List<ResourceLocation> resources = Lists.newArrayList();

        return entryToResources(packType,namespace,path,pFilter,stream.filter(entry -> !entry.isDirectory())
                .map(ZipEntry::getName)
        ).collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Set<String> getNamespaces(PackType packType) {
        Stream<? extends ZipEntry> stream = zipFile.stream();

        return entryToNamespaces(
                packType,
                stream.filter(entry -> !entry.isDirectory())
                        .map(ZipEntry::getName)
        ).collect(Collectors.toSet());
    }

    @Override
    public Stream<String> getAllEntriesStream() {
        return zipFile.stream().map(ZipEntry::getName);
    }

    @Override
    public List<String> getAllEntries() {
        return getAllEntriesStream().collect(Collectors.toList());
    }

}
