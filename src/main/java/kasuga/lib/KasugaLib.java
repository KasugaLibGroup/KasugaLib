package kasuga.lib;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kasuga.lib.core.KasugaLibStacks;
import kasuga.lib.core.client.frontend.commands.FrontendCommands;
import kasuga.lib.core.client.frontend.gui.layout.yoga.api.YogaFileLocator;
import kasuga.lib.core.javascript.commands.JavascriptModuleCommands;
import kasuga.lib.core.packets.AllPackets;
import kasuga.lib.core.util.Envs;
import kasuga.lib.example_env.AllExampleElements;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(KasugaLib.MOD_ID)
public class KasugaLib {
    public static final String MOD_ID = "kasuga_lib";
    public static final Logger MAIN_LOGGER = createLogger("MAIN");
    public static IEventBus EVENTS = FMLJavaModLoadingContext.get().getModEventBus();

    public static final KasugaLibStacks STACKS = new KasugaLibStacks(EVENTS);
    public static final Gson GSON = new GsonBuilder().enableComplexMapKeySerialization().create();

    public KasugaLib() {
        E2EIntergration.invoke();
        EVENTS.register(this);
        AllPackets.init();
        // YogaExample.example();
        JavascriptModuleCommands.invoke();
        FrontendCommands.invoke();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT,()-> YogaFileLocator::configureLWJGLPath);
        KasugaLibConfig.invoke();
        if (Envs.isDevEnvironment())
            AllExampleElements.invoke();

    }

    public static Logger createLogger(String name) {
        return LoggerFactory.getLogger(name);
    }
}