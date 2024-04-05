package kasuga.lib.core.xml;

import java.util.Optional;
import java.util.Set;

public class XmlNumber implements IXmlObject<Double>{
    private String key = "";
    private double value = 0;
    private final Set<IXmlObject<?>> attributes;
    private boolean singleSide = false;

    public XmlNumber(String key, Double value, IXmlObject<?>... attributes) {
        this.key = key;
        this.value = value;
        this.attributes = Set.of(attributes);
    }

    public XmlNumber(String key, Double value, boolean singleSide, IXmlObject<?>... attributes) {
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
    public Set<Double> getValues() {
        return Set.of(value);
    }

    @Override
    public Double getValue(String key) {
        return this.key.equals(key) ? value : 0d;
    }

    @Override
    public void setValue(String key, Object value) {
        if(!(value instanceof Double)) return;
        if(this.key.equals(key)) this.value = (Double) value;
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
        builder.append(singleSide ? "/>\n" : ">");
        if(!singleSide)
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
