package kasuga.lib.core.client.frontend.commands;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.addons.node.AssetReader;
import kasuga.lib.core.javascript.JavascriptContext;
import kasuga.lib.core.javascript.module.JavascriptModule;
import net.minecraftforge.common.util.Lazy;
import org.graalvm.polyglot.Value;

public class MetroServerModule extends JavascriptModule {
    public MetroServerModule(
            JavascriptContext context,
            Value source,
            String serverAddress,
            String relativePath,
            MetroServerResourceProvider provider
    ) {
        super(context);
        this.sourceFn = source;
        this.serverAddress = serverAddress;
        this.relativePath = relativePath;
        this.provider = provider;
    }

    protected String serverAddress;

    protected String relativePath;
    protected String fileName;
    protected Value sourceFn;

    protected MetroServerResourceProvider provider;

    protected Lazy<Value> module = Lazy.concurrentOf(()->{
        Value module = getContext().eval("({exports:{}})");
        if(KasugaLib.STACKS.JAVASCRIPT.ASSETS.isPresent()){
            module.putMember("asset", new AssetReader(relativePath, getContext(), provider, KasugaLib.STACKS.JAVASCRIPT.ASSETS.get()));
        }
        sourceFn.execute(
                module.getMember("exports"),
                Value.asValue(getContext().getRequireFunction(this)),
                module,
                relativePath,
                fileName
        );

        return module.getMember("exports");
    });

    @Override
    public Value get() {
        return module.get();
    }

    public MetroServerResourceProvider getProvider() {
        return provider;
    }

    public String getServerAddress() {
        return serverAddress;
    }
}
