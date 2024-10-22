package kasuga.lib.core.javascript.engine;

public interface JavascriptModuleLoader {
    public JavascriptEngineModule load(JavascriptEngineContext engineContext, String name, JavascriptEngineModule source);
}
