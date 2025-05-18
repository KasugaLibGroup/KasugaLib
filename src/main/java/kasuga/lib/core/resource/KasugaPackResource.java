package kasuga.lib.core.resource;

import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraftforge.resource.PathPackResources;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Getter
public class KasugaPackResource extends PathPackResources {

    private final HashMap<ResourceLocation, Stack<Resource>> resources;
    private final File file;
    private final List<String> namespaces;
    private final String name;

    public KasugaPackResource(String name, String... namespaces) {
        super(name, Path.of("dummy"));
        this.resources = new HashMap<>();
        this.file = getFile();
        this.namespaces = new ArrayList<>();
        this.namespaces.addAll(Arrays.asList(namespaces));
        this.name = name;
    }

    public boolean registerResource(ResourceLocation location, byte[] data) {
        Resource resource = new Resource(location.getNamespace(), () -> new ByteArrayInputStream(data));
        return registerResource(location, resource);
    }

    public boolean registerResource(ResourceLocation location, InputStream stream) throws IOException {
        byte[] data = stream.readAllBytes();
        stream.close();
        return registerResource(location, data);
    }

    public boolean registerResource(ResourceLocation location, File file) throws IOException {
        FileInputStream stream = new FileInputStream(file);
        return registerResource(location, stream);
    }

    public boolean registerResource(ResourceLocation location, Resource resource) {
        if (!location.getNamespace().equals(resource.sourcePackId())) return false;
        synchronized (namespaces) {
            if (!namespaces.contains(location.getNamespace())) {
                namespaces.add(location.getNamespace());
            }
        }
        synchronized (resources) {
            if (!resources.containsKey(location)) {
                Stack<Resource> stack = new Stack<>();
                stack.push(resource);
                resources.put(location, stack);
                return true;
            }
            Stack<Resource> stack = resources.get(location);
            synchronized (stack) {
                stack.push(resource);
            }
            return true;
        }
    }

    @Override
    public @Nullable InputStream getRootResource(String pFileName) throws IOException {
        return new ByteArrayInputStream(new byte[0]);
    }

    @Override
    public InputStream getResource(PackType pType, ResourceLocation pLocation) throws IOException {
        Stack<Resource> resource = resources.getOrDefault(pLocation, null);
        if (resource == null || resource.isEmpty()) {
            throw new IllegalArgumentException("Resource " + pLocation + " not found");
        }
        return resource.peek().open();
    }

    @Override
    public Collection<ResourceLocation> getResources(PackType pType, String pNamespace, String pPath, Predicate<ResourceLocation> pFilter) {
        if (!namespaces.contains(pNamespace)) return List.of();
        Stream<ResourceLocation> locationStream = resources.keySet().stream();
        return locationStream.filter(location -> location.getNamespace().equals(pNamespace) &&
                location.getPath().startsWith(pPath) && pFilter.test(location)).toList();
    }

    @Override
    public boolean hasResource(PackType pType, ResourceLocation pLocation) {
        return resources.containsKey(pLocation);
    }

    @Override
    public Set<String> getNamespaces(PackType pType) {
        return new HashSet<>(this.namespaces);
    }

    @Override
    public @Nullable <T> T getMetadataSection(MetadataSectionSerializer<T> pDeserializer) throws IOException {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void close() {

    }
}
