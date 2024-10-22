package kasuga.lib.core.client.frontend.dom.registration;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.client.frontend.gui.GuiInstance;
import kasuga.lib.core.javascript.JavascriptContext;
import kasuga.lib.core.javascript.engine.JavascriptValue;
import kasuga.lib.core.javascript.registration.JavascriptPriorityRegistry;
import net.minecraft.resources.ResourceLocation;

public class DOMPriorityRegistry extends JavascriptPriorityRegistry<DOMRegistryItem> {

    @Override
    public DOMRegistryItem fromValue(JavascriptValue value) {
        value.pin();
        if(value.canExecute()){
           return DOMRegistryItem.fromExecutable(value);
        }
        return DOMRegistryItem.fromConfigurableObject(value);
    }

    public void notifyUpdate(ResourceLocation location){
        for (GuiInstance instance : KasugaLib.STACKS.GUI.orElseThrow(IllegalStateException::new).instances) {
            instance.getContext().ifPresent((context)->{
                context.getRenderer().notifyUpdate(location);
            });
        }
    }

    @Override
    public void unregister(JavascriptContext self, ResourceLocation location, DOMRegistryItem item) {
        super.unregister(self, location, item);
        notifyUpdate(location);
    }

    @Override
    public void register(JavascriptContext self, ResourceLocation location, DOMRegistryItem item) {
        super.register(self, location, item);
        notifyUpdate(location);
    }
}
