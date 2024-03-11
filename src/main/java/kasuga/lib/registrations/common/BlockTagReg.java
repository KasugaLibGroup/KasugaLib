package kasuga.lib.registrations.common;

import kasuga.lib.registrations.registry.SimpleRegistry;
import kasuga.lib.registrations.TagReg;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public class BlockTagReg extends TagReg<Block> {
    TagKey<Block> tag = null;
    private final String path;

    public BlockTagReg(String registrationKey, String path) {
        super(registrationKey);
        this.path = path;
    }

    @Override
    public BlockTagReg submit(SimpleRegistry registry) {
        location = new ResourceLocation(registry.namespace, path);
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
