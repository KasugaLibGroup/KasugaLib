package kasuga.lib.core.client.frontend.commands;

import com.mojang.blaze3d.systems.RenderSystem;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.base.commands.CommandHandler;
import kasuga.lib.core.javascript.JavascriptContext;
import kasuga.lib.core.javascript.JavascriptThread;
import kasuga.lib.registrations.common.CommandReg;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.graalvm.polyglot.Source;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

public class FrontendCommands {
    public static final SimpleRegistry REGISTRY = KasugaLib.STACKS.REGISTRY;

    protected static final CloseableHttpClient client = HttpClients.createDefault();
    public static final CommandReg GUI_DEBUG_COMMAND = new CommandReg("kasugalib")
            .addLiteral("gui", false)
            .addLiteral("debug", false)
            .addString("Bundle", false)
            .onlyIn(Dist.CLIENT)
            .setHandler(new CommandHandler(){
                @Override
                public void run() {
                    final String bundle = getParameter("Bundle", String.class);
                    JavascriptThread thread = KasugaLib.STACKS.JAVASCRIPT.GROUP_GUI.getOrCreate("DEBUG","Debugger thread");
                    thread.recordCall(()->{
                        try{
                            URI uri = new URI(bundle);
                            String serverName = uri.getHost();
                            boolean isSecureConnection = uri.getScheme() != null && uri.getScheme().equals("https");
                            if(serverName == null)
                                serverName = "localhost:8081";

                            JavascriptContext context = thread.createOrGetContext("debuuger::"+bundle,"Debugging thread "+bundle);
                            HttpGet bundleAccessor = new HttpGet(
                                    (isSecureConnection ? "https://" : "http://") +
                                            serverName + "/" + bundle + ".bundle?"+
                                            "platform=minecraft&dev=true&inlineSourceMap=false"+
                                            "&modulesOnly=false&runModule=true&hot=true"
                            );
                            CloseableHttpResponse response;
                            InputStream stream;
                            InputStreamReader reader;
                            response = client.execute(bundleAccessor);
                            stream = response.getEntity().getContent();
                            reader = new InputStreamReader(stream);

                            Source source = Source.newBuilder("js",reader,uri.toString()).build();
                            context.run(source);
                        }catch (RuntimeException | URISyntaxException | IOException e){
                            System.out.println("Error during debugging: "+e.getMessage());
                            thread.closeContext("debuuger::"+bundle);
                        }finally {
                        }
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
                    ResourceLocation id = getParameter("id", ResourceLocation.class);
                    RenderSystem.recordRenderCall(()->{
                        Minecraft.getInstance().setScreen(KasugaLib.STACKS.GUI.create(id).createScreen());
                    });
                }
            }).submit(REGISTRY);
    public static void invoke(){
        REGISTRY.submit();
    }
}
