package kasuga.lib.core.client.frontend.assets;

import kasuga.lib.core.addons.node.AssetReader;
import kasuga.lib.core.client.render.texture.WorldTexture;
import kasuga.lib.core.javascript.Asset;
import net.minecraft.resources.ResourceLocation;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.function.BiFunction;

public class TextureAssetProvider{
    public static WorldTexture apply(InputStream stream, UUID uuid) {
        try{
            WorldTexture texture = new WorldTexture(
                    new ResourceLocation("kasuga_resources",uuid.toString()),
                    new ByteArrayInputStream(stream.readAllBytes()),
                    0,
                    0,
                    0,
                    0,
                    0xffffff,
                    1.0F
            );
            return texture.cutSize(0,0,texture.getImgWidth(),texture.getImgHeight());
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void init(){
        AssetReader.assetReaders.put("texture", TextureAssetProvider::apply);
    }
}
