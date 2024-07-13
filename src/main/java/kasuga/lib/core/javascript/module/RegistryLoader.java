package kasuga.lib.core.javascript.module;


import kasuga.lib.core.util.SortedArrayList;
import kasuga.lib.core.util.data_type.Pair;
import kasuga.lib.core.util.data_type.PrioritizedItem;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class RegistryLoader implements ModuleLoader {

    public RegistryLoader(){
        this.parent = null;
    }
    public RegistryLoader(RegistryLoader parent){
        this.parent = parent;
    }

    Set<ModuleLoader> registeredLoaders = new HashSet<>();
    SortedArrayList<PrioritizedItem<ModuleLoader>> list = new SortedArrayList<>();

    RegistryLoader parent;

    public void register(ModuleLoader loader){
        register(loader, 0);
    }

    public void register(ModuleLoader loader, Integer priority){
        list.add(PrioritizedItem.of(loader, priority));
        registeredLoaders.add(loader);
    }

    @Override
    public Optional<JavascriptModule> load(JavascriptModule source, String name) {
        for (Pair<ModuleLoader, Integer> loader : list) {
            Optional<JavascriptModule> module = loader.getFirst().load(source, name);
            if(module.isPresent())
                return module;
        }
        if(parent != null)
            return parent.load(source, name);
        return Optional.empty();
    }
}
