package kasuga.lib.core.javascript;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.addons.node.EntryType;
import kasuga.lib.core.addons.node.NodePackageLoader;
import kasuga.lib.core.client.frontend.dom.registration.DOMPriorityRegistry;
import kasuga.lib.core.javascript.loader.JavascriptResourceEntryLoader;
import kasuga.lib.core.javascript.module.KasugaModuleLoader;
import kasuga.lib.core.javascript.prebuilt.registry.RegistryLoader;
import kasuga.lib.core.javascript.registration.RegistrationRegistry;
import kasuga.lib.core.util.Envs;
import net.minecraft.resources.ResourceLocation;

public class JavascriptApi {
    public final JavascriptThreadGroup GROUP_MAIN = new JavascriptThreadGroup("main");
    public final JavascriptThreadGroup GROUP_CLIENT = GROUP_MAIN.createChild("client");
    public JavascriptThreadGroup GROUP_GUI;
    // public final JavascriptResourceEntryLoader GUI_LOADER = new JavascriptResourceEntryLoader(GROUP_GUI,"guis.json");

    public final NodePackageLoader CLIENT_LOADER = new NodePackageLoader();

    public final RegistrationRegistry registry = new RegistrationRegistry();

    public void init() {
        GROUP_MAIN.getLoaderRegistry().register(new KasugaModuleLoader());
        if(Envs.isClient()){
            if(!KasugaLib.STACKS.GUI.isPresent()){
                throw new IllegalStateException("GUI stack is not present, this should not happen!");
            }
            GROUP_GUI = GROUP_CLIENT.createChild("gui");
            GROUP_GUI.getLoaderRegistry().register(new RegistryLoader());
            registry.register(new ResourceLocation("kasuga_lib:gui"), KasugaLib.STACKS.GUI.get().domRegistry);
            CLIENT_LOADER.bindRuntime(GROUP_GUI, EntryType.CLIENT);
        }
    }

    public void setup() {
        // GUI_LOADER.start();
    }

    public void renderTick(){
        GROUP_GUI.dispatchTick();
    }
}
