package kasuga.lib.core.client.frontend.commands;

public class MetroModuleInfo {

    private final String serverAddress;
    private final MetroServerResourceProvider provider;

    public MetroModuleInfo(
            String serverAddress,
            MetroServerResourceProvider provider
    ){
        this.serverAddress = serverAddress;
        this.provider = provider;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public MetroServerResourceProvider getProvider() {
        return provider;
    }
}
