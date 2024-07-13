package kasuga.lib.core.javascript.ffi;

import net.minecraft.resources.ResourceLocation;
import org.graalvm.polyglot.Value;

public class ResourceLocationFFIHelper {
    public static ResourceLocation fromValue(Value value){
        if(!value.isString())
            throw new IllegalArgumentException();
        String val = value.asString();
        return new ResourceLocation(val);
    }
}
