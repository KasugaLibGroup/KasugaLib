package kasuga.lib.core;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.base.CustomBlockRenderer;
import kasuga.lib.core.base.commands.ArgumentTypes.BaseArgument;
import kasuga.lib.core.base.commands.ArgumentTypes.BaseArgumentInfo;
import kasuga.lib.core.channel.ChannelNetworkManager;
import kasuga.lib.core.channel.network.NetworkManager;
import kasuga.lib.core.channel.test.ChannelTest;
import kasuga.lib.core.client.animation.Constants;
import kasuga.lib.core.client.frontend.gui.GuiEngine;
import kasuga.lib.core.events.both.BothSetupEvent;
import kasuga.lib.core.events.both.EntityAttributeEvent;
import kasuga.lib.core.events.client.*;
import kasuga.lib.core.events.server.ServerConnectionListeners;
import kasuga.lib.core.events.server.ServerResourceListener;
import kasuga.lib.core.events.server.ServerStartingEvents;
import kasuga.lib.core.client.render.texture.old.SimpleTexture;
import kasuga.lib.core.javascript.JavascriptApi;
import kasuga.lib.core.menu.targets.TargetsClient;
import kasuga.lib.core.util.Envs;
import kasuga.lib.registrations.client.KeyBindingReg;
import kasuga.lib.registrations.registry.SimpleRegistry;
import kasuga.lib.registrations.registry.FontRegistry;
import kasuga.lib.registrations.registry.TextureRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
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

    public final JavascriptApi JAVASCRIPT = new JavascriptApi();

    public Optional<GuiEngine> GUI = Optional.empty();
    public static final SimpleRegistry REGISTRY = new SimpleRegistry(KasugaLib.MOD_ID, KasugaLib.EVENTS);
    public static final ChannelNetworkManager CHANNEL = new ChannelNetworkManager();
    public static HashSet<Minecraft> mcs = new HashSet<>();

    public KasugaLibStacks(IEventBus bus) {
        this.bus = bus;
        this.registries = new HashMap<>();
        KeyBindingReg.invoke();
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


        if(Envs.isClient()) {
            MinecraftForge.EVENT_BUS.addListener(PacketEvent::onClientPayloadHandleEvent);
            MinecraftForge.EVENT_BUS.addListener(Constants::onClientTick);
            MinecraftForge.EVENT_BUS.addListener(Constants::onAnimStart);
            MinecraftForge.EVENT_BUS.addListener(Constants::onAnimStop);
            MinecraftForge.EVENT_BUS.addListener(ClientTickEvent::onClientTick);
            MinecraftForge.EVENT_BUS.addListener(ClientTickEvent::onGuiTick);

            MinecraftForge.EVENT_BUS.addListener(PlayLogEvent::playerLogout);
            MinecraftForge.EVENT_BUS.addListener(PlayLogEvent::playerLogin);

            bus.addListener(ModelRegistryEvent::registerAdditionalModels);
            bus.addListener(ModelRegistryEvent::registerStaticImages);
            bus.addListener(ModelRegistryEvent::bakingCompleted);
            bus.addListener(TextureRegistryEvent::onModelRegistry);
            bus.addListener(ClientSetupEvent::onClientSetup);
            MinecraftForge.EVENT_BUS.addListener(RenderTickEvent::onRenderTick);
            bus.addListener(GeometryEvent::registerGeometry);
            bus.addListener(GeometryEvent::registerReloadListener);
            bus.addListener(BothSetupEvent::RegisterKeyEvent);
            GUI = Optional.of(new GuiEngine());
            bus.addListener(AnimationModelRegistryEvent::registerAnimations);
            if (Envs.isDevEnvironment()) KasugaLibClient.invoke();
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, ()-> TargetsClient::reigster);
        }

        MinecraftForge.EVENT_BUS.addListener(ServerResourceListener::onServerStarting);
        MinecraftForge.EVENT_BUS.addListener(ServerResourceListener::onServerStopping);
        MinecraftForge.EVENT_BUS.addListener(ServerConnectionListeners::onClientDisconnect);
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
