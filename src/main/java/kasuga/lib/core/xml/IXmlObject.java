package kasuga.lib.core.xml;

import java.util.Set;

public interface IXmlObject<T> {
    String key();
    Set<T> getValues();
    T getValue(String key);
    void  setValue(String key, Object value);
    Set<IXmlObject<?>> attributes();
    IXmlObject<?> getAttribute(String key);
    void setAttribute(String key, Object value);
    String toFormattedString(int stage, boolean isPlainText);
    boolean isSingleSideElement();
    void setIsSingleSide(boolean singleSide);
    boolean isPrimitive();
}
