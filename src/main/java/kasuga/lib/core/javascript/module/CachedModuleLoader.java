package kasuga.lib.core.javascript.module;

import kasuga.lib.core.javascript.module.node.JavascriptNodeModule;

import java.util.HashMap;
import java.util.Optional;

public abstract class CachedModuleLoader implements ModuleLoader{
    protected final HashMap<String, JavascriptModule> cache = new HashMap<>();
    @Override
    public Optional<JavascriptModule> load(JavascriptModule source, String name) {
        if(cache.containsKey(name)){
            return Optional.of(cache.get(name));
        }
        Optional<JavascriptModule> module = getModule(source, name);
        module.ifPresent((m)->{
            synchronized (cache){
                JavascriptModule actualModule = cache.computeIfAbsent(name, (k)->m);
                source.getContext().collectEffect(()->{
                    cache.remove(name, actualModule);
                });
            }
        });
        return Optional.ofNullable(cache.get(name));
    }

    public abstract Optional<JavascriptModule> getModule(JavascriptModule source, String name);
}
