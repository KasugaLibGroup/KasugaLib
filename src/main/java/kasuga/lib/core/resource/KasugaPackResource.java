package kasuga.lib.core.resource;

import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.Resource;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Getter
public class KasugaPackResource implements PackResources {

    private final HashMap<ResourceLocation, Stack<Resource>> resources;
    private final File file;
    private final PackType packType;
    private final List<String> namespaces;
    private final String name;

    public KasugaPackResource(PackType type, String name, String... namespaces) {
        this.packType = type;
        this.resources = new HashMap<>();
        this.file = new File("dummy");
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
        if (!namespaces.contains(location.getNamespace())) {
            namespaces.add(location.getNamespace());
        }
        if (!resources.containsKey(location)) {
            Stack<Resource> stack = new Stack<>();
            stack.push(resource);
            resources.put(location, stack);
            return true;
        }
        Stack<Resource> stack = resources.get(location);
        stack.push(resource);
        return true;
    }

    @Override
    public @Nullable InputStream getRootResource(String pFileName) throws IOException {
        return new ByteArrayInputStream(new byte[0]);
    }

    @Override
    public InputStream getResource(PackType pType, ResourceLocation pLocation) throws IOException {
        if (!pType.equals(this.packType)) {
            throw new IllegalArgumentException("Pack type " + pType + " is not supported");
        }
        Stack<Resource> resource = resources.getOrDefault(pLocation, null);
        if (resource == null || resource.isEmpty()) {
            throw new IllegalArgumentException("Resource " + pLocation + " not found");
        }
        return resource.peek().open();
    }

    @Override
    public Collection<ResourceLocation> getResources(PackType pType, String pNamespace, String pPath, Predicate<ResourceLocation> pFilter) {
        if (!pType.equals(this.packType)) return List.of();
        if (!namespaces.contains(pNamespace)) return List.of();
        Stream<ResourceLocation> locationStream = resources.keySet().stream();
        return locationStream.filter(location -> location.getNamespace().equals(pNamespace) &&
                location.getPath().startsWith(pPath) && pFilter.test(location)).toList();
    }

    @Override
    public boolean hasResource(PackType pType, ResourceLocation pLocation) {
        if (!pType.equals(this.packType)) return false;
        return resources.containsKey(pLocation);
    }

    @Override
    public Set<String> getNamespaces(PackType pType) {
        if (!pType.equals(this.packType)) return Collections.emptySet();
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
