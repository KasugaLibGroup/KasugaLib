package kasuga.lib.core.javascript.loader;

import com.google.gson.JsonObject;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.javascript.JavascriptContext;
import kasuga.lib.core.javascript.JavascriptThread;
import kasuga.lib.core.javascript.JavascriptThreadGroup;
import kasuga.lib.core.util.Resources;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.common.util.Lazy;
import org.graalvm.polyglot.Source;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.*;

public class JavascriptResourceEntryLoader {
    public static final Logger LOGGER = KasugaLib.createLogger("JAVASCRIPT/LOADER");
    private final String entry;
    JavascriptThreadGroup threadGroup;
    String prefix;
    ArrayList<JavascriptThread> threads = new ArrayList<>();
    HashMap<String,Integer> resourcePackIdMapper = new HashMap<>();
    public JavascriptResourceEntryLoader(JavascriptThreadGroup threadGroup, String entryFileName){
        this.entry = entryFileName;
        this.threadGroup = threadGroup;
    }
    public void start(){
        List<Resource> resources = Minecraft.getInstance()
                .getResourceManager()
                .getResourceStack(new ResourceLocation("kasuga_lib", entry));

        int index = 0;

        for (Resource resource : resources) {
            try {
                JsonObject object = KasugaLib.GSON.fromJson(resource.openAsReader(), JsonObject.class);
                if(!object.has("entry"))
                    return;
                String resourceString = object.get("entry").getAsString();
                ResourceLocation resourceLocation = ResourceLocation.tryParse(resourceString);
                if(resourceLocation == null)
                    return;
                JavascriptThread thread = threadGroup.getOrCreate(resource.sourcePackId(),"Resource");
                JavascriptContext javascriptContext = thread.createOrGetContext(resource,"Namespace "+ resource);
                javascriptContext.requireExternal(String.valueOf(resourceLocation));
                javascriptContext.getLoaderContext().setLoadPriority(index++);
                threads.add(thread);
                LOGGER.info("Load "+ resourceLocation.toString() + " on resource pack" + resource.sourcePackId() + "successful!");
            } catch (IOException|RuntimeException e) {
                LOGGER.error("Load javascript resource for " + resource.sourcePackId() + " failed !",e);
            }
        }
    }

    public void stop(){
        for (JavascriptThread thread : this.threads) {
            thread.interrupt();
        }
        threads.clear();
        resourcePackIdMapper.clear();
    }

    public void reload(){
        this.stop();
        this.start();
    }
}
