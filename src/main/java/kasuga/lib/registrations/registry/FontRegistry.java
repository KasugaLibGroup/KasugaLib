package kasuga.lib.registrations.registry;

import kasuga.lib.core.client.render.component.Font;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;

public class FontRegistry {
    
    String namespace;
    private final HashMap<ResourceLocation, Font> listOfReg;
    public FontRegistry(String namespace) {
        this.namespace = namespace;
        this.listOfReg = new HashMap<>();
    }
    
    public void stackIn(Font reg) {
        listOfReg.put(reg.getLocation(), reg);
    }
    
    public void onRegister() {
        for(ResourceLocation location : listOfReg.keySet()) {
            listOfReg.get(location).loadFont();
        }
    }
    
    public Font getFont(ResourceLocation location) {
        return listOfReg.getOrDefault(location, null);
    }
}
