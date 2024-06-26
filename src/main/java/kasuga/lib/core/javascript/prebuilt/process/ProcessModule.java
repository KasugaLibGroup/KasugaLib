package kasuga.lib.core.javascript.prebuilt.process;

import kasuga.lib.core.javascript.JavascriptContext;
import org.graalvm.polyglot.HostAccess;

public class ProcessModule {
    private final JavascriptContext javascriptContext;

    public ProcessModule(JavascriptContext javascriptContext) {
        this.javascriptContext = javascriptContext;
    }

    @HostAccess.Export
    public String[] listEnv(){
        return javascriptContext.getEnvironment().keySet().toArray(new String[0]);
    }

    @HostAccess.Export
    public Object getEnv(String key){
        return javascriptContext.getEnvironment().get(key);
    }
}
