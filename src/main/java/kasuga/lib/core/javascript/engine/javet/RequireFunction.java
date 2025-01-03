package kasuga.lib.core.javascript.engine.javet;

import com.caoccao.javet.values.V8Value;
import kasuga.lib.core.javascript.engine.annotations.HostAccess;

public interface RequireFunction {
    @HostAccess.Export
    public V8Value require(String moduleName);
}
