package kasuga.lib.registrations.registry;

import kasuga.lib.core.client.render.texture.SimpleTexture;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.HashSet;

public class TextureRegistry {
    private final HashSet<SimpleTexture> UNREGED;
    private final HashMap<ResourceLocation, SimpleTexture> PICTURES;

    public TextureRegistry(String namespace) {
        UNREGED = new HashSet<>();
        PICTURES = new HashMap<>();
    }

    public void stackIn(SimpleTexture pic) {
        this.UNREGED.add(pic);
    }

    public HashSet<SimpleTexture> getUnregistered() {
        return UNREGED;
    }

    public void clearUnregistered() {
        UNREGED.clear();
    }

    public SimpleTexture getTexture(ResourceLocation location) {
        return PICTURES.getOrDefault(location, null);
    }

    public HashMap<ResourceLocation, SimpleTexture> getPictures() {
        return PICTURES;
    }

    public void onRegister() {
        for(SimpleTexture picture : getUnregistered()) {
            picture.uploadPicture(picture.getLocation());
            PICTURES.put(picture.getLocation(), picture);
        }
        UNREGED.clear();
    }
}
