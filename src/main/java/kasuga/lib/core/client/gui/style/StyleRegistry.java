package kasuga.lib.core.client.gui.style;

import java.util.HashMap;

public class StyleRegistry {

    public static HashMap<String,StyleType<?>> registry = new HashMap<>();
    public static HashMap<StyleType<?>,String> reversal = new HashMap<>();

    public static StyleType<?> getStyle(String name){
        return registry.get(name);
    }

    public static String getStyleName(StyleType<?> styleType){
        return reversal.get(styleType);
    }

    public static <T extends StyleType<?>> T register(String name,T styleType){
        registry.put(name,styleType);
        reversal.put(styleType,name);
        return styleType;
    }
}
