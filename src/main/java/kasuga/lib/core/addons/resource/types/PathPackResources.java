package kasuga.lib.core.addons.resource.types;

import com.google.common.base.Joiner;
import kasuga.lib.core.addons.resource.adapter.PackType;
import kasuga.lib.core.addons.resource.adapter.QuickListProvider;
import net.minecraft.resources.ResourceLocation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PathPackResources extends AbstractPackResources implements QuickListProvider {
    private final Path source;
    private final String packName;

    public PathPackResources(String packName, Path source) {
        super(new File("dummy"));
        this.source = source;
        this.packName = packName;
    }

    public static PathPackResources adapt(net.minecraftforge.resource.PathPackResources origin){
        return new PathPackResources(origin.getName(),origin.getSource());
    }

    protected Path resolve(String... paths) {
        Path path = this.source;
        for(int i = 0; i < paths.length; ++i) {
            String name = paths[i];
            if(name.startsWith("/"))
                name = name.substring(1);
            path = path.resolve(name);
        }

        return path;
    }

    protected boolean hasResource(String name) {
        Path path = this.resolve(name);
        return Files.exists(path, new LinkOption[0]);
    }



    @Override
    public String getName() {
        return packName;
    }

    protected InputStream getResource(String name) throws IOException {
        Path path = this.resolve(name);
        if (!Files.exists(path, new LinkOption[0])) {
            throw new FileNotFoundException("Can't find resource " + name + " at " + source);
        } else {
            return Files.newInputStream(path, StandardOpenOption.READ);
        }
    }

    public Collection<ResourceLocation> getResources(PackType type, String resourceNamespace, String pathIn, Predicate<ResourceLocation> filter) {
        try {
            Path root = this.resolve(type.getDirectory(), resourceNamespace).toAbsolutePath();
            Path inputPath = root.getFileSystem().getPath(pathIn);
            return Files.walk(root)
                    .map(root::relativize)
                    .filter(path -> !path.toString().endsWith(".mcmeta") && path.startsWith(inputPath))
                    .filter(path -> ResourceLocation.isValidPath(Joiner.on('/').join(path)))
                    .map(path -> new ResourceLocation(resourceNamespace, Joiner.on('/').join(path)))
                    .filter(filter)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    public Set<String> getNamespaces(PackType type) {
        try {
            Path root = this.resolve(type.getDirectory());
            return Files.walk(root, 1)
                    .filter(path -> Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS))
                    .map(root::relativize)
                    .filter(path -> path.getNameCount() > 0)
                    .map(path -> path.toString().replaceAll("/$", ""))
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            return Collections.emptySet();
        }
    }


    @Override
    public Stream<String> list(PackType packType, String path) {
        System.out.printf("Listing %s %s\n",path, this.resolve(packType.getDirectory(), path));
        try{
            Stream<Path> stream = Files.walk(this.resolve(packType.getDirectory(), path),1);
            return stream
                    .map((p)->p.getFileName().toString())
                    .filter((name)->!name.equals("."));

        }catch (IOException ioException){
            return Stream.empty();
        }
    }
}
