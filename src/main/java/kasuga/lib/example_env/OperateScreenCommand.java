package kasuga.lib.example_env;

import com.mojang.blaze3d.systems.RenderSystem;
import kasuga.lib.core.client.interaction.GuiOperatingPerspectiveScreen;
import net.minecraft.client.Minecraft;

public class OperateScreenCommand {
    public static void invoke(){
        RenderSystem.recordRenderCall(()->{
            Minecraft.getInstance().setScreen(new GuiOperatingPerspectiveScreen());
        });
    }
}
