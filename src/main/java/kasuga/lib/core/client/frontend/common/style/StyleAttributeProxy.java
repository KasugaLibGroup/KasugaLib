package kasuga.lib.core.client.frontend.common.style;

import kasuga.lib.core.client.frontend.dom.attribute.AttributeProxy;

public class StyleAttributeProxy implements AttributeProxy {
    private final StyleList<?> list;

    public StyleAttributeProxy(StyleList<?> list){
        this.list = list;
    }
    @Override
    public String get() {
        return list.toString();
    }

    @Override
    public String set(String value) {
        list.clear();
        list.decode(value);
        return value;
    }
}
