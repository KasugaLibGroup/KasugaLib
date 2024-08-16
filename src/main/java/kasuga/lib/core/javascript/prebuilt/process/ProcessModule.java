package kasuga.lib.core.javascript.prebuilt.process;
import kasuga.lib.core.javascript.JavascriptContext;
import kasuga.lib.core.javascript.prebuilt.PrebuiltModule;
import org.graalvm.polyglot.HostAccess;

public class ProcessModule extends PrebuiltModule {
    private final JavascriptContext runtime;

    public ProcessModule(JavascriptContext runtime) {
        super(runtime);
        this.runtime = runtime;
    }

    @HostAccess.Export
    public String[] listEnv(){
        return runtime.getEnvironment().keySet().toArray(new String[0]);
    }

    @HostAccess.Export
    public Object getEnv(String key){
        return runtime.getEnvironment().get(key);
    }
}
