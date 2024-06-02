package kasuga.lib.core.javascript;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.client.frontend.dom.registration.DOMPriorityRegistry;
import kasuga.lib.core.javascript.loader.JavascriptResourceEntryLoader;
import kasuga.lib.core.javascript.module.KasugaModuleLoader;
import kasuga.lib.core.javascript.prebuilt.registry.RegistryLoader;
import kasuga.lib.core.javascript.registration.RegistrationRegistry;
import net.minecraft.resources.ResourceLocation;

public class JavascriptApi {
    public final JavascriptThreadGroup GROUP_MAIN = new JavascriptThreadGroup("main");

    public final JavascriptThreadGroup GROUP_GUI = GROUP_MAIN.createChild("gui");

    public final JavascriptResourceEntryLoader GUI_LOADER = new JavascriptResourceEntryLoader(GROUP_GUI,"guis.json");

    public final RegistrationRegistry registry = new RegistrationRegistry();


    public void init() {
        GROUP_MAIN.getLoaderRegistry().register(new KasugaModuleLoader());
        GROUP_GUI.getLoaderRegistry().register(new RegistryLoader());
        registry.register(new ResourceLocation("kasuga_lib:gui"), KasugaLib.STACKS.GUI.domRegistry);
    }

    public void setup() {
        GUI_LOADER.start();
    }

    public void renderTick(){
        GROUP_GUI.dispatchTick();
    }
}
