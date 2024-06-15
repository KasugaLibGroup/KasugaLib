package kasuga.lib.core;

import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

public class KasugaAtlasSprite extends TextureAtlasSprite {
    public KasugaAtlasSprite(ResourceLocation pAtlasLocation, SpriteContents pContents) {
        super(pAtlasLocation, pContents, pContents.width(), pContents.height(), 0, 0);
    }
}
