package kasuga.lib.core.xml;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class XmlCompound implements IXmlObject<IXmlObject<?>> {
    private String key = "";
    private final Set<IXmlObject<?>> values;
    private final Set<IXmlObject<?>> attributes;
    private boolean singleSide = false;

    public XmlCompound(String key, IXmlObject<?>... attributes){
        this.key = key;
        values = new HashSet<>();
        this.attributes = new HashSet<>();
        this.attributes.addAll(List.of(attributes));
    }

    public XmlCompound(String key, boolean singleSide, IXmlObject<?>... attributes){
        this.key = key;
        values = new HashSet<>();
        this.attributes = new HashSet<>();
        this.attributes.addAll(List.of(attributes));
        this.singleSide = singleSide;
    }

    public static XmlCompound empty() {
        return new XmlCompound("");
    }
    @Override
    public String key() {
        return key;
    }

    @Override
    public Set<IXmlObject<?>> getValues() {
        return values;
    }

    @Override
    public IXmlObject<?> getValue(String key) {
        return null;
    }

    @Override
    public void setValue(String key, Object value) {
        if(!(value instanceof IXmlObject<?>)) return;
        values.add((IXmlObject<?>) value);
    }

    @Override
    public Set<IXmlObject<?>> attributes() {
        return attributes;
    }

    @Override
    public IXmlObject<?> getAttribute(String key) {
        Optional<IXmlObject<?>> optional = attributes.stream().filter(a -> a.key().equals(key)).findAny();
        return optional.orElse(null);
    }

    @Override
    public void setAttribute(String key, Object value) {
        IXmlObject<?> attr = getAttribute(key);
        if(attr != null)
            attr.setValue(key, value);
    }

    @Override
    public String toFormattedString(int stage, boolean isPlainText) {
        String repeat = "    ".repeat(isPlainText ? 0 : Math.max(0, stage));
        StringBuilder builder = new StringBuilder(repeat + "<" + key);
        for(IXmlObject<?> attr : attributes) {
            builder.append(" ").append(attr.key()).append("=");
            if(attr instanceof XmlString)
                builder.append("\"").append(attr.getValue(attr.key()).toString()).append("\"");
            else
                builder.append(attr.getValue(attr.key()).toString());
        }
        builder.append(">\n");
        for(IXmlObject<?> value : values) {
            builder.append(value.toFormattedString(stage + 1, isPlainText));
        }
        builder.append(repeat);
        builder.append("</").append(key).append(">\n");
        return builder.toString();
    }

    @Override
    public boolean isSingleSideElement() {
        return singleSide;
    }

    @Override
    public void setIsSingleSide(boolean singleSide) {
        this.singleSide = singleSide;
    }

    @Override
    public boolean isPrimitive() {
        return false;
    }

    @Override
    public String toString() {
        return toFormattedString(0, false);
    }
}
