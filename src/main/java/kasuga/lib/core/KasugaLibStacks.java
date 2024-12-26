package kasuga.lib.core;

import com.simibubi.create.content.trains.track.TrackMaterial;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.base.CustomBlockRenderer;
import kasuga.lib.core.base.commands.ArgumentTypes.BaseArgument;
import kasuga.lib.core.base.commands.ArgumentTypes.BaseArgumentInfo;
import kasuga.lib.core.channel.ChannelNetworkManager;
import kasuga.lib.core.channel.network.NetworkManager;
import kasuga.lib.core.channel.network.address.NetworkAddressTypes;
import kasuga.lib.core.channel.packets.ChannelNetworkPacket;
import kasuga.lib.core.channel.test.ChannelTest;
import kasuga.lib.core.client.animation.Constants;
import kasuga.lib.core.client.frontend.gui.GuiEngine;
import kasuga.lib.core.client.model.ModelPreloadManager;
import kasuga.lib.core.create.graph.RailwayManager;
import kasuga.lib.core.events.both.BothSetupEvent;
import kasuga.lib.core.events.both.EntityAttributeEvent;
import kasuga.lib.core.events.client.*;
import kasuga.lib.core.events.server.ServerConnectionListeners;
import kasuga.lib.core.events.server.ServerLevelEvents;
import kasuga.lib.core.events.server.ServerResourceListener;
import kasuga.lib.core.events.server.ServerStartingEvents;
import kasuga.lib.core.client.render.texture.old.SimpleTexture;
import kasuga.lib.core.events.server.ServerTickEvent;
import kasuga.lib.core.javascript.JavascriptApi;
import kasuga.lib.core.menu.GuiMenuManager;
import kasuga.lib.core.menu.locator.ServerChunkMenuLocatorManager;
import kasuga.lib.core.menu.targets.TargetsClient;
import kasuga.lib.core.util.Envs;
import kasuga.lib.registrations.client.KeyBindingReg;
import kasuga.lib.registrations.common.FluidReg;
import kasuga.lib.registrations.create.TrackMaterialReg;
import kasuga.lib.registrations.registry.SimpleRegistry;
import kasuga.lib.registrations.registry.FontRegistry;
import kasuga.lib.registrations.registry.TextureRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.function.Supplier;
import java.util.HashSet;
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
    public static final HashMap<FluidReg<?>, String> FLUID_RENDERS = new HashMap<>();
    private final HashMap<TrackMaterial, TrackMaterialReg> TRACK_MATERIALS;
    public final JavascriptApi JAVASCRIPT = new JavascriptApi();

    public Optional<GuiEngine> GUI = Optional.empty();

    public static final GuiMenuManager MENU = new GuiMenuManager();

    public static final SimpleRegistry REGISTRY = new SimpleRegistry(KasugaLib.MOD_ID, KasugaLib.EVENTS);
    public static final ChannelNetworkManager CHANNEL = new ChannelNetworkManager();
    public static HashSet<Minecraft> mcs = new HashSet<>();

    public final RailwayManager RAILWAY = RailwayManager.createServer();

    public KasugaLibStacks(IEventBus bus) {
        this.bus = bus;
        this.registries = new HashMap<>();
        TEXTURES = new TextureRegistry(KasugaLib.MOD_ID);
        FONTS = new FontRegistry(KasugaLib.MOD_ID);
        TRACK_MATERIALS = new HashMap<>();
        KeyBindingReg.invoke();
        ARGUMENT_TYPES = DeferredRegister.create(ForgeRegistries.Keys.COMMAND_ARGUMENT_TYPES, MOD_ID);
        ARGUMENT_TYPES.register("base", () -> ArgumentTypeInfos.registerByClass(BaseArgument.class, new BaseArgumentInfo()));
        ARGUMENT_TYPES.register(bus);
        BLOCK_RENDERERS = new HashMap<>();
        MENU.init();

        MinecraftForge.EVENT_BUS.addListener(ServerStartingEvents::serverStarting);
        MinecraftForge.EVENT_BUS.addListener(ServerStartingEvents::serverAboutToStart);
        MinecraftForge.EVENT_BUS.addListener(PacketEvent::onServerPayloadHandleEvent);
        MinecraftForge.EVENT_BUS.addListener(ServerChunkMenuLocatorManager::onWatch);
        MinecraftForge.EVENT_BUS.addListener(ServerChunkMenuLocatorManager::onUnWatch);
        bus.addListener(BothSetupEvent::onFMLCommonSetup);
        bus.addListener(EntityAttributeEvent::entityAttributeCreation);
        MinecraftForge.EVENT_BUS.addListener(ServerTickEvent::onServerTick);

        MinecraftForge.EVENT_BUS.addListener(ServerLevelEvents::onLevelLoad);
        MinecraftForge.EVENT_BUS.addListener(ServerLevelEvents::onLevelSave);
        MinecraftForge.EVENT_BUS.addListener(ServerLevelEvents::onLevelExit);


        if(Envs.isClient()) {
            MinecraftForge.EVENT_BUS.addListener(PacketEvent::onClientPayloadHandleEvent);
            MinecraftForge.EVENT_BUS.addListener(Constants::onClientTick);
            MinecraftForge.EVENT_BUS.addListener(Constants::onAnimStart);
            MinecraftForge.EVENT_BUS.addListener(Constants::onAnimStop);
            MinecraftForge.EVENT_BUS.addListener(ClientTickEvent::onClientTick);
            MinecraftForge.EVENT_BUS.addListener(ClientTickEvent::onGuiTick);

            MinecraftForge.EVENT_BUS.addListener(PlayLogEvent::playerLogout);
            MinecraftForge.EVENT_BUS.addListener(PlayLogEvent::playerLogin);
            MinecraftForge.EVENT_BUS.addListener(ClientConnection::onClientConnect);
            MinecraftForge.EVENT_BUS.addListener(ClientConnection::onClientDisconnect);

            bus.addListener(ModelRegistryEvent::registerAdditionalModels);
            bus.addListener(ModelRegistryEvent::registerStaticImages);
            bus.addListener(ModelRegistryEvent::bakingCompleted);
            bus.addListener(TextureRegistryEvent::onModelRegistry);
            bus.addListener(ClientSetupEvent::onClientSetup);
            MinecraftForge.EVENT_BUS.addListener(RenderTickEvent::onRenderTick);
            MinecraftForge.EVENT_BUS.addListener(InteractionFovEvent::onComputedFov);
            bus.addListener(GeometryEvent::registerGeometry);
            bus.addListener(GeometryEvent::registerReloadListener);
            bus.addListener(BothSetupEvent::RegisterKeyEvent);
            GUI = Optional.of(new GuiEngine());
            MENU.initClient();
            bus.addListener(AnimationModelRegistryEvent::registerAnimations);
            if (Envs.isDevEnvironment()) KasugaLibClient.invoke();
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, ()-> TargetsClient::register);
            // bus.addListener(REGISTRY::hookFluidAndRenders);
            bus.addListener(ModelPreloadManager.INSTANCE::registerPreloadedModel);
        }

        MinecraftForge.EVENT_BUS.addListener(ServerResourceListener::onServerStarting);
        MinecraftForge.EVENT_BUS.addListener(ServerResourceListener::onServerStopping);
        MinecraftForge.EVENT_BUS.addListener(ServerConnectionListeners::onClientDisconnect);
        ChannelNetworkPacket.invoke();
        NetworkAddressTypes.invoke();
        ChannelTest.invoke();
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

    public void cacheTrackMaterialIn(TrackMaterialReg reg) {
        TRACK_MATERIALS.put(reg.getMaterial(), reg);
    }

    public TrackMaterialReg getCachedTrackMaterial(TrackMaterial material) {
        return TRACK_MATERIALS.getOrDefault(material, null);
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
}
