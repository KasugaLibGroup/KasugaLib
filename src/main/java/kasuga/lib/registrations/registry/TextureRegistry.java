package kasuga.lib.registrations.registry;

import kasuga.lib.core.annos.Inner;
import kasuga.lib.core.client.render.texture.old.SimpleTexture;
import kasuga.lib.core.client.render.texture.old.WorldTexture;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.HashSet;

/**
 * TextureRegistry是KasugaLib风格的纹理注册注册机。
 * 你可以使用{@link SimpleTexture}和{@link WorldTexture}来进行快速纹理使用。
 * TextureRegistry is registry for KasugaLib style textures. We provide {@link SimpleTexture}
 * and {@link WorldTexture} for quick Texture usage.
 */
@Inner
public class TextureRegistry {
    private final HashSet<SimpleTexture> UNREGED;
    private final HashMap<ResourceLocation, SimpleTexture> PICTURES;

    public TextureRegistry(String namespace) {
        UNREGED = new HashSet<>();
        PICTURES = new HashMap<>();
    }

    @Inner
    public void stackIn(SimpleTexture pic) {
        this.UNREGED.add(pic);
    }

    @Inner
    public HashSet<SimpleTexture> getUnregistered() {
        return UNREGED;
    }

    @Inner
    public void clearUnregistered() {
        UNREGED.clear();
    }

    /**
     * 从注册机获取纹理
     * @param location 纹理位置
     * @return 纹理
     * get texture from this registry.
     * @param location the location of the texture.
     * @return texture.
     */
    public SimpleTexture getTexture(ResourceLocation location) {
        return PICTURES.getOrDefault(location, null);
    }

    /**
     * 获取所有注册的纹理
     * @return 注册的纹理
     * get all registered pics.
     * @return registered pics.
     */
    public HashMap<ResourceLocation, SimpleTexture> getPictures() {
        return PICTURES;
    }

    /**
     * 这是在{@link kasuga.lib.core.events.client.TextureRegistryEvent}中触发的事件
     * this is a event fired in {@link kasuga.lib.core.events.client.TextureRegistryEvent}
     */
    @Inner
    public void onRegister() {
        for(SimpleTexture picture : getUnregistered()) {
            picture.uploadPicture(picture.getLocation());
            PICTURES.put(picture.getLocation(), picture);
        }
        UNREGED.clear();
    }
}
