package kasuga.lib.registrations.common;

import kasuga.lib.core.annos.Mandatory;
import kasuga.lib.registrations.registry.SimpleRegistry;
import kasuga.lib.registrations.TagReg;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

/**
 * BlockTag is a static attribute of a block. For example minecraft:stone or minecraft:sand tag.
 * It marks a block and groups different blocks. See {@link TagKey}.
 */
public class BlockTagReg extends TagReg<Block> {
    TagKey<Block> tag = null;

    /**
     * Use this to create a new block tag registration.
     * @param registrationKey the name of your tag key.
     * @param path the path of your tag file. Root folder is namespace:tag (It's usually under the "resource/data" folder)
     */
    public BlockTagReg(String registrationKey, String path) {
        super(registrationKey, path);
    }

    public BlockTagReg(String namespace, String registrationKey, String path) {
        super(namespace ,registrationKey, path);
    }

    @Override
    @Mandatory
    public BlockTagReg submit(SimpleRegistry registry) {
        location = otherNamespace == null ?
                new ResourceLocation(registry.namespace, path) :
                new ResourceLocation(otherNamespace, path);
        tag = BlockTags.create(location);
        return this;
    }

    public @Nullable TagKey<Block> tag() {
        return tag;
    }

    @Override
    public String getIdentifier() {
        return "block_tag";
    }
}
