package kasuga.lib.core.menu.targets;

import kasuga.lib.core.menu.base.BindingClient;

public class TargetsClient {
    public static void register(){
        BindingClient.registerBinding(Target.SCREEN, ClientScreenTarget::new);
        BindingClient.registerBinding(Target.WORLD_RENDERER, WorldRendererTarget::new);
        BindingClient.registerBinding(Target.TEXTURE, ClientTextureTarget::new);
    }
}
