package kasuga.lib.core.menu.javascript;

import kasuga.lib.core.channel.peer.ChannelHandle;
import kasuga.lib.core.javascript.JavascriptContext;
import kasuga.lib.core.javascript.engine.JavascriptEngineContext;
import kasuga.lib.core.javascript.engine.JavascriptValue;
import kasuga.lib.core.javascript.registration.JavascriptPriorityRegistry;
import net.minecraft.resources.ResourceLocation;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public class JavascriptMenuRegistry extends JavascriptPriorityRegistry<JavascriptMenuHandler> {
    @Override
    public JavascriptMenuHandler fromValue(JavascriptContext context, JavascriptValue value) {
        if(value.canExecute()){
            value.pin();
            return JavascriptMenuHandler.ofExecutable(context.getRuntimeContext() ,value);
        }
        throw new IllegalStateException("Value is not executable");
    }

    HashMap<ResourceLocation, HashSet<JavascriptMenu>> menus = new HashMap<>();

    @Override
    public void register(JavascriptContext self, ResourceLocation location, JavascriptMenuHandler item) {
        super.register(self, location, item);
        dispatchReload(location);
    }

    @Override
    public void unregister(JavascriptContext self, ResourceLocation location, JavascriptMenuHandler item) {
        super.unregister(self, location, item);
        dispatchReload(location);
    }

    public void listenReload(ResourceLocation id, JavascriptMenu menu){
        menus.computeIfAbsent(id, k -> new HashSet<>()).add(menu);
    }

    public void unlistenReload(ResourceLocation id, JavascriptMenu menu){
        if(!menus.containsKey(id)){
            return;
        }
        menus.get(id).remove(menu);
        if(menus.get(id).isEmpty()){
            menus.remove(id);
        }
    }

    private void dispatchReload(ResourceLocation id){
        if(!menus.containsKey(id)){
            return;
        }
        ArrayList<JavascriptMenu> pendingUpdateMenus = new ArrayList<>(menus.get(id));
        for(JavascriptMenu menu : pendingUpdateMenus){
            menu.reload();
        }
    }
}
