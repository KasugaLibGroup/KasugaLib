package kasuga.lib.core.events.both;

import kasuga.lib.core.client.gui.commands.DevelopmentCommand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;

public class CommandEvent {
    @SubscribeEvent
    public static void register(RegisterCommandsEvent event){
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT,()->()->{
            DevelopmentCommand.register(event.getDispatcher());
        });
    }
}
