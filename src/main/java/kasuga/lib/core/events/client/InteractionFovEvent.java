package kasuga.lib.core.events.client;

import kasuga.lib.core.client.interaction.GuiOperatingPerspectiveScreen;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.FOVModifierEvent;

public class InteractionFovEvent {
    public static void onComputedFov(FOVModifierEvent event){
        if(Minecraft.getInstance().screen != null &&
                Minecraft.getInstance().screen instanceof GuiOperatingPerspectiveScreen operating)
            event.setNewfov(operating.getFovModifier());
    }
}
