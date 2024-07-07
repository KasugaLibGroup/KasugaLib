package kasuga.lib.core.javascript.module.node;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.addons.node.AssetReader;
import kasuga.lib.core.addons.node.NodePackage;
import kasuga.lib.core.javascript.Asset;
import kasuga.lib.core.javascript.JavascriptContext;
import kasuga.lib.core.javascript.module.JavascriptModule;
import net.minecraftforge.common.util.Lazy;
import org.graalvm.polyglot.Value;

import java.util.HashMap;
import java.util.UUID;

public class JavascriptNodeModule extends JavascriptModule {
    protected String dirname;
    protected String path;
    protected NodePackage nodePackage;
    protected Value sourceFn;
    protected Lazy<Value> module = Lazy.concurrentOf(()->{
        Value module = getContext().eval("({exports:{}})");
        if(KasugaLib.STACKS.JAVASCRIPT.ASSETS.isPresent()){
            module.putMember("asset", new AssetReader(dirname, getContext(), nodePackage.reader, KasugaLib.STACKS.JAVASCRIPT.ASSETS.get()));
        }
        sourceFn.execute(
                module.getMember("exports"),
                Value.asValue(getContext().getRequireFunction(this)),
                module,
                dirname,
                path
        );

        return module.getMember("exports");
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
        this.nodePackage = nodePackage;
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
