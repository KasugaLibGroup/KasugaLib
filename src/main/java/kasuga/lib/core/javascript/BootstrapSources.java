package kasuga.lib.core.javascript;

import kasuga.lib.core.util.Resources;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.Lazy;
import org.graalvm.polyglot.Source;

import java.io.BufferedReader;
import java.io.IOException;

public class BootstrapSources {

    public static Lazy<Source> BOOTSTRAP = Lazy.of(()->getSource("kasuga_lib:js/bootstrap.js"));

    public static Lazy<Source> WORKER = Lazy.of(()->getSource("kasuga_lib:js/worker_init.js"));

    public static Source getSource(String path) {
        try{
            BufferedReader reader = Resources.getResource(ResourceLocation.tryParse(path)).openAsReader();
            return Source.newBuilder("js",reader,path).build();
        }catch (IOException e){
            throw new IllegalStateException("Failed to get source of "+path);
        }
    }
}
