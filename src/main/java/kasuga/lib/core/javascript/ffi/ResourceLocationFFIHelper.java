package kasuga.lib.core.javascript.ffi;

import kasuga.lib.core.javascript.engine.JavascriptValue;
import net.minecraft.resources.ResourceLocation;

public class ResourceLocationFFIHelper {
    public static ResourceLocation fromValue(JavascriptValue value){
        if(!value.isString())
            throw new IllegalArgumentException();
        String val = value.asString();
        return new ResourceLocation(val);
    }
}
