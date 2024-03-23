package kasuga.lib.core.events.client;

import kasuga.lib.KasugaLib;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class TextureRegistryEvent {

    @SubscribeEvent
    public static void onModelRegistry(ModelRegistryEvent bakingCompleted) {
        KasugaLib.STACKS.fireTextureRegistry();
        KasugaLib.STACKS.fontRegistry().onRegister();
    }
}
