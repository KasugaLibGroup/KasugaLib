package kasuga.lib.core.client.frontend.commands;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.javascript.JavascriptContext;
import kasuga.lib.core.javascript.module.JavascriptModule;

public class MetroLoaderModule extends JavascriptModule {
    MetroServerResourceProvider provider;
    String serverAddress = "http://127.0.0.1:8081";
    public MetroLoaderModule(JavascriptContext context, MetroServerResourceProvider provider) {
        super(context);
        this.provider = provider;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public MetroServerResourceProvider getProvider() {
        return provider;
    }
}
