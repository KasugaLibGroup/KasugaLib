package kasuga.lib;

import kasuga.lib.core.config.SimpleConfig;

public class KasugaLibConfig {

    public static final SimpleConfig CONFIG = new SimpleConfig()
            .client("client_side_settings")
            .boolConfig("enable_animation_cache", true)
            .registerConfigs();

    public static void invoke(){}
}
