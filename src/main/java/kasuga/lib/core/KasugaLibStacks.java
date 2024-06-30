package kasuga.lib.core;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.addons.AddonFolderLoader;
import kasuga.lib.core.addons.AddonManager;
import kasuga.lib.core.addons.ResourceAddonLoader;
import kasuga.lib.core.base.CustomBlockRenderer;
import kasuga.lib.core.base.commands.ArgumentTypes.BaseArgument;
import kasuga.lib.core.base.commands.ArgumentTypes.BaseArgumentInfo;
import kasuga.lib.core.client.animation.Constants;
import kasuga.lib.core.client.frontend.gui.GuiEngine;
import kasuga.lib.core.events.both.BothSetupEvent;
import kasuga.lib.core.events.both.EntityAttributeEvent;
import kasuga.lib.core.events.client.*;
import kasuga.lib.core.events.server.ServerResourceListener;
import kasuga.lib.core.events.server.ServerStartingEvents;
import kasuga.lib.core.client.render.texture.SimpleTexture;
import kasuga.lib.core.javascript.JavascriptApi;
import kasuga.lib.core.util.Envs;
import kasuga.lib.registrations.registry.SimpleRegistry;
import kasuga.lib.registrations.registry.FontRegistry;
import kasuga.lib.registrations.registry.TextureRegistry;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Optional;

import static kasuga.lib.KasugaLib.MOD_ID;

public class KasugaLibStacks {
    private final HashMap<String, SimpleRegistry> registries;
    public final IEventBus bus;
    private final DeferredRegister<ArgumentTypeInfo<?, ?>> ARGUMENT_TYPES;
    private boolean hasTextureRegistryFired = false;
    private final TextureRegistry TEXTURES;
    private final FontRegistry FONTS;
    private final RandomSource random = RandomSource.create();
    private final HashMap<Block, CustomBlockRenderer> BLOCK_RENDERERS;
    public final JavascriptApi JAVASCRIPT = new JavascriptApi();

    public Optional<GuiEngine> GUI = Optional.empty();
    public final SimpleRegistry REGISTRY = new SimpleRegistry(KasugaLib.MOD_ID, KasugaLib.EVENTS);
    public final AddonFolderLoader SCRIPT_FOLDER_LOADER;
    public final ResourceAddonLoader SERVER_SCRIPT_PACK_LOADER;
    public final ResourceAddonLoader CLIENT_SCRIPT_PACK_LOADER;

    public final AddonManager SERVER_ADDON_MANAGER;
    public final AddonManager CLIENT_ADDON_MANAGER;


    public KasugaLibStacks(IEventBus bus) {
        this.bus = bus;
        this.registries = new HashMap<>();
        ARGUMENT_TYPES = DeferredRegister.create(ForgeRegistries.Keys.COMMAND_ARGUMENT_TYPES, MOD_ID);
        ARGUMENT_TYPES.register("base", () -> ArgumentTypeInfos.registerByClass(BaseArgument.class, new BaseArgumentInfo()));
        ARGUMENT_TYPES.register(bus);
        TEXTURES = new TextureRegistry(MOD_ID);
        FONTS = new FontRegistry(MOD_ID);
        BLOCK_RENDERERS = new HashMap<>();
        MinecraftForge.EVENT_BUS.addListener(ServerStartingEvents::serverStarting);
        MinecraftForge.EVENT_BUS.addListener(ServerStartingEvents::serverAboutToStart);
        MinecraftForge.EVENT_BUS.addListener(PacketEvent::onServerPayloadHandleEvent);
        bus.addListener(BothSetupEvent::onFMLCommonSetup);
        bus.addListener(EntityAttributeEvent::entityAttributeCreation);

        SCRIPT_FOLDER_LOADER = new AddonFolderLoader(FMLPaths.GAMEDIR.get().resolve("scripts").normalize());
        SERVER_SCRIPT_PACK_LOADER = new ResourceAddonLoader();
        CLIENT_SCRIPT_PACK_LOADER = new ResourceAddonLoader();
        SERVER_ADDON_MANAGER = new AddonManager();
        CLIENT_ADDON_MANAGER = new AddonManager();

        if(Envs.isClient()) {
            MinecraftForge.EVENT_BUS.addListener(PacketEvent::onClientPayloadHandleEvent);
            MinecraftForge.EVENT_BUS.addListener(Constants::onClientTick);
            MinecraftForge.EVENT_BUS.addListener(Constants::onAnimStart);
            MinecraftForge.EVENT_BUS.addListener(Constants::onAnimStop);
            bus.addListener(ModelRegistryEvent::registerAdditionalModels);
            bus.addListener(ModelRegistryEvent::bakingCompleted);
            bus.addListener(TextureRegistryEvent::onModelRegistry);
            bus.addListener(ClientSetupEvent::onClientSetup);
            MinecraftForge.EVENT_BUS.addListener(RenderTickEvent::onRenderTick);
            GUI = Optional.of(new GuiEngine());
        }

        MinecraftForge.EVENT_BUS.addListener(ServerResourceListener::onServerStarting);
        MinecraftForge.EVENT_BUS.addListener(ServerResourceListener::onServerStopping);
    }

    public void stackIn(SimpleRegistry registry) {
        this.registries.put(registry.namespace, registry);
    }

    public boolean isTextureRegistryFired() {
        return hasTextureRegistryFired;
    }

    public void fireTextureRegistry() {
        this.hasTextureRegistryFired = true;
        TEXTURES.onRegister();
    }

    public void cacheBlockRendererIn(Block block, CustomBlockRenderer blockRenderer) {
        BLOCK_RENDERERS.put(block, blockRenderer);
    }

    public CustomBlockRenderer getBlockRenderer(Block block) {
        return BLOCK_RENDERERS.getOrDefault(block, null);
    }

    public HashMap<String, SimpleRegistry> getRegistries() {
        return registries;
    }

    public void putUnregisteredPicIn(SimpleTexture pic) {
        this.TEXTURES.stackIn(pic);
    }

    public FontRegistry fontRegistry() {
        return FONTS;
    }

    public RandomSource random() {
        return random;
    }

    public void initJavascript(){
        JAVASCRIPT.init();
        GUI.ifPresent(GuiEngine::init);
    }
}
