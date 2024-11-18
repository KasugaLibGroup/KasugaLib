package kasuga.lib.core.util.nbt_json;

import net.minecraft.nbt.Tag;

public class ListIsEmptyException extends Exception {

    public final String path;

    public final Tag tag;

    public ListIsEmptyException(Tag tag, String path) {
        this.tag = tag;
        this.path = path;
    }

    @Override
    public String getMessage() {
        return "Lists in template NBT should not be empty! Found empty list at nbt path: " + path;
    }
}
