package kasuga.lib.core.webserver;

import io.javalin.Javalin;
import kasuga.lib.KasugaLib;
import kasuga.lib.KasugaLibConfig;
import kasuga.lib.core.base.commands.CommandHandler;
import kasuga.lib.registrations.common.CommandReg;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.slf4j.Logger;

public class KasugaHttpServer {

    public static Javalin SERVER = Javalin.create();

    public static Javalin CLIENT = Javalin.create();

    protected static Logger LOGGER = KasugaLib.createLogger("HttpServer");

    public static KasugaServerAuthenticationGateway SERVER_AUTH = new KasugaServerAuthenticationGateway();
    public static KasugaServerAuthenticationGateway CLIENT_AUTH = new KasugaServerAuthenticationGateway();

    protected static boolean serverHttpServerStart = false;
    protected static boolean clientHttpServerStart = false;

    public static void onServerStart(MinecraftServer server) {
        if(!KasugaLibConfig.CONFIG.getBoolValue("server_http_server_enabled")) {
            return;
        }

        if(DistExecutor.unsafeCallWhenOn(Dist.CLIENT,()->()->Boolean.valueOf(WebServerMinecraftClientUtils.isIntergratedServer(server)))) {
            return;
        }

        LOGGER.info("Starting HTTP server for server-side....");

        if(KasugaLibConfig.CONFIG.getBoolValue("server_http_server_listen_all_address")) {
            SERVER.start(KasugaLibConfig.CONFIG.getIntValue("server_http_server_port"));
        } else {
            SERVER.start("127.0.0.1", KasugaLibConfig.CONFIG.getIntValue("server_http_server_port"));
        }
        serverHttpServerStart = true;
    }

    public static void onServerStop() {
        serverHttpServerStart = false;
        SERVER.stop();

        LOGGER.info("Stopping HTTP server for server-side....");
    }

    public static void onClientStart() {
        if(!KasugaLibConfig.CONFIG.getBoolValue("client_http_server_enabled")) {
            return;
        }
        clientHttpServerStart = true;

        LOGGER.info("Starting HTTP server for client-side....");

        if(KasugaLibConfig.CONFIG.getBoolValue("client_http_server_listen_all_address")) {
            CLIENT.start(KasugaLibConfig.CONFIG.getIntValue("client_http_server_port"));
        } else {
            CLIENT.start("127.0.0.1", KasugaLibConfig.CONFIG.getIntValue("client_http_server_port"));
        }
    }

    public static void invoke() {
        SERVER_AUTH.registerRoutes(SERVER);
        CLIENT_AUTH.registerRoutes(CLIENT);
    }

    public static boolean isClientHttpServerStart() {
        return clientHttpServerStart;
    }

    public static boolean isServerHttpServerStart() {
        return serverHttpServerStart;
    }


    public static final CommandReg WEBUI_LOGIN_TOKEN = new CommandReg("kasugalib")
            .addLiteral("webui", false)
            .addLiteral("login", false)
            .addString("token", false)
            .setHandler(new CommandHandler() {
                @Override
                public void run() {
                    CommandSourceStack source = this.ctx.getSource();
                    if(!source.isPlayer())
                        return;
                    if(!KasugaHttpServer.isServerHttpServerStart())
                        return;
                    KasugaHttpServer
                            .SERVER_AUTH
                            .grant(
                                    this.ctx.getArgument("token", String.class),
                                    KasugaServerAuthenticationGateway.SessionInfo.forPlayer(source.getPlayer())
                            );
                }
            })
            .submit(KasugaLib.STACKS.REGISTRY);

    public static final CommandReg WEBUI_OPEN = new CommandReg("kasugalib")
            .addLiteral("webui", false)
            .addLiteral("open", false)
            .setHandler(new CommandHandler() {
                @Override
                public void run() {
                    CommandSourceStack source = this.ctx.getSource();
                    if(!source.isPlayer())
                        return;
                    KasugaAuthenticator.notifyOpen(source.getPlayer(), "/", KasugaAuthenticator.Direction.BOTH);
                }
            })
            .submit(KasugaLib.STACKS.REGISTRY);
}
