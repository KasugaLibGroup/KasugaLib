package kasuga.lib.core;

import kasuga.lib.core.client.render.texture.ImageMask;
import kasuga.lib.core.client.render.texture.StaticImage;
import kasuga.lib.core.client.render.texture.StaticImageHolder;
import kasuga.lib.core.util.LazyRecomputable;
import kasuga.lib.core.util.data_type.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.IOException;

import static kasuga.lib.KasugaLib.MOD_ID;

@OnlyIn(Dist.CLIENT)
public class KasugaLibClient {

    public static final StaticImageHolder NO_IMG =
            new StaticImageHolder(new ResourceLocation(MOD_ID, "textures/gui/no_img.png"));

    public static void invoke() {}
}
