package kasuga.lib;

import kasuga.lib.core.config.SimpleConfig;

public class KasugaLibConfig {

    public static final SimpleConfig CONFIG = new SimpleConfig()
            .client("client_side_settings")
            .boolConfig("enable_animation_cache", false)
            .boolConfig("client_http_server_enabled", true)
            .boolConfig("client_http_server_listen_all_address", false)
            .intConfig("client_http_server_port", 15140)

            .server("server_side_settings")
            .boolConfig("server_http_server_enabled", false)
            .boolConfig("server_http_server_listen_all_address", true)
            .intConfig("server_http_server_port", 15141)
            .stringConfig("server_http_server_address", "http://your-server-ip:15141")

            .registerConfigs();

    public static void invoke(){}
}
