package kasuga.lib.registrations;

import kasuga.lib.registrations.Reg;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

/**
 * This is the base class of KasugaLib style Tag Registration.
 * Tag is a kind of data-gen element. It marks some static attribute of blocks or items.
 * For example, all blocks which could be harvested by pickaxe should have a tag "minecraft:mineable/pickaxe"
 * @param <T> Tag type you would like to register.
 */
public abstract class TagReg<T> extends Reg {

    public ResourceLocation location = null;
    public TagReg(String registrationKey) {
        super(registrationKey);
    }
    public abstract TagKey<T> tag();

    /**
     * get resource location of the tag.
     * @return the location.
     */
    public ResourceLocation location() {return location;}
}
