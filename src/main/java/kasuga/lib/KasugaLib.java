package kasuga.lib;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kasuga.lib.core.KasugaLibStacks;
import kasuga.lib.core.client.gui.components.Components;
import kasuga.lib.core.client.gui.layout.yoga.YogaFileLocator;
import kasuga.lib.core.client.gui.style.Styles;
import kasuga.lib.core.packets.AllPackets;
import kasuga.lib.core.util.Envs;
import kasuga.lib.example_env.AllExampleElements;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.sensing.WardenEntitySensor;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
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
        EVENTS.register(this);
        AllExampleElements.invoke();
        AllPackets.init();
        YogaFileLocator.configureLWJGLPath();
        // YogaExample.example();
        Styles.init();
        Components.init();
        if (Envs.isDevEnvironment())
            AllExampleElements.invoke();
    }

    public static Logger createLogger(String name) {
        return LoggerFactory.getLogger(name);
    }
}