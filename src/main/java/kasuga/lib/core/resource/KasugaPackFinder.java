package kasuga.lib.core.resource;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.KasugaLibClient;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.resources.Resource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class KasugaPackFinder {

    static final KasugaPackFinder INSTANCE = new KasugaPackFinder();

    private final HashMap<String, Pack> packs;
    private final HashMap<String, PackBuilder> packBuilders;
    private final HashMap<String, KasugaPackResource> ksgResources;

    static {
        PackBuilder ksgInternalBuilder = new PackBuilder(KasugaLibClient.INTERNAL_TEXTURE_PACK,
                Component.translatable("internal.kasuga_lib.texture_pack"),
                Component.translatable("internal.kasuga_lib.texture_pack.desc"),
                9, PackType.CLIENT_RESOURCES)
                .addNamespace(KasugaLib.MOD_ID);
        getInstance().addPackBuilder(ksgInternalBuilder);
    }

    public static KasugaPackFinder getInstance() {
        return INSTANCE;
    }

    private KasugaPackFinder() {
        packs = new HashMap<>();
        ksgResources = new HashMap<>();
        packBuilders = new HashMap<>();
    }

    public boolean hasPack(String packName) {
        return packs.containsKey(packName);
    }

    public void addPackBuilder(PackBuilder packBuilder) {
        packBuilders.put(packBuilder.getId(), packBuilder);
    }

    public Set<String> listPacks() {
        return packs.keySet();
    }

    public @Nullable Pack getPack(String packName) {
        return packs.getOrDefault(packName, null);
    }

    public boolean hasKsgResource(String resourceName) {
        return ksgResources.containsKey(resourceName);
    }

    public Set<String> listKsgResources() {
        return ksgResources.keySet();
    }

    public @Nullable KasugaPackResource getKsgResource(String resourceName) {
        return ksgResources.getOrDefault(resourceName, null);
    }

    public boolean registerResource(String resourceName, ResourceLocation location, InputStream stream) throws IOException {
        KasugaPackResource resource = getKsgResource(resourceName);
        if (resource == null) return false;
        return resource.registerResource(location, stream);
    }

    public boolean registerResource(String resourceName, ResourceLocation location, byte[] bytes) throws IOException {
        KasugaPackResource resource = getKsgResource(resourceName);
        if (resource == null) return false;
        return resource.registerResource(location, bytes);
    }

    public boolean registerResource(String resourceName, ResourceLocation location, Resource resource) throws IOException {
        KasugaPackResource resources = getKsgResource(resourceName);
        if (resources == null) return false;
        return resources.registerResource(location, resource);
    }

    public boolean registerResource(String resourceName, ResourceLocation location, File file) throws IOException {
        KasugaPackResource resource = getKsgResource(resourceName);
        if (resource == null) return false;
        return resource.registerResource(location, file);
    }

    @SubscribeEvent
    public void post(AddPackFindersEvent event) {
        packs.clear();
        ksgResources.clear();
        PackType type = event.getPackType();
        event.addRepositorySource((consumer, constructor) -> {
            for (Map.Entry<String, PackBuilder> entry : packBuilders.entrySet()) {
                PackBuilder builder = entry.getValue();
                if (!builder.getDists().contains(type)) continue;
                Pack pack = builder.create(constructor);
                consumer.accept(pack);
                if (pack.open() instanceof KasugaPackResource ksgPack) {
                    ksgResources.put(entry.getKey(), ksgPack);
                }
                packs.put(entry.getKey(), pack);
            }
        });
    }

}
