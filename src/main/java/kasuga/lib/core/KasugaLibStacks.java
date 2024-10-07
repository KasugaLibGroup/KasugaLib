package kasuga.lib.core;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.base.CustomBlockRenderer;
import kasuga.lib.core.base.commands.ArgumentTypes.BaseArgument;
import kasuga.lib.core.client.animation.Constants;
import kasuga.lib.core.client.frontend.gui.GuiEngine;
import kasuga.lib.core.events.both.BothSetupEvent;
import kasuga.lib.core.events.both.EntityAttributeEvent;
import kasuga.lib.core.events.client.*;
import kasuga.lib.core.events.server.ServerResourceListener;
import kasuga.lib.core.events.server.ServerStartingEvents;
import kasuga.lib.core.client.render.texture.old.SimpleTexture;
import kasuga.lib.core.javascript.JavascriptApi;
import kasuga.lib.core.util.Envs;
import kasuga.lib.registrations.create.TrackMaterialReg;
import kasuga.lib.registrations.client.KeyBindingReg;
import kasuga.lib.registrations.registry.SimpleRegistry;
import kasuga.lib.registrations.registry.FontRegistry;
import kasuga.lib.registrations.registry.TextureRegistry;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.HashMap;
import java.util.Random;
import java.util.Optional;

import static kasuga.lib.KasugaLib.MOD_ID;
import java.util.function.Supplier;

public class KasugaLibStacks {
    private final HashMap<String, SimpleRegistry> registries;
    public final IEventBus bus;
    private boolean hasTextureRegistryFired = false;
    private final TextureRegistry TEXTURES;
    private final FontRegistry FONTS;
    private final Random random = new Random();
    private final HashMap<Block, CustomBlockRenderer> BLOCK_RENDERERS;
    public final JavascriptApi JAVASCRIPT = new JavascriptApi();

    public Optional<GuiEngine> GUI = Optional.empty();
    public static final SimpleRegistry REGISTRY = new SimpleRegistry(KasugaLib.MOD_ID, KasugaLib.EVENTS);


    private final HashMap<TrackMaterial, TrackMaterialReg> TRACK_MATERIALS;
    public KasugaLibStacks(IEventBus bus) {
        this.bus = bus;
        this.registries = new HashMap<>();
        TEXTURES = new TextureRegistry(MOD_ID);
        FONTS = new FontRegistry(MOD_ID);
        TRACK_MATERIALS = new HashMap<>();
        BLOCK_RENDERERS = new HashMap<>();
        MinecraftForge.EVENT_BUS.addListener(ServerStartingEvents::serverStarting);
        MinecraftForge.EVENT_BUS.addListener(ServerStartingEvents::serverAboutToStart);
        MinecraftForge.EVENT_BUS.addListener(PacketEvent::onServerPayloadHandleEvent);
        bus.addListener(BothSetupEvent::onFMLCommonSetup);
        bus.addListener(EntityAttributeEvent::entityAttributeCreation);
        ArgumentTypes.register("base", BaseArgument.class, new BaseArgument.Serializer());
        KeyBindingReg.invoke();

        if(Envs.isClient()) {
            MinecraftForge.EVENT_BUS.addListener(PacketEvent::onClientPayloadHandleEvent);
            MinecraftForge.EVENT_BUS.addListener(Constants::onClientTick);
            MinecraftForge.EVENT_BUS.addListener(Constants::onAnimStart);
            MinecraftForge.EVENT_BUS.addListener(Constants::onAnimStop);
            MinecraftForge.EVENT_BUS.addListener(ClientTickEvent::onClientTick);
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

    public void cacheTrackMaterialIn(TrackMaterialReg reg) {
        TRACK_MATERIALS.put(reg.getMaterial(), reg);
    }

    public TrackMaterialReg getCachedTrackMaterial(TrackMaterial material) {
        return TRACK_MATERIALS.getOrDefault(material, null);
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

    public Random random() {
        return random;
    }
}
