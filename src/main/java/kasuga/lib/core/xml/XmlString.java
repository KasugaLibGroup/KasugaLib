package kasuga.lib.core.xml;

import java.util.Optional;
import java.util.Set;

public class XmlString implements IXmlObject<String>{
    private String key = "", value = "";
    private final Set<IXmlObject<?>> attributes;
    private boolean singleSide;

    public XmlString(String key, String value, IXmlObject<?>... attributes) {
        this.key = key;
        this.value = value;
        this.attributes = Set.of(attributes);
    }

    public XmlString(String key, String value, boolean singleSide, IXmlObject<?>... attributes) {
        this.key = key;
        this.value = value;
        this.attributes = Set.of(attributes);
        this.singleSide = singleSide;
    }

    @Override
    public String key() {
        return key;
    }

    @Override
    public Set<String> getValues() {
        return Set.of(value);
    }

    @Override
    public String getValue(String key) {
        return this.key.equals(key) ? value : "";
    }

    @Override
    public void setValue(String key, Object value) {
        if(!(value instanceof String)) return;
        if(this.key.equals(key)) this.value = (String) value;
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

    private boolean keyIsXml() {
        return key.equals("xml");
    }

    @Override
    public String toFormattedString(int stage, boolean isPlainText) {
        String repeat = "    ".repeat(isPlainText ? 0 : Math.max(0, stage));
        StringBuilder builder = new StringBuilder(repeat + (keyIsXml() ? "<?" : "<") + key);
        for(IXmlObject<?> attr : attributes) {
            builder.append(" ").append(attr.key()).append("=");
            if(attr instanceof XmlString)
                builder.append("\"").append(attr.getValue(attr.key()).toString()).append("\"");
            else
                builder.append(attr.getValue(attr.key()).toString());
        }

            builder.append((keyIsXml() ? "?>\n" : (singleSide ? "/>\n" : ">")));
        if(!keyIsXml() && !singleSide)
            builder.append(value).append("</").append(key).append(">\n");
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
        return true;
    }

    @Override
    public String toString() {
        return toFormattedString(0, false);
    }
}
