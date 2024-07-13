package kasuga.lib.core.javascript.module;

import java.util.Optional;

public interface ModuleLoader {

    public Optional<JavascriptModule> load(
            JavascriptModule source,
            String name
    );
}
