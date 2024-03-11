package kasuga.lib.registrations;

import kasuga.lib.registrations.Reg;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public abstract class TagReg<T> extends Reg {

    public ResourceLocation location = null;
    public TagReg(String registrationKey) {
        super(registrationKey);
    }
    public abstract TagKey<T> tag();

    public ResourceLocation location() {return location;}

    public interface TagProvider<T> {
        TagKey<T> provide();
    }
}
