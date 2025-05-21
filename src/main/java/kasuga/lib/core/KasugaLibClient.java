package kasuga.lib.core;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.client.model.BedrockModelLoader;
import kasuga.lib.core.client.model.model_json.BedrockModel;
import kasuga.lib.core.client.render.texture.StaticImageHolder;
import kasuga.lib.core.resource.Resources;
import kasuga.lib.core.create.graph.RailwayManager;
import kasuga.lib.core.create.graph.RailwayManager;
import kasuga.lib.core.util.LazyRecomputable;
import kasuga.lib.core.util.projectile.PanelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashSet;

import static kasuga.lib.KasugaLib.MOD_ID;

@OnlyIn(Dist.CLIENT)
public class KasugaLibClient {

    public static final StaticImageHolder NO_IMG =
            new StaticImageHolder(new ResourceLocation(MOD_ID, "textures/gui/no_img.png"));

    public static final HashSet<PanelRenderer> PANEL_RENDERERS = new HashSet<>();
    public static final String INTERNAL_TEXTURE_PACK = "internal_texture";

    public static void invoke() {
//        Resources.registerCustomPack(PackType.CLIENT_RESOURCES,
//                MOD_ID, INTERNAL_TEXTURE_PACK);
    }

    public static final RailwayManager RAILWAY = RailwayManager.createClient();
}
