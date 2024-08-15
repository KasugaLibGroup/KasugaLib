package kasuga.lib.core.javascript.module;

import kasuga.lib.core.javascript.JavascriptContext;
import kasuga.lib.core.util.data_type.Pair;

import java.util.HashMap;
import java.util.Optional;

public abstract class CachedModuleLoader implements ModuleLoader{
    protected final HashMap<Pair<JavascriptContext,String>, JavascriptModule> cache = new HashMap<>();
    @Override
    public Optional<JavascriptModule> load(JavascriptModule source, String name) {
        Pair<JavascriptContext,String> cacheKey = Pair.of(source.getContext(), name);
        if(cache.containsKey(cacheKey)){
            return Optional.of(cache.get(cacheKey));
        }
        Optional<JavascriptModule> module = getModule(source, name);
        module.ifPresent((m)->{
            synchronized (cache){
                JavascriptModule actualModule = cache.computeIfAbsent(cacheKey, (k)->m);
                source.getContext().collectEffect(()->{
                    cache.remove(cacheKey, actualModule);
                });
            }
        });
        return Optional.ofNullable(cache.get(cacheKey));
    }

    public abstract Optional<JavascriptModule> getModule(JavascriptModule source, String name);
}
