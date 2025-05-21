package kasuga.lib.core.base;

import kasuga.lib.KasugaLib;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class AllKasugaTags {
    public static final TagKey<Block> DRIVE_CONTROLLER_BLOCKS
            = BlockTags.create(KasugaLib.STACKS.REGISTRY.asResource("drive_controller"));
}
