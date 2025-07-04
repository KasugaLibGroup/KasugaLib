package kasuga.lib;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kasuga.lib.core.KasugaLibStacks;
import kasuga.lib.core.client.frontend.commands.FrontendCommands;
import kasuga.lib.core.client.frontend.gui.layout.LayoutEngines;
import kasuga.lib.core.client.frontend.webserver.GuiWebServerEndpoint;
import kasuga.lib.core.javascript.commands.JavascriptModuleCommands;
import kasuga.lib.core.packets.AllPackets;
import kasuga.lib.core.util.Envs;
import kasuga.lib.core.resource.KasugaPackResource;
import kasuga.lib.core.webserver.KasugaHttpServer;
import kasuga.lib.example_env.AllExampleElements;
import kasuga.lib.mixins.mixin.MultiPackResourceManagerAccessor;
import kasuga.lib.mixins.mixin.ReloadableResourceManagerAccessor;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.*;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.WorldData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(KasugaLib.MOD_ID)
public class KasugaLib {
    public static final String MOD_ID = "kasuga_lib";
    public static final Logger MAIN_LOGGER = createLogger("MAIN");
    public static IEventBus EVENTS = FMLJavaModLoadingContext.get().getModEventBus();

    public static final KasugaLibStacks STACKS = new KasugaLibStacks(EVENTS);
    public static final Gson GSON = new GsonBuilder().enableComplexMapKeySerialization().create();

    @Nullable
    @Getter
    public static MinecraftServer server = null;

    public KasugaLib() {
        EVENTS.register(this);
        AllPackets.init();
        // YogaExample.example();
        JavascriptModuleCommands.invoke();
        FrontendCommands.invoke();
        KasugaLibConfig.invoke();
        KasugaHttpServer.invoke();
        if (Envs.isDevEnvironment())
            AllExampleElements.invoke();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT,()-> LayoutEngines::init);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT,()-> GuiWebServerEndpoint::invoke);
    }

    public static Logger createLogger(String name) {
        return LoggerFactory.getLogger(name);
    }
}