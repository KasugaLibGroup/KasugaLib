package kasuga.lib.core.util.nbt_json;

import net.minecraft.nbt.Tag;

public class NoAvailableConversionException extends Exception {

    public final String path;
    public final Class<? extends Tag> clazz;

    public NoAvailableConversionException(Class<? extends Tag> clazz, String path) {
        super();
        this.clazz = clazz;
        this.path = path;
    }

    @Override
    public String getMessage() {
        return "No available conversion pair for " + clazz.getName() + " at " + path;
    }
}
