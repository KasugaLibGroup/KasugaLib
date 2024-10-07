package kasuga.lib.core.addons.resource;

import kasuga.lib.mixins.mixin.resources.PathResourcePackMixin;
import net.minecraftforge.resource.PathResourcePack;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class VanillaPathResourcePackProvider implements ResourceProvider,HierarchicalFilesystem {
    private final Path source;
    private final PathResourcePack pack;

    public VanillaPathResourcePackProvider(Path source, PathResourcePack pack) {
        this.source = source;
        this.pack = pack;
    }

    @Override
    public InputStream open(String path) throws IOException {
        Path filePath = resolve(path);
        System.out.printf("Opening file: %s\n",filePath.toString());
        return Files.newInputStream(filePath);
    }

    @Override
    public boolean exists(String path) {
        Path filePath = resolve(path);
        System.out.printf("Testing file: %s\n",filePath.toString());
        return Files.exists(filePath);
    }

    public List<String> list(String path) throws IOException {
        try(var stream = Files.list(resolve(path))){
            return stream.map(p -> p.getFileName().toString()).toList();
        }
    }

    @Override
    public boolean isRegularFile(String path) {
        return Files.isRegularFile(resolve(path));
    }

    @Override
    public boolean isDirectory(String path) {
        return Files.isDirectory(resolve(path));
    }

    public Path resolve(String path){
        if(path.startsWith("/"))
            path = path.substring(1);
        path = "script/" + path;
        return ((PathResourcePackMixin) pack).invokeResolve(path);
    }
}


