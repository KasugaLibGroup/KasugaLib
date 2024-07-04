package kasuga.lib.core.javascript.module.node;

import kasuga.lib.core.addons.node.NodePackage;
import kasuga.lib.core.javascript.JavascriptContext;
import kasuga.lib.core.javascript.module.JavascriptModule;
import net.minecraftforge.common.util.Lazy;
import org.graalvm.polyglot.Value;

import java.io.Reader;

public class JavascriptNodeModule extends JavascriptModule {
    protected String dirname;
    protected String path;
    protected NodePackage nodePackage;
    protected Value sourceFn;
    protected Lazy<Value> module = Lazy.concurrentOf(()->{
        Value module = getContext().eval("({exports:{}})");
        return sourceFn.execute(
                module.getMember("exports"),
                Value.asValue(getContext().getRequireFunction(this)),
                module,
                dirname,
                path
        );
    });

    public JavascriptNodeModule(
            JavascriptContext context,
            String dirname,
            String path,
            NodePackage nodePackage,
            Value wrappedCommonJSModule
    ) {
        super(context);
        this.dirname = dirname;
        this.path = path;
        this.sourceFn = wrappedCommonJSModule;
    }

    public String getDirname(){
        return dirname;
    }

    public NodePackage getPackage(){
        return nodePackage;
    }

    public Value get(){
        return module.get();
    }
}
