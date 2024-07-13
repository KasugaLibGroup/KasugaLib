package kasuga.lib.core.events.both;

import kasuga.lib.KasugaLib;
import kasuga.lib.registrations.common.CommandReg;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = KasugaLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommandEvent {
    @SubscribeEvent
    public static void register(RegisterCommandsEvent event){
        CommandReg.register(event.getDispatcher());
    }
}