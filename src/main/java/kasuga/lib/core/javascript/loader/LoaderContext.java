package kasuga.lib.core.javascript.loader;

public class LoaderContext {
    protected int loadPriority;

    public void setLoadPriority(int loadPriority) {
        this.loadPriority = loadPriority;
    }

    public int getLoadPriority() {
        return loadPriority;
    }
}
