package kasuga.lib.core.client.frontend.commands;

import com.mojang.blaze3d.systems.RenderSystem;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.client.frontend.gui.GuiInstance;
import kasuga.lib.core.client.frontend.gui.GuiScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public class GuiScreenHelper {
    public static void attach(UUID instanceId){
        GuiInstance instance = KasugaLib.STACKS.GUI.orElseThrow().getInstanceById(instanceId).get();
        if(instance == null)
            return;
        RenderSystem.recordRenderCall(()->{
            Minecraft.getInstance().setScreen(new GuiScreen(instance));
        });
    }

    public static void createAndAttach(ResourceLocation location){
        RenderSystem.recordRenderCall(()->{
            Minecraft.getInstance().setScreen(KasugaLib.STACKS.GUI.get().create(location).createScreen());
        });
    }
}
