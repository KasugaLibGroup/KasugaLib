package kasuga.lib.core.client.gui.intergration.javascript;

import kasuga.lib.core.client.gui.components.Node;

public class JavascriptGuiBinding {
    private final JavascriptGuiContainer container;
    private final JavascriptNativeApi nativeApi;
    private final JavascriptPlatformRuntime platformRuntime;


    public JavascriptGuiBinding(JavascriptPlatformRuntime platformRuntime, Node root) {
        this.container = new JavascriptGuiContainer(root);
        this.nativeApi = new JavascriptNativeApi(this);
        this.platformRuntime = platformRuntime;
    }

    public JavascriptGuiContainer getContainer() {
        return container;
    }

    public void close(){
        this.container.close();
    }

    public JavascriptNativeApi getNative() {
        return nativeApi;
    }

    public JavascriptPlatformRuntime getPlatform() {
        return platformRuntime;
    }
}
