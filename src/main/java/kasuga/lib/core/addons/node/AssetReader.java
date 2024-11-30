package kasuga.lib.core.addons.node;

import kasuga.lib.core.addons.resource.ResourceProvider;
import kasuga.lib.core.javascript.JavascriptContext;
import kasuga.lib.core.javascript.engine.HostAccess;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.BiFunction;


public class AssetReader implements BiFunction<String, String, String> {

    public static HashMap<String, BiFunction<InputStream,UUID,Object>> assetReaders = new HashMap<>();
    private final HashMap<UUID, Object> assets;
    private final JavascriptContext context;
    private final String dirname;
    private final String assetRoot;

    ResourceProvider provider;

    public AssetReader(String dirname, JavascriptContext context, ResourceProvider provider, HashMap<UUID, Object> assets, String assetRoot) {
        this.provider = provider;
        this.context = context;
        this.assets = assets;
        this.dirname = dirname;
        this.assetRoot = assetRoot == null ? "/" : assetRoot;
    }

    @HostAccess.Export
    public String apply(String path, String resourceType){
        if(!assetReaders.containsKey(resourceType)){
            return null;
        }
        BiFunction<InputStream,UUID,Object> reader = assetReaders.get(resourceType);
        if(!path.startsWith("/")){
            path = PackageScanner.joinPath(
                    PackageScanner.resolve(
                            PackageScanner.splitPath(
                                    dirname
                            ),
                            PackageScanner.splitPath(
                                    path
                            )
                    )
            );
        }
        path = PackageScanner.joinPath(
                PackageScanner.resolve(
                        PackageScanner.splitPath(
                                assetRoot
                        ),
                        PackageScanner.splitPath(
                                path
                        )
                )
        );
        UUID uuid;
        do{
            uuid = UUID.randomUUID();
        }while (assets.containsKey(uuid));

        UUID finalUuid = uuid;

        try(InputStream stream = provider.open(path)){
            assets.put(finalUuid, reader.apply(stream, finalUuid));
            context.collectEffect(()-> assets.remove(finalUuid));
        } catch (IOException e) {
            return null;
        }

        return finalUuid.toString();
    }
}
