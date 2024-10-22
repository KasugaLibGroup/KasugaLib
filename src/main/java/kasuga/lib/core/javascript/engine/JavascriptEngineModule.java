package kasuga.lib.core.javascript.engine;

import kasuga.lib.core.addons.node.NodePackage;

public interface JavascriptEngineModule {
    public String getAbsoultePath();
    NodePackage getPackage();
    String getDirectoryName();
    Object getFeature(String name);
    boolean hasFeature(String name);
    boolean shouldCache();
    void setShouldCache(boolean shouldCache);
    JavascriptEngineModule setFeature(String name, Object object);
}
