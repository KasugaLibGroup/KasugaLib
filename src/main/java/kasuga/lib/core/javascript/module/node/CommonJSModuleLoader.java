package kasuga.lib.core.javascript.module.node;

import kasuga.lib.core.addons.node.NodePackage;
import kasuga.lib.core.addons.node.PackageReader;
import kasuga.lib.core.addons.node.PackageScanner;
import kasuga.lib.core.javascript.JavascriptContext;
import kasuga.lib.core.javascript.module.CachedModuleLoader;
import kasuga.lib.core.javascript.module.JavascriptModule;
import kasuga.lib.core.javascript.module.ModuleLoader;
import kasuga.lib.core.util.data_type.Pair;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import javax.swing.text.html.Option;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class CommonJSModuleLoader implements ModuleLoader {

    WeakHashMap<JavascriptContext,HashMap<Pair<NodePackage, String>, JavascriptNodeModule>> moduleCache = new WeakHashMap<>();

    @Override
    public Optional<JavascriptModule> load(JavascriptModule source, String name) {
        if(name.contains(":"))
            return Optional.empty();
        List<Pair<NodePackage, String>> availableModuleNames = getModuleCandidate(source, name);
        if(availableModuleNames.isEmpty())
            return Optional.empty();
        Optional<JavascriptModule> moduleLike = Optional.empty();
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
                        if(moduleCache.containsKey(source.getContext()) && moduleCache.get(source.getContext()).containsKey(cacheKey)){
                            return moduleCache.get(source.getContext()).get(cacheKey);
                        }
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
                            InputStreamReader streamReader = new InputStreamReader(stream);

                            Reader wrappedCommonJSModule = CommonJSUtils.transform(streamReader);

                            Source parsedSource
                                    = Source
                                    .newBuilder("js", wrappedCommonJSModule, packageTarget.packageName + "/" + path)
                                    .buildLiteral();

                            Value moduleWrapper = source
                                    .getContext()
                                    .execute(parsedSource);

                            JavascriptNodeModule module = new JavascriptNodeModule(
                                    source.getContext(),
                                    dirName,
                                    fileName,
                                    packageTarget,
                                    moduleWrapper
                            );
                            moduleCache.computeIfAbsent(
                                    source.getContext(),
                                    (key)->new HashMap<>()
                            ).put(cacheKey, module);
                            return module;
                        }catch (IOException e){
                            e.printStackTrace();
                            return null;
                        }
                    }
            );
            if(moduleLike.isPresent())
                break;
        }

        return moduleLike;
    }

    public List<Pair<NodePackage, String>> getModuleCandidate(JavascriptModule source, String name){
        List<Pair<NodePackage, String>> result = new ArrayList<>();

        if(source instanceof JavascriptNodeModule nodeModule){
            NodePackage nodePackage = nodeModule.getPackage();
            List<String> relative = PackageScanner.resolve(
                    PackageScanner.splitPath(nodeModule.dirname),
                    PackageScanner.splitPath(name)
            );
            if(nodePackage != null){
                result.add(Pair.of(nodePackage, PackageScanner.joinPath(relative)));
            }
        }

        List<Pair<NodePackage, String>> maybeModules =
                source.getContext().getScope().getPackage(name);

        result.addAll(maybeModules);

        return result;
    }

    public static Optional<JavascriptModule> first(
            List<String> maybeFiles,
            Predicate<String> predicate,
            Function<String, JavascriptNodeModule> reader
    ){
        for (String maybeFile : maybeFiles) {
            if(!predicate.test(maybeFile))
                continue;

            JavascriptNodeModule result = reader.apply(maybeFile);
            if(result != null){
                return Optional.of(result);
            }
        }
        return Optional.empty();
    }
}
