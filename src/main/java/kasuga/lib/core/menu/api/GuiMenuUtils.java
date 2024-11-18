package kasuga.lib.core.menu.api;

import kasuga.lib.core.menu.base.GuiMenu;
import kasuga.lib.core.menu.targets.ClientScreenTarget;
import kasuga.lib.core.util.Envs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

public class GuiMenuUtils {
    public static void openScreen(GuiMenu menu){
        if(Envs.isClient()){
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT,()->()-> ClientScreenTarget.openScreen(menu));
        }else{
            // @TODO
        }
    }
}
