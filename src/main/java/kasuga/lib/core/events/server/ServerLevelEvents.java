package kasuga.lib.core.events.server;


import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ServerLevelEvents {

    @SubscribeEvent
    public void onLevelLoad(WorldEvent.Load event) {}

    @SubscribeEvent
    public void onLevelSave(WorldEvent.Save event) {}

    @SubscribeEvent
    public void onLevelExit(WorldEvent.Unload event) {}
}
