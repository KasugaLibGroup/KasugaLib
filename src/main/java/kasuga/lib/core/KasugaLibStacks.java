package kasuga.lib.core;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.base.CustomBlockRenderer;
import kasuga.lib.core.client.animation.Constants;
import kasuga.lib.core.events.both.EntityAttributeEvent;
import kasuga.lib.core.events.client.PacketEvent;
import kasuga.lib.core.events.client.ClientSetupEvent;
import kasuga.lib.core.events.client.ModelRegistryEvent;
import kasuga.lib.core.events.client.TextureRegistryEvent;
import kasuga.lib.core.events.server.ServerStartingEvents;
import kasuga.lib.core.client.render.texture.SimpleTexture;
import kasuga.lib.core.util.Envs;
import kasuga.lib.registrations.registry.SimpleRegistry;
import kasuga.lib.registrations.registry.FontRegistry;
import kasuga.lib.registrations.registry.TextureRegistry;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.HashMap;
import java.util.HashSet;

public class KasugaLibStacks {
    private static final HashSet<String> registeredNamespaces = new HashSet<>();
    private final HashMap<String, SimpleRegistry> registries;
    public final IEventBus bus;
    private boolean hasTextureRegistryFired = false;
    private final TextureRegistry TEXTURES;
    private final FontRegistry FONTS;
    private final RandomSource random = RandomSource.create();
    private final HashMap<Block, CustomBlockRenderer> BLOCK_RENDERERS;
    public KasugaLibStacks(IEventBus bus) {
        this.bus = bus;
        this.registries = new HashMap<>();
        TEXTURES = new TextureRegistry(KasugaLib.MOD_ID);
        FONTS = new FontRegistry(KasugaLib.MOD_ID);
        BLOCK_RENDERERS = new HashMap<>();
        MinecraftForge.EVENT_BUS.addListener(ServerStartingEvents::serverStarting);
        MinecraftForge.EVENT_BUS.addListener(ServerStartingEvents::serverAboutToStart);
        MinecraftForge.EVENT_BUS.addListener(PacketEvent::onClientPayloadHandleEvent);
        MinecraftForge.EVENT_BUS.addListener(PacketEvent::onServerPayloadHandleEvent);
        MinecraftForge.EVENT_BUS.addListener(Constants::onClientTick);
        MinecraftForge.EVENT_BUS.addListener(Constants::onAnimStart);
        MinecraftForge.EVENT_BUS.addListener(Constants::onAnimStop);
        bus.addListener(EntityAttributeEvent::entityAttributeCreation);
        if(Envs.isClient()) {
            bus.addListener(ModelRegistryEvent::registerAdditionalModels);
            bus.addListener(ModelRegistryEvent::bakingCompleted);
            bus.addListener(TextureRegistryEvent::onModelRegistry);
            bus.addListener(ClientSetupEvent::onClientSetup);
        }
    }

    public static void registerNamespace(SimpleRegistry registry) {
        registeredNamespaces.add(registry.namespace);
    }

    public static HashSet<AtlasResources> getAdditionAtlasManager() {
        HashSet<AtlasResources> result = new HashSet<>();
        registeredNamespaces.forEach(namespace -> result.add(new AtlasResources(namespace)));
        return result;
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
