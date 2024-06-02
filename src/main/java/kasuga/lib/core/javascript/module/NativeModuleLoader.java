package kasuga.lib.core.javascript.module;

import kasuga.lib.core.javascript.JavascriptContext;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import java.util.Objects;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

public class NativeModuleLoader<T> implements ModuleLoader{

    private final String identifier;
    private final Function<JavascriptContext, T> supplier;

    private final WeakHashMap<JavascriptContext, T> cache = new WeakHashMap<>();

    NativeModuleLoader(String identifier, Function<JavascriptContext, T> supplier){
        this.identifier = identifier;
        this.supplier = supplier;
    }

    @Override
    public boolean isLoadable(String identifier) {
        return Objects.equals(identifier, this.identifier);
    }

    @Override
    public Optional<Value> load(Context context, String module, Value requireFn, JavascriptContext javascriptContext) {
        return Objects.equals(module, this.identifier)
                ? Optional.of(Value.asValue(cache.computeIfAbsent(javascriptContext, supplier)))
                : Optional.empty();
    }
}
