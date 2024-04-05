package kasuga.lib.registrations.registry;

import kasuga.lib.core.annos.Inner;
import kasuga.lib.core.client.render.texture.SimpleTexture;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.HashSet;

/**
 * TextureRegistry is registry for KasugaLib style textures. We provide {@link SimpleTexture}
 * and {@link kasuga.lib.core.client.render.texture.WorldTexture} for quick Texture usage.
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
     * get texture from this registry.
     * @param location the location of the texture.
     * @return texture.
     */
    public SimpleTexture getTexture(ResourceLocation location) {
        return PICTURES.getOrDefault(location, null);
    }

    /**
     * get all registered pics.
     * @return registered pics.
     */
    public HashMap<ResourceLocation, SimpleTexture> getPictures() {
        return PICTURES;
    }

    /**
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
