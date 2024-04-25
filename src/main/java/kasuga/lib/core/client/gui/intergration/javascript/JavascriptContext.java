package kasuga.lib.core.client.gui.intergration.javascript;

import kasuga.lib.core.client.gui.components.Node;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Value;

import java.util.HashSet;

public class JavascriptContext {
    public final String code;
    private final JavascriptGuiContainer container;
    private final JavascriptNativeApi nativeApi;

    private final Context graalContext;

    public JavascriptContext(String code) {
        this.code = code;
        this.container = new JavascriptGuiContainer();
        this.nativeApi = new JavascriptNativeApi(this);
        this.graalContext = Context.newBuilder().allowHostAccess(HostAccess.SCOPED).build();
        Value binding = this.graalContext.getBindings("js");
        binding.putMember("minecraft",this.nativeApi);
    }

    public JavascriptGuiContainer getContainer() {
        return container;
    }

    public void run() {
        try{
            this.graalContext.eval("js",this.code);
        }catch (PolyglotException e){
            System.out.println(e.toString());
        }
    }

    public void close(){
        this.container.close();
        this.graalContext.close();
    }
}
