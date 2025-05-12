package kasuga.lib.core.client.block_bench_model.json_data;

import com.mojang.blaze3d.platform.NativeImage;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.KasugaLibClient;
import kasuga.lib.core.resource.Resources;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.FallbackResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.stream.Stream;

@Getter
public class TextureSource {

    private final String type;
    private final byte[] data;
    private final boolean valid;

    public TextureSource(String base64str) {
       if (!base64str.startsWith("data:image/")) {
           type = null;
           valid = false;
           data = new byte[0];
           return;
       }
       base64str = base64str.substring("data:image/".length());
       type = base64str.substring(0, base64str.indexOf(";"));
       base64str = base64str.substring(base64str.indexOf(";") + 1);
       if (!base64str.startsWith("base64")) {
           data = new byte[0];
           valid = false;
           return;
       }
       base64str = base64str.substring(base64str.indexOf(",") + 1);
       data = Base64.getDecoder().decode(base64str);
       valid = true;
    }

    public void registerTextureAs(ResourceLocation location) throws IOException {
        if (!valid) return;
        Resources.CheatResourceLocation cheat =
                new Resources.CheatResourceLocation(location.getNamespace(), location.getPath());
        Minecraft.getInstance().textureManager.register((ResourceLocation) cheat,
                new DynamicTexture(NativeImage.read(new ByteArrayInputStream(data))));
    }

    public Material getAsMaterial(ResourceLocation atlas, ResourceLocation location) throws IOException {
        if (!valid) return null;
        ResourceLocation rl = new ResourceLocation(
                location.getNamespace(),
                "textures/" + location.getPath() + ".png"
        );
        registerAsResource(rl);
        return new Material(atlas, location);
    }

    public void registerAsResource(ResourceLocation location) {
        Resources.registerResource(KasugaLib.MOD_ID, KasugaLibClient.INTERNAL_TEXTURE_PACK,
                pack -> pack.registerResource(location, data));
    }
}
