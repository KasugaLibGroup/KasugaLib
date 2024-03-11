package kasuga.lib.core.events.server;


import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ServerLevelEvents {

    @SubscribeEvent
    public void onLevelLoad(LevelEvent.Load event) {}

    @SubscribeEvent
    public void onLevelSave(LevelEvent.Save event) {}

    @SubscribeEvent
    public void onLevelExit(LevelEvent.Unload event) {}
}
