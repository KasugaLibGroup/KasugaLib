package kasuga.lib.core.client.frontend.common.style;


import java.util.HashMap;

public class StyleRegistry<R> {

    public HashMap<String, StyleType<?,R>> registry = new HashMap<>();
    public HashMap<StyleType<?,R>,String> reversal = new HashMap<>();

    public StyleType<?,R> getStyle(String name){
        return registry.get(name);
    }

    public String getStyleName(StyleType<?,R> styleType){
        return reversal.get(styleType);
    }

    public <T extends StyleType<?,R>> T register(String name, T styleType){
        registry.put(name,styleType);
        reversal.put(styleType,name);
        return styleType;
    }
}
