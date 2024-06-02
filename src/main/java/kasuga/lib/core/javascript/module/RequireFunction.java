package kasuga.lib.core.javascript.module;

import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;

@FunctionalInterface
public interface RequireFunction {
    @HostAccess.Export
    default Value require(Value module){
        if(!module.isString())
            throw new IllegalArgumentException("Invalid argument: expected string moduleId");
        return require(module.asString());
    }

    Value require(String moduleId);
}
