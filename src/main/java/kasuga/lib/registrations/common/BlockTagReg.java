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
 * BlockTag是方块的静态属性。
 * 例如 minecraft:stone 或 minecraft:sand 标签。
 * 它标记一个方块并分组不同的方块。详见 {@link TagKey}。
 * BlockTag is a static attribute of a block. For example minecraft:stone or minecraft:sand tag.
 * It marks a block and groups different blocks. See {@link TagKey}.
 */
public class BlockTagReg extends TagReg<Block> {
    TagKey<Block> tag = null;

    /**
     * 调用此函数来创建一个新的方块标签注册机。
     * @param registrationKey 你的标签键的名字。
     * @param path 你的标签文件的路径。根目录是 namespace:tag (通常在 "resource/data" 文件夹下)
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
