package kasuga.lib.core.resource;

import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.IoSupplier;
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
        super(name, true, Path.of("dummy"));
        this.resources = new HashMap<>();
        this.file = getFile();
        this.namespaces = new ArrayList<>();
        this.namespaces.addAll(Arrays.asList(namespaces));
        this.name = name;
    }

    public boolean registerResource(ResourceLocation location, byte[] data) {
        Resource resource = new Resource(this, () -> new ByteArrayInputStream(data));
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
    public @Nullable IoSupplier<InputStream> getRootResource(String... paths) {
        return IoSupplier.create(Path.of("dummy"));
    }

    @Override
    public IoSupplier<InputStream> getResource(PackType pType, ResourceLocation pLocation) {
        Stack<Resource> resource = resources.getOrDefault(pLocation, null);
        if (resource == null || resource.isEmpty()) return null;
        return CustomIoSupplier.create(resource.peek());
    }

    public Resource getResource(ResourceLocation location) {
        Stack<Resource> resource = resources.getOrDefault(location, null);
        if (resource == null || resource.isEmpty()) return null;
        return resource.peek();
    }

    @Override
    public void listResources(PackType pType, String pNamespace, String pPath, ResourceOutput output) {
        if (!namespaces.contains(pNamespace)) return;
        Stream<ResourceLocation> locationStream = resources.keySet().stream();
        locationStream.filter(location -> location.getNamespace().equals(pNamespace) &&
                location.getPath().startsWith(pPath)).forEach(a -> {
            try {
                Resource r = getResource(a);
                if (r == null) return;
                output.accept(a, CustomIoSupplier.create(r));
            } catch (Exception ignored) {}
        });
    }

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

    public String getName() {
        return name;
    }

    @Override
    public void close() {

    }
}
