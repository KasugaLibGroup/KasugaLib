package kasuga.lib.core.javascript;

import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;

import java.util.function.Function;

@FunctionalInterface
public interface RequireFunction extends Function<String,Value> {
    @HostAccess.Export
    public Value apply(String moduleName);
}
