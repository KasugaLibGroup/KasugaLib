package kasuga.lib.core.client.frontend.rendering;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.client.frontend.assets.TextureAssetProvider;
import kasuga.lib.core.client.render.texture.SimpleTexture;
import kasuga.lib.core.client.render.texture.StaticImage;
import kasuga.lib.core.client.render.texture.WorldTexture;

import java.util.UUID;
import java.util.function.Supplier;

public class AssetImageProvider implements ImageProvider {
    private final String name;

    public AssetImageProvider(String name) {
        this.name = name;
    }

    @Override
    public WorldTexture getWorldTexture() {
        if(!KasugaLib.STACKS.JAVASCRIPT.ASSETS.isPresent())
            return null;
        UUID uuid = null;
        try{
            uuid = UUID.fromString(name);
        }catch (IllegalArgumentException e){
            return null;
        }
        if(!KasugaLib.STACKS.JAVASCRIPT.ASSETS.get().containsKey(uuid))
            return null;
        Object obj = KasugaLib.STACKS.JAVASCRIPT.ASSETS.get().get(uuid);
        if(obj instanceof WorldTexture)
            return (WorldTexture) obj;
        return null;
    }

    @Override
    public SimpleTexture getSimpleTexture() {
        if(!KasugaLib.STACKS.JAVASCRIPT.ASSETS.isPresent())
            return null;
        UUID uuid = null;
        try{
             uuid = UUID.fromString(name);
        }catch (IllegalArgumentException e){
            return null;
        }
        if(!KasugaLib.STACKS.JAVASCRIPT.ASSETS.get().containsKey(uuid))
            return null;
        Object obj = KasugaLib.STACKS.JAVASCRIPT.ASSETS.get().get(uuid);
        if(obj instanceof WorldTexture worldTexture)
            return worldTexture;
        if(obj instanceof SimpleTexture simpleTexture)
            return simpleTexture;
        return null;
    }

    @Override
    public StaticImage getImage() {
        if (!KasugaLib.STACKS.JAVASCRIPT.ASSETS.isPresent())
            return null;
        UUID id;
        try {
            id = UUID.fromString(name);
        } catch (IllegalArgumentException e) {
            return null;
        }
        if(!KasugaLib.STACKS.JAVASCRIPT.ASSETS.get().containsKey(id))
            return null;
        Object obj = KasugaLib.STACKS.JAVASCRIPT.ASSETS.get().get(id);
        if (obj instanceof TextureAssetProvider.ImageHolder image)
            return image.supplier().get();
        return null;
    }
}
