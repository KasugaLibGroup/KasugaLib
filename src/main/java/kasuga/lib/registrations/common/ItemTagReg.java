package kasuga.lib.registrations.common;

import kasuga.lib.core.annos.Mandatory;
import kasuga.lib.registrations.TagReg;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;


/**
 * ItemTag is a static attribute of items.
 * It marks a item and groups different items. See {@link TagKey}.
 */
public class ItemTagReg extends TagReg<Item> {
    TagKey<Item> tag = null;

    /**
     * Create a item tag reg.
     * @param registrationKey the registration key of your item tag.
     * @param path the resource location path of your item.
     */
    public ItemTagReg(String registrationKey, String path) {
        super(registrationKey, path);
    }

    public ItemTagReg(String namespace, String registrationKey, String path) {
        super(namespace, registrationKey, path);
    }

    /**
     * Submit your config to minecraft and forge registry.
     * @param registry the mod SimpleRegistry.
     * @return self.
     */
    @Override
    @Mandatory
    public ItemTagReg submit(SimpleRegistry registry) {
        location = otherNamespace == null ?
                new ResourceLocation(registry.namespace, path) :
                new ResourceLocation(otherNamespace, path);
        tag = ItemTags.create(location);
        return this;
    }

    public @Nullable TagKey<Item> tag() {
        return tag;
    }

    @Override
    public String getIdentifier() {
        return "item_tag";
    }
}
