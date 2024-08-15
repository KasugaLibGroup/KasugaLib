package kasuga.lib.core.client.frontend.commands;

import com.oracle.truffle.api.exception.AbstractTruffleException;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.addons.node.PackageScanner;
import kasuga.lib.core.javascript.JavascriptContext;
import kasuga.lib.core.javascript.JavascriptThread;
import kasuga.lib.core.javascript.module.JavascriptModule;
import kasuga.lib.core.javascript.module.ModuleLoader;
import kasuga.lib.core.javascript.module.node.CommonJSUtils;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Optional;

public class MetroModuleLoader implements ModuleLoader {
    @Override
    public Optional<JavascriptModule> load(JavascriptModule source, String name) {
        if(source instanceof MetroLoaderModule loaderModule){
            return this.loaderLoad(loaderModule, name);
        }
        if(!(source instanceof MetroServerModule metroModule)) {
            return Optional.empty();
        }
        String path;
        if(name.startsWith("/")){
            path = name;
        }else{
            path = PackageScanner.joinPath(
                    PackageScanner.resolve(
                            PackageScanner.splitPath(metroModule.relativePath),
                            PackageScanner.splitPath(name)
                    )
            );
        }

        return this.load(
                path,
                metroModule.getServerAddress(),
                metroModule.getProvider(),
                metroModule.getContext()
        );

    }

    private Optional<JavascriptModule> loaderLoad(MetroLoaderModule loaderModule, String name) {
        return load(
                name,
                loaderModule.getServerAddress(),
                loaderModule.getProvider(),
                loaderModule.getContext()
        );
    }

    protected Optional<JavascriptModule> load(
            String path,
            String serverAddress,
            MetroServerResourceProvider provider,
            JavascriptContext context
    ){
        if(path.endsWith(".bundle")){
            path += "?platform=minecraft";
        }
        try(InputStream stream = provider.open(path)){
            InputStreamReader reader = new InputStreamReader(stream);
            Reader parsedResource = CommonJSUtils.transform(reader);
            Source source = Source.newBuilder("js",parsedResource,path).build();
            List<String> parsedPath = PackageScanner.splitPath(path);
            Value sourceValue = context.execute(source);
            MetroServerModule module = new MetroServerModule(
                    context,
                    sourceValue,
                    serverAddress,
                    PackageScanner.joinPath(parsedPath.subList(1,parsedPath.size() - 1)),
                    provider
            );
            return Optional.of(module);
        }catch (IOException | AbstractTruffleException e){
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public static void init(){
        KasugaLib
                .STACKS
                .JAVASCRIPT
                .GROUP_CLIENT
                .getModuleLoader()
                .getLoader()
                .register(new MetroModuleLoader());
    }

    public static JavascriptThread getThread(){
        return KasugaLib
                .STACKS
                .JAVASCRIPT
                .GROUP_CLIENT
                .getOrCreate(MetroModuleLoader.class,"Metro Server Thread");
    }
}
