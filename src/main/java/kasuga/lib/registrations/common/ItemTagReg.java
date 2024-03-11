package kasuga.lib.registrations.common;

import kasuga.lib.registrations.registry.SimpleRegistry;
import kasuga.lib.registrations.TagReg;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

public class ItemTagReg extends TagReg<Item> {
    TagKey<Item> tag = null;
    private final String path;
    public ItemTagReg(String registrationKey, String path) {
        super(registrationKey);
        this.path = path;
    }

    @Override
    public ItemTagReg submit(SimpleRegistry registry) {
        location = new ResourceLocation(registry.namespace, path);
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
