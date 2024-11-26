package kasuga.lib.core.javascript.commands;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.addons.node.NodePackage;
import kasuga.lib.core.addons.node.PackageMinecraftField;
import kasuga.lib.core.base.commands.CommandHandler;
import kasuga.lib.core.javascript.JavascriptContext;
import kasuga.lib.core.javascript.JavascriptThread;
import kasuga.lib.core.javascript.engine.JavascriptEngineModule;
import kasuga.lib.registrations.common.ArgumentTypeReg;
import kasuga.lib.registrations.common.CommandReg;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.UUID;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.io.IOException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

@SuppressWarnings("Unused")
public class JavascriptModuleCommands {
    private static final HashMap<UUID, String> serverUrls = new HashMap<>();
    private static final CloseableHttpClient client = HttpClients.createDefault();

    public static final SimpleRegistry REGISTRY = new SimpleRegistry(KasugaLib.MOD_ID, KasugaLib.EVENTS);

    public static final ArgumentTypeReg type = ArgumentTypeReg.INSTANCE.registerType(File.class, File::new)
            .submit(REGISTRY);

    public static final NodePackage PACKAGE =
            new NodePackage(
                    "@kasugalib/debugging",
                    "1.0.0",
                    "",
                    List.of(),
                    null,
                    PackageMinecraftField.empty()
            );

    public static final CommandReg JS_OPEN = new CommandReg("kasugalib")
            .addLiteral("js", false)
            .addLiteral("debug", false)
            .addString("server", false)
            .setHandler(new CommandHandler(){
                @Override
                public void run() {
                    String serverUrl = getParameter("server", String.class);
                    try {
                        HttpGet request = new HttpGet(serverUrl);
                        CloseableHttpResponse response = client.execute(request);
                        
                        if (response.getStatusLine().getStatusCode() == 200) {
                            String jsContent = EntityUtils.toString(response.getEntity());
                            JavascriptThread thread = KasugaLib
                                    .STACKS
                                    .JAVASCRIPT
                                    .GROUP_SERVER
                                    .getOrCreate(JavascriptModuleCommands.class, "Server Script Debugger");

                            thread.recordCall(() -> {
                                // 创建新的Context
                                UUID contextId = UUID.randomUUID();
                                JavascriptContext context = thread.createContext(
                                         contextId,
                                        "Debugger Context - " + contextId
                                );

                                serverUrls.put(contextId, serverUrl);

                                InputStream stringInput = new ByteArrayInputStream(jsContent.getBytes());
                                JavascriptEngineModule module = context.getRuntimeContext().compileModuleFromSource(
                                        PACKAGE,
                                        contextId.toString() + ".js",
                                        ".",
                                        stringInput
                                );
                                context.getRuntimeContext().loadModule(module);

                                this.ctx.getSource().sendSystemMessage(
                                        Component.literal("[KasugaLib] Debugging session started  ")
                                                .append(Component.literal("[CLOSE]")
                                                        .withStyle(style ->
                                                                style.withColor(ChatFormatting.RED)
                                                                        .withClickEvent(
                                                                                new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/kasugalib js close "+ contextId.toString()))
                                                )));
                            });
                        }
                        response.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).submit(REGISTRY);

    public static final CommandReg JS_CLOSE = new CommandReg("kasugalib")
            .addLiteral("js", false)
            .addLiteral("close", false)
            .addString("context", false)
            .setHandler(new CommandHandler(){
                @Override
                public void run() {
                    String contextId = getParameter("context", String.class);
                    UUID uuid = UUID.fromString(contextId);
                    JavascriptThread thread = KasugaLib
                            .STACKS
                            .JAVASCRIPT
                            .GROUP_SERVER
                            .getOrCreate(JavascriptModuleCommands.class, "Server Script Debugger");
                    thread.closeContext(uuid);
                    serverUrls.remove(uuid);
                }
            }).submit(REGISTRY);


    public static void invoke(){
        REGISTRY.submit();
    }
}
