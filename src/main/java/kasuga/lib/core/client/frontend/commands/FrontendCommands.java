package kasuga.lib.core.client.frontend.commands;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.addons.node.NodePackage;
import kasuga.lib.core.base.commands.CommandHandler;
import kasuga.lib.core.javascript.JavascriptContext;
import kasuga.lib.core.javascript.JavascriptThread;
import kasuga.lib.registrations.common.CommandReg;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;

public class FrontendCommands {
    public static final SimpleRegistry REGISTRY = KasugaLib.STACKS.REGISTRY;

    protected static final CloseableHttpClient client = HttpClients.createDefault();

    public static final CommandReg GUI_DEBUG_COMMAND = new CommandReg("kasugalib")
            .addLiteral("gui", false)
            .addLiteral("debug", false)
            .onlyIn(Dist.CLIENT)
            .setHandler(new CommandHandler(){
                @Override
                public void run() {
                    MetroServerResourceProvider resourceProvider = new MetroServerResourceProvider();
                    UUID contextId = UUID.randomUUID();
                    JavascriptThread thread = MetroModuleLoader.getThread();
                    thread.recordCall(()->{
                        NodePackage nodePackage = null;
                        try(InputStream inputStream = resourceProvider.open("/package.json")){
                            InputStreamReader reader = new InputStreamReader(inputStream);
                            JsonObject json = KasugaLib.GSON.fromJson(reader, JsonObject.class);
                            nodePackage = NodePackage.parse(json, null);
                        }catch (IOException e){
                            return;
                        }

                        nodePackage.minecraft.clientDebuggerEntries().forEach((entry)->{
                            JavascriptContext context = thread.createContext(entry,"Debugger Context - "+entry);

                            MetroModuleInfo moduleInfo = new MetroModuleInfo(resourceProvider.getServerAddress(), resourceProvider);

                            MetroLoaderModule loaderModule = new MetroLoaderModule(moduleInfo);

                            String session = MetroModuleLoader.createSession(moduleInfo);

                            context.loadModuleVoid("metro-session:"+session+"/"+entry);
                        });

                    });

                }
            }).submit(REGISTRY);

    public static final CommandReg GUI_OPEN = new CommandReg("kasugalib")
            .addLiteral("gui", false)
            .addLiteral("open", false)
            .addResourceLocation("id", false)
            .onlyIn(Dist.CLIENT)
            .setHandler(new CommandHandler(){
                @Override
                public void run() {
                    //kasuga open <loc>
                    if(KasugaLib.STACKS.GUI.isEmpty()){
                        return;
                    }
                    ResourceLocation id = getParameter("id", ResourceLocation.class);
                    RenderSystem.recordRenderCall(()->{
                        Minecraft.getInstance().setScreen(KasugaLib.STACKS.GUI.get().create(id).createScreen());
                    });
                }
            }).submit(REGISTRY);
    public static void invoke(){
        REGISTRY.submit();
    }
}
