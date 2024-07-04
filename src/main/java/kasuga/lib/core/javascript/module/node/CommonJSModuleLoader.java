package kasuga.lib.core.javascript.module.node;

import kasuga.lib.core.addons.node.NodePackage;
import kasuga.lib.core.addons.node.PackageReader;
import kasuga.lib.core.addons.node.PackageScanner;
import kasuga.lib.core.javascript.module.CachedModuleLoader;
import kasuga.lib.core.javascript.module.JavascriptModule;
import kasuga.lib.core.javascript.module.ModuleLoader;
import kasuga.lib.core.util.data_type.Pair;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class CommonJSModuleLoader extends CachedModuleLoader implements ModuleLoader {
    @Override
    public Optional<JavascriptModule> getModule(JavascriptModule source, String name) {
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
                        try{
                            String dirName = PackageScanner.joinPath(
                                    PackageScanner.splitPath(path)
                                            .subList(0, PackageScanner.splitPath(path).size() - 1)
                            );
                            InputStream stream = reader.open(path);
                            InputStreamReader streamReader = new InputStreamReader(stream);

                            Reader wrappedCommonJSModule = CommonJSUtils.transform(streamReader);

                            Source parsedSource
                                    = Source
                                    .newBuilder("js", wrappedCommonJSModule, path)
                                    .buildLiteral();

                            Value moduleWrapper = source
                                    .getContext()
                                    .execute(parsedSource);

                            return new JavascriptNodeModule(
                                    source.getContext(),
                                    dirName,
                                    path,
                                    packageTarget,
                                    moduleWrapper
                            );

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
            if(nodePackage != null){
                result.add(Pair.of(nodePackage,name));
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
