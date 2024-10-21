package kasuga.lib.core.javascript.engine;

import java.util.HashMap;

public abstract class AbstractJavascriptEngineModule implements JavascriptEngineModule{
    HashMap<String, Object> featureMap = new HashMap<>();
    @Override
    public JavascriptEngineModule setFeature(String name, Object object) {
        featureMap.put(name, object);
        return this;
    }

    @Override
    public Object getFeature(String name) {
        return featureMap.get(name);
    }

    @Override
    public boolean hasFeature(String name) {
        return featureMap.containsKey(name);
    }

    boolean shouldCache = true;

    @Override
    public boolean shouldCache() {
        return shouldCache;
    }

    @Override
    public void setShouldCache(boolean shouldCache) {
        this.shouldCache = shouldCache;
    }
}
