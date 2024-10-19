package kasuga.lib.core.javascript.module;

import kasuga.lib.core.addons.node.NodePackage;
import kasuga.lib.core.addons.node.PackageReader;
import kasuga.lib.core.addons.node.PackageScanner;
import kasuga.lib.core.javascript.engine.JavascriptEngineContext;
import kasuga.lib.core.javascript.engine.JavascriptEngineModule;
import kasuga.lib.core.javascript.engine.JavascriptModuleLoader;
import kasuga.lib.core.util.data_type.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class NodeModuleResolver implements JavascriptModuleLoader {
    @Override
    public JavascriptEngineModule load(JavascriptEngineContext engineContext, String name, JavascriptEngineModule source) {
        if(name.contains(":"))
            return null;
        List<Pair<NodePackage, String>> availableModuleNames = getModuleCandidate(engineContext, source, name);
        if(availableModuleNames.isEmpty())
            return null;
        JavascriptEngineModule moduleLike = null;
        for (Pair<NodePackage, String> moduleNameEntry : availableModuleNames) {
            NodePackage packageTarget = moduleNameEntry.getFirst();
            String realName = moduleNameEntry.getSecond();

            if(packageTarget == null){
                continue;
            }

            if(PackageScanner.splitPath(realName).isEmpty()){
                realName = packageTarget.main;
                if(realName == null){
                    continue;
                }
            }

            PackageReader reader = packageTarget.reader;

            moduleLike = first(
                    List.of(
                            realName,
                            realName + ".js",
                            realName + "/index.js"
                    ),
                    reader::exists,
                    (path)->{
                        Pair<NodePackage,String> cacheKey = Pair.of(packageTarget,path);

                        try{
                            if(!reader.isRegularFile(path) || reader.isDirectory(path))
                                return null;

                            List<String> splitedPath = PackageScanner.splitPath(path);
                            String dirName = PackageScanner.joinPath(
                                    splitedPath
                                            .subList(0, PackageScanner.splitPath(path).size() - 1)
                            );

                            String fileName = splitedPath.get(splitedPath.size() - 1);

                            InputStream stream = reader.open(path);

                            JavascriptEngineModule module =
                                    engineContext.compileModuleFromSource(packageTarget, fileName, dirName, stream);

                            return module;
                        }catch (IOException e){
                            e.printStackTrace();
                            return null;
                        }
                    }
            );
            if(moduleLike != null)
                break;
        }

        return moduleLike;
    }

    public List<Pair<NodePackage, String>> getModuleCandidate(JavascriptEngineContext engineContext, JavascriptEngineModule module, String name){
        List<Pair<NodePackage, String>> packageCandidates = new ArrayList<>();
        NodePackage nodePackage = module.getPackage();
        if(nodePackage != null){
            List<String> relative = PackageScanner.resolve(
                    PackageScanner.splitPath(module.getDirectoryName()),
                    PackageScanner.splitPath(name)
            );
            packageCandidates.add(Pair.of(nodePackage, PackageScanner.joinPath(relative)));
        }

        List<Pair<NodePackage, String>> maybeModules =
                engineContext.getModuleScope()
                        .getPackage(name);

        packageCandidates.addAll(maybeModules);

        return packageCandidates;
    }

    public static JavascriptEngineModule first(
            List<String> maybeFiles,
            Predicate<String> predicate,
            Function<String, JavascriptEngineModule> reader
    ){
        for (String maybeFile : maybeFiles) {
            if(!predicate.test(maybeFile))
                continue;

            JavascriptEngineModule result = reader.apply(maybeFile);
            if(result != null){
                return result;
            }
        }
        return null;
    }
}
