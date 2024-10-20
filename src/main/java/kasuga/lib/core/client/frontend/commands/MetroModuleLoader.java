package kasuga.lib.core.client.frontend.commands;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.addons.node.AssetReader;
import kasuga.lib.core.addons.node.PackageScanner;
import kasuga.lib.core.javascript.JavascriptContext;
import kasuga.lib.core.javascript.JavascriptThread;
import kasuga.lib.core.javascript.engine.JavascriptEngineContext;
import kasuga.lib.core.javascript.engine.JavascriptEngineModule;
import kasuga.lib.core.javascript.engine.JavascriptModuleLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MetroModuleLoader implements JavascriptModuleLoader {
    protected static HashMap<UUID, MetroModuleInfo> sessions = new HashMap<>();
    public static String createSession(MetroModuleInfo moduleInfo) {
        UUID uuid = UUID.randomUUID();
        sessions.put(uuid, moduleInfo);
        return uuid.toString();
    }

    @Override
    public JavascriptEngineModule load(JavascriptEngineContext engineContext, String _name, JavascriptEngineModule source) {
        MetroModuleInfo moduleInfo;
        String name;
        if(!_name.startsWith("metro-session:")){
            if(!source.hasFeature("metro")){
                return null;
            }

            if(!(source.getFeature("metro") instanceof MetroModuleInfo _moduleInfo)){
                return null;
            }
            moduleInfo = _moduleInfo;
            name = _name;
        }else{
            UUID sessionId = UUID.fromString(_name.substring(
                    _name.indexOf(":"),
                    _name.indexOf("/") - 1
            ));
            if(sessions.containsKey(sessionId)){
                name = _name.substring(_name.indexOf("/"));
                moduleInfo = sessions.get(sessionId);

            }else return null;
        }

        if(name == "metro:assets"){
            JavascriptContext javascriptContext = engineContext.getContext();
            return engineContext.compileNativeModule(
                    new AssetReader(
                            source.getDirectoryName(),
                            javascriptContext,
                            moduleInfo.getProvider(),
                            KasugaLib.STACKS.JAVASCRIPT.ASSETS.get()
                    ),
                    null
            );
        }


        String path;
        if(name.startsWith("/")){
            path = name;
        }else{
            path = PackageScanner.joinPath(
                    PackageScanner.resolve(
                            PackageScanner.splitPath(source.getDirectoryName()),
                            PackageScanner.splitPath(name)
                    )
            );
        }

        return this.load(
                path,
                moduleInfo.getServerAddress(),
                moduleInfo.getProvider(),
                moduleInfo,
                engineContext
        );
    }

    protected JavascriptEngineModule load(
            String path,
            String serverAddress,
            MetroServerResourceProvider provider,
            MetroModuleInfo moduleInfo, JavascriptEngineContext context
    ){
        if(path.endsWith(".bundle")){
            path += "?platform=minecraft";
        }
        try(InputStream stream = provider.open(path)){
            String abstractPath = PackageScanner.joinPath(
                    PackageScanner.resolve(
                            PackageScanner.splitPath(serverAddress),
                            PackageScanner.splitPath(path)
                    )
            );

            List<String> _path = PackageScanner.splitPath(path);

            return context
                    .compileModuleFromSource(
                            null,
                            path,
                            PackageScanner.joinPath(_path.subList(1, _path.size() - 1)),
                            stream
                    ).setFeature("metro", moduleInfo);

        }catch (IOException e){
            e.printStackTrace();
            return null;
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
