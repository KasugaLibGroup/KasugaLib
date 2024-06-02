package kasuga.lib.core.client.frontend.common.layouting;

public interface LayoutEngine<T extends EngineLayoutContext> {
    public void apply(LayoutContext context);
    public void trigger(LayoutContext context);
    public void layout(LayoutContext context);
    public T createContext(LayoutContext context);
}
