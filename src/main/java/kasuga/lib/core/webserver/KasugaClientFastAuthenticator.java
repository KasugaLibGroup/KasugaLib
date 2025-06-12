package kasuga.lib.core.webserver;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class KasugaClientFastAuthenticator {
    public static String grantAuthenticationToken(){
        if(!KasugaHttpServer.isClientHttpServerStart())
            return null;
        String token = KasugaHttpServer.CLIENT_AUTH.generateLoginToken();
        KasugaHttpServer.CLIENT_AUTH.grant(token, KasugaServerAuthenticationGateway.SessionInfo.local());
        return token;
    }

    public static String authenticate(String path){
        if(!KasugaHttpServer.isClientHttpServerStart())
            return null;
        int localPort = KasugaHttpServer.CLIENT.port();
        String token = grantAuthenticationToken();
        String url = String.format(
                "http://127.0.0.1:%d/login?token=%s&redirect=%s",
                localPort,
                token,
                URLEncoder.encode(path, StandardCharsets.UTF_8)
        );
        return url;
    }
}
