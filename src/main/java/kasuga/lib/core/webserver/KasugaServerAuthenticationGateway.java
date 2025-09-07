package kasuga.lib.core.webserver;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.websocket.WsConnectHandler;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.*;

public class KasugaServerAuthenticationGateway {


    public static interface AuthenticatedHandler {
        public void handle(Context handler, SessionInfo info);
    }

    public static class SessionInfo {
        boolean toLocalPlayer;
        @Nullable
        Player player;

        private Date cleanUpAt;

        public SessionInfo(boolean toLocalPlayer, @Nullable Player player) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.MINUTE,10);
            this.cleanUpAt = calendar.getTime();
            this.toLocalPlayer = toLocalPlayer;
            this.player = player;
        }
        public static SessionInfo local() {
            return new SessionInfo(true, null);
        }

        public static SessionInfo forPlayer(Player player) {
            return new SessionInfo(false, player);
        }

        public Player getPlayer() {
            return player;
        }

        public boolean isToLocalPlayer() {
            return toLocalPlayer;
        }

        public boolean expired() {
            return !cleanUpAt.after(new Date());
        }
    }

    public static class AuthenticationInfo extends SessionInfo {

        private Date expiresAt;
        public AuthenticationInfo(boolean toLocalPlayer, @Nullable Player player, Date expiresAt) {
            super(toLocalPlayer, player);
            this.expiresAt = expiresAt;
        }

        public static AuthenticationInfo fromAuthenticationInfo(SessionInfo info, Date expiresAt){
            return new AuthenticationInfo(info.isToLocalPlayer(), info.getPlayer(), expiresAt);
        }

        public boolean expired() {
            return !expiresAt.after(new Date());
        }
    }

    public static record ErrorDTO(
            int code,
            String error
    ){}

    public static record AuthenticationDTO(
            String sessionId
    ) {}

    public static record CreateTokenDTO(
            String token
    ) {}

    protected HashMap<String, AuthenticationInfo> authentications = new HashMap<>();
    protected HashMap<String, SessionInfo> sessions = new HashMap<>();
    protected String generateSecureString(int bytes){
        SecureRandom random = new SecureRandom();
        byte[] secureByte = random.generateSeed(bytes);
        return new BigInteger(1, secureByte).toString(36);
    }

    public String generateLoginToken() {
        return "login_" + generateSecureString(24);
    }

    public String generateCookieToken() {
        return generateSecureString(64);
    }

    public Handler withAuthentication(Handler inner) {
        return (ctx)->{
            SessionInfo info = authentications.get(ctx.cookie("kasugalib_session"));
            if(info == null) {
                String sessionId = ctx.header("Authorization");
                if(sessionId!=null)info = authentications.get(sessionId.replaceAll("Bearer ", ""));
            }
            if(info == null) {
                ctx.redirect("/login?redirect=" + ctx.path());
                return;
            }
            inner.handle(ctx);
        };
    }

    public Handler withAuthentication(AuthenticatedHandler inner) {
        return (ctx)->{
            SessionInfo info = sessions.get(ctx.cookieStore().get("kasugalib_session"));
            if(info == null) {
                info = authentications.get(ctx.header("Authorization").replaceAll("Bearer ", ""));
            }
            if(info == null) {
                ctx.redirect("/login?redirect=" + URLEncoder.encode(ctx.path(), StandardCharsets.UTF_8));
                return;
            }
            inner.handle(ctx, info);
        };
    }


    public void registerRoutes(Javalin server) {
        server.get("/api/auth", (ctx)->{

            String token = ctx.queryParam("token");

            if(token == null) {
                ctx.json(new CreateTokenDTO(this.generateLoginToken()));
                return;
            }

            if(!sessions.containsKey(token)) {
                ctx.status(401);
                ctx.json(new ErrorDTO(40100, "Authentication failed. Please retry later"));
                return;
            }

            String sessionId = generateCookieToken();

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.HOUR_OF_DAY, 1);
            Date expires = calendar.getTime();
            authentications.put(sessionId, AuthenticationInfo.fromAuthenticationInfo(sessions.get(token), expires));

            ctx.cookie("kasugalib_session", sessionId);

            sessions.remove(token);
        });

        server.get("/login", (ctx)->{
            ctx.html(new String(KasugaServerAuthenticationGateway.class.getResource("/templates/login.html").openStream().readAllBytes(), StandardCharsets.UTF_8));
        });
    }

    public void grant(String token, SessionInfo info) {
        this.sessions.put(token, info);
    }

    public void clean() {
        Iterator<AuthenticationInfo> authenticationsInfoIterator = this.authentications.values().iterator();
        while(authenticationsInfoIterator.hasNext()) {
            AuthenticationInfo info = authenticationsInfoIterator.next();
            if(info.expired()) {
                authenticationsInfoIterator.remove();
            }
        }

        Iterator<SessionInfo> sessionInfoIterator = this.sessions.values().iterator();
        while(sessionInfoIterator.hasNext()) {
            SessionInfo info = sessionInfoIterator.next();
            if(info.expired()) {
                sessionInfoIterator.remove();
            }
        }
    }

    public WsConnectHandler withWsAuthentication(WsConnectHandler handler) {
        return (ctx)->{
            if(ctx.queryParam("token") == null) {
                ctx.send(new Object(){
                    public String error = "Authentication failed. Please retry later";
                });
                ctx.closeSession();
                return;
            }
            handler.handleConnect(ctx);
        };
    }
}
