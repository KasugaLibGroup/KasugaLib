package kasuga.lib.core.events.client;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.AtlasResources;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class TextureRegistryEvent {

    @SubscribeEvent
    public static void onModelRegistry(ModelEvent.RegisterAdditional ignored) {
        KasugaLib.STACKS.fireTextureRegistry();
        KasugaLib.STACKS.fontRegistry().onRegister();
        AtlasResources resources = new AtlasResources(KasugaLib.MOD_ID);
        try {
            resources.runLoadingResources();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
