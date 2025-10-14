package kasuga.lib.registrations;

import kasuga.lib.registrations.Reg;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

/**
 * 这是KasugaLib的Tag注册基类。
 * Tag是一种数据生成元素。它标记了方块或物品的一些静态属性。
 * 例如，所有可以用镐子开采的方块都应该有一个标签"minecraft:mineable/pickaxe"
 * @param <T> 你想注册的Tag类型。
 * This is the base class of KasugaLib style Tag Registration.
 * Tag is a kind of data-gen element. It marks some static attribute of blocks or items.
 * For example, all blocks which could be harvested by pickaxe should have a tag "minecraft:mineable/pickaxe"
 * @param <T> Tag type you would like to register.
 */
public abstract class TagReg<T> extends Reg {

    public ResourceLocation location = null;

    public final String otherNamespace;
    public final String path;

    public TagReg(String registrationKey, String path) {
        super(registrationKey);
        otherNamespace = null;
        this.path = path;
    }

    public TagReg(String namespace, String registrationKey, String path) {
        super(registrationKey);
        otherNamespace = namespace;
        this.path = path;
    }

    public abstract TagKey<T> tag();

    /**
     * 获取tag的资源位置
     * @return 路径
     * get resource location of the tag.
     * @return the location.
     */
    public ResourceLocation location() {return location;}
}
