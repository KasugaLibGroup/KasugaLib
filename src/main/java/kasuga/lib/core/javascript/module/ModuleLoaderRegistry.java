package kasuga.lib.core.javascript.module;

import kasuga.lib.core.javascript.JavascriptContext;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import java.util.ArrayList;
import java.util.Optional;

public class ModuleLoaderRegistry implements ModuleLoader {
    ArrayList<ModuleLoader> loaders = new ArrayList<>();

    public void register(ModuleLoader loader){
        loaders.add(loader);
    }

    public void unregister(ModuleLoader loader){
        loaders.remove(loader);
    }

    @Override
    public boolean isLoadable(String identifier) {
        return true;
    }

    public Optional<Value> load(Context context, String module, Value requireFn, JavascriptContext javascriptContext){
        try{
            for (ModuleLoader loader : loaders) {
                if(loader.isLoadable(module)){
                    Optional<Value> loadResult = loader.load(context, module, requireFn, javascriptContext);
                    if(loadResult.isPresent())
                        return loadResult;
                }
            }
        }catch (ModuleLoadException e){
            throw e;
        }catch (RuntimeException e){
            throw new ModuleLoadException(module, e.getMessage(), e);
        }
        return Optional.empty();
    }
}
