package kasuga.lib.core.javascript;

import kasuga.lib.core.javascript.module.JavascriptModule;
import kasuga.lib.core.javascript.module.ModuleLoadException;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;

import java.util.function.Function;

@FunctionalInterface
public interface RequireFunction extends Function<String,Value> {
    @HostAccess.Export
    public Value apply(String moduleName);
}
