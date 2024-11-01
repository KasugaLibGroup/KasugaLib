package kasuga.lib.core.events.server;


import kasuga.lib.KasugaLib;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ServerLevelEvents {

    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event) {}

    @SubscribeEvent
    public static void onLevelSave(LevelEvent.Save event) {
        if(event.getLevel().getServer().overworld() == event.getLevel()){
            KasugaLib.STACKS.RAILWAY.save(event.getLevel().getServer().overworld());
        }
    }

    @SubscribeEvent
    public static void onLevelExit(LevelEvent.Unload event) {
        if(event.getLevel().getServer().overworld() == event.getLevel()){
            KasugaLib.STACKS.RAILWAY.save(event.getLevel().getServer().overworld());
        }
    }
}
