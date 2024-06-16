package kasuga.lib.core.events.both;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.client.frontend.common.command.DevelopmentCommand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = KasugaLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommandEvent {
    @SubscribeEvent
    public static void register(RegisterCommandsEvent event){
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT,()->()->{
            DevelopmentCommand.register(event.getDispatcher());
        });
    }
}