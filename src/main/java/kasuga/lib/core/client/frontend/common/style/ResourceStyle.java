package kasuga.lib.core.client.frontend.common.style;

import kasuga.lib.core.util.data_type.Pair;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.resources.ResourceLocation;

import java.io.StringReader;
import java.util.ArrayList;

public class ResourceStyle {
    public static Pair<ResourceLocation,String> parse(String string){
        int state = 0; // 0 = resource type, 1 = resource Name
        int escapeState = 0;
        int stringState = 0;
        ArrayList<String> locations = new ArrayList<>(2);
        StringBuilder current = new StringBuilder();
        for(int i=0;i<string.length();i++){
            char chr = string.charAt(i);
            if(escapeState == 1){
                current.append(chr);
                continue;
            }
            if(state == 2 && chr != ' ' && chr != ';')
                throw new IllegalStateException();
            if(chr == ';')
                break;
            switch (chr){
                case '\\':
                    state = 1;
                    break;
                case '\"':
                    if(stringState == 1)
                        stringState = 0;
                    else if(stringState == 0)
                        stringState = 1;
                    else
                        current.append(chr);
                    break;
                case '\'':
                    if(stringState == 2)
                        stringState = 0;
                    else if (stringState == 0)
                        stringState = 2;
                    else
                        current.append(chr);
                    break;
                case '(':
                    if(state == 0){
                        locations.add(current.toString());
                        current = new StringBuilder();
                        state = 1;
                        break;
                    }
                    throw new IllegalStateException();
                case ')':
                    if(state == 1){
                        locations.add(current.toString());
                        current = new StringBuilder();
                        state = 2;
                        break;
                    }
                    throw new IllegalStateException();
                case ' ':
                    if(stringState != 0)
                        current.append(chr);
                    break;
                default:
                    current.append(chr);
            }
        }
        if(locations.isEmpty())
            return null;
        if(locations.size() == 1){
            locations.add(0,"kasuga_lib:resource");
        }
        if(!locations.get(0).contains(":")){
            locations.set(0,"kasuga_lib:"+locations.get(0));
        }
        ResourceLocation loader = new ResourceLocation(locations.get(0));
        String param = locations.get(1);
        return Pair.of(loader,param);
    }
}