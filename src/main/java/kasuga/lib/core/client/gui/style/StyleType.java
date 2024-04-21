package kasuga.lib.core.client.gui.style;

public interface StyleType<T extends Style<?>> {
    public T getDefault();
    public T create(String string);
}
