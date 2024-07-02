package kasuga.lib.core.addons.resource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public interface ResourceProvider {
    public static Path safeResolve(Path first, String ...paths){
        Path iter = first;
        for(String path : paths){
            while(path.startsWith("/"))
                path = path.substring(1);
            iter = iter.resolve(path);
        }
        if(!first.relativize(iter).startsWith(".."))
            return iter;
        else
            throw new IllegalArgumentException("Path is not safe(relative to the first path)");
    }
    public static String firstSplash(String path){
        while(path.startsWith("/"))
            path = path.substring(1);
        return path;
    }
    public InputStream open(String path) throws IOException;
    public boolean exists(String path);
}
