package kasuga.lib.registrations.common;

import kasuga.lib.core.annos.Mandatory;
import kasuga.lib.registrations.registry.SimpleRegistry;
import kasuga.lib.registrations.TagReg;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;


/**
 * 物品Tag是物品的一个静态属性。
 * 它标记一个物品并分组不同的物品。见{@link TagKey}。
 * ItemTag is a static attribute of items.
 * It marks a item and groups different items. See {@link TagKey}.
 */
public class ItemTagReg extends TagReg<Item> {
    TagKey<Item> tag = null;

    /**
     * 创建一个物品Tag注册。
     * @param registrationKey 你的物品Tag的注册名。
     * @param path 你的物品的资源位置路径。
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
     * 将你的配置提交到minecraft和forge注册表。
     * @param registry mod的SimpleRegistry。
     * @return 自身
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
