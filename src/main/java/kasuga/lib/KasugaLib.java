package kasuga.lib;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import kasuga.lib.core.KasugaLibStacks;
import kasuga.lib.core.client.frontend.commands.FrontendCommands;
import kasuga.lib.core.client.frontend.gui.layout.yoga.api.YogaFileLocator;
import kasuga.lib.core.javascript.commands.JavascriptModuleCommands;
import kasuga.lib.core.packets.AllPackets;
import kasuga.lib.core.util.Envs;
import kasuga.lib.example_env.AllExampleElements;
import kasuga.lib.example_env.ExampleMain;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStreamReader;

@Mod(KasugaLib.MOD_ID)
public class KasugaLib {
    public static final String MOD_ID = "kasuga_lib";
    public static final Logger MAIN_LOGGER = createLogger("MAIN");
    public static IEventBus EVENTS = FMLJavaModLoadingContext.get().getModEventBus();

    public static final KasugaLibStacks STACKS = new KasugaLibStacks(EVENTS);
    public static final Gson GSON = new GsonBuilder().enableComplexMapKeySerialization().create();

    public KasugaLib() {
        EVENTS.register(this);
        AllPackets.init();
        // YogaExample.example();
        JavascriptModuleCommands.invoke();
        FrontendCommands.invoke();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT,()-> YogaFileLocator::configureLWJGLPath);
        KasugaLibConfig.invoke();
        if (Envs.isDevEnvironment())
            ExampleMain.invoke();
    }

    public static Logger createLogger(String name) {
        return LoggerFactory.getLogger(name);
    }
}