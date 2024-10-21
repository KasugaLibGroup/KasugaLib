package kasuga.lib.core.menu.targets;

import kasuga.lib.core.menu.BindingClient;
import kasuga.lib.core.menu.GuiBinding;

public class TargetsClient {
    public static void reigster(){
        BindingClient.registerBinding(Target.SCREEN, ClientScreenTarget::new);
        BindingClient.registerBinding(Target.WORLD_RENDERER, WorldRendererTarget::new);
        BindingClient.registerBinding(Target.TEXTURE, ClientTextureTarget::new);
    }
}
