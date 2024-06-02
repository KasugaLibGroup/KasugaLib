package kasuga.lib.core.javascript.module;

import kasuga.lib.core.javascript.JavascriptContext;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import java.util.Optional;

public class KasugaModuleLoader implements ModuleLoader {

    CommonJSModuleLoader commonJSModuleLoader = new CommonJSModuleLoader();
    @Override
    public boolean isLoadable(String identifier) {
        return identifier.startsWith("kasuga_lib:");
    }

    @Override
    public Optional<Value> load(Context context, String module, Value requireFn, JavascriptContext javascriptContext) {
        ResourceLocation moduleTarget;
        try{
            ResourceLocation location = new ResourceLocation(module);
            moduleTarget = new ResourceLocation(location.getNamespace(),"js/modules/"+location.getPath());
        }catch (ResourceLocationException e){
            return Optional.empty();
        }

        return commonJSModuleLoader.load(context,moduleTarget.toString(),requireFn,javascriptContext);
    }
}
