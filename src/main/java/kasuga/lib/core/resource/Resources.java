package kasuga.lib.core.resource;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.annos.Inner;
import kasuga.lib.core.annos.Util;
import kasuga.lib.core.util.Envs;
import kasuga.lib.mixins.mixin.MultiPackResourceManagerAccessor;
import kasuga.lib.mixins.mixin.ReloadableResourceManagerAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.WorldData;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

@Util
public class Resources {

    /**
     * Get resources with their path.
     * @param location The location of file or folder.
     * @param isFile is the location pointing at a file or a folder?
     * @return the map of resource path and the resource.
     * @throws IOException if the file/folder don't exist, throw this.
     */
    @Util
    public static Map<String, net.minecraft.server.packs.resources.Resource> getResources(ResourceLocation location, boolean isFile) throws IOException {
        ResourceManager rm = Minecraft.getInstance().getResourceManager();
        return innerGetResources(rm, location, isFile, false);
    }

    /**
     * Get resources with their full path. Full path means that the path contains the location path you gave.
     * @param location The location of file or folder.
     * @param isFile is the location pointing at a file or a folder?
     * @return the map of resource full path and the resource.
     * @throws IOException if the file/folder don't exist, throw this.
     */
    @Util
    public static Map<String, net.minecraft.server.packs.resources.Resource> getFullPathResources(ResourceLocation location, boolean isFile) throws IOException {
        ResourceManager rm = Minecraft.getInstance().getResourceManager();
        return innerGetResources(rm, location, isFile, true);
    }

    /**
     * Get single resource file from your asset or data.
     * @param location the file location.
     * @return the resource.
     * @throws IOException if the file don't exist, throw this.
     */
    @Util
    public static net.minecraft.server.packs.resources.Resource getResource(ResourceLocation location) throws IOException {
        ResourceManager rm = Minecraft.getInstance().getResourceManager();
        return rm.getResourceOrThrow(location);
    }

    @Util
    public static Optional<net.minecraft.server.packs.resources.Resource> attemptGetResource(ResourceLocation location) {
        ResourceManager rm = Minecraft.getInstance().getResourceManager();
        return rm.getResource(location);
    }

    /**
     * Get Block from its registration key.
     * @param location the block registration key (we usually use them in data-gen)
     * @return the block we got.
     */
    @Util
    public static Block getBlock(ResourceLocation location) {
        return ForgeRegistries.BLOCKS.getValue(location);
    }

    /**
     * Get item from its registration key.
     * @param location the item registration key (we usually use them in data-gen)
     * @return the item we got.
     */
    @Util
    public static Item getItem(ResourceLocation location) {
        return ForgeRegistries.ITEMS.getValue(location);
    }

    @Inner
    private static Map<String, net.minecraft.server.packs.resources.Resource> innerGetResources(ResourceManager rm, ResourceLocation location, boolean isFile, boolean fullyPath) throws IOException {
        if(isFile) {
            return Map.of(location.getPath(), rm.getResourceOrThrow(location));
        }
        Map<ResourceLocation, net.minecraft.server.packs.resources.Resource> resources = rm.listResources(location.getPath(),
                l -> l.getNamespace().equals(location.getNamespace()));
        HashMap<String, net.minecraft.server.packs.resources.Resource> result = new HashMap<>();
        for(ResourceLocation location1 : resources.keySet()) {
            if(fullyPath) {
                if (location1.getPath().contains("."))
                    result.put(location1.getPath(), resources.get(location1));
            } else {
                if (location1.getPath().contains("."))
                    result.put(location1.getPath().replaceAll(location.getPath() + "/", ""),
                            resources.get(location1));
            }
        }
        return result;
    }

    @Util
    public static List<PackResources> listPack(){
        ResourceManager rm = Minecraft.getInstance().getResourceManager();
        return rm.listPacks().toList();
    }

    @Util
    public static class CheatResourceLocation extends ResourceLocation {

        private boolean isOutside = true;

        protected CheatResourceLocation(String[] pDecomposedLocation) {
            super(pDecomposedLocation);
        }

        public CheatResourceLocation(String pLocation) {
            super(pLocation);
        }

        public CheatResourceLocation(String pNamespace, String pPath) {
            super(pNamespace, pPath);
        }

        public CheatResourceLocation(String namespace, String path, boolean isOutside) {
            super(namespace, path);
            this.isOutside = isOutside;
        }

        public static CheatResourceLocation copy(ResourceLocation location) {
            if (location instanceof CheatResourceLocation cheatResourceLocation)
                return cheatResourceLocation.clone();
            return new CheatResourceLocation(location.getNamespace(), location.getPath(), true);
        }

        public CheatResourceLocation clone() {
            return new CheatResourceLocation(namespace, path, isOutside);
        }

        public void setOutside(boolean outside) {
            isOutside = outside;
        }

        public boolean isOutside() {
            return isOutside;
        }
    }

    public static void registerResource(String namespace, String name, Consumer<KasugaPackResource> registerFunc) {
        HashMap<ResourceLocation, Stack<Consumer<KasugaPackResource>>> map =
                CustomResourceReloadListener.INSTANCE.getRegisterFunctions();
        ResourceLocation rl = new ResourceLocation(namespace, name);
        if (map.containsKey(rl)) {
            map.get(rl).push(registerFunc);
        } else {
            Stack<Consumer<KasugaPackResource>> stack = new Stack<>();
            stack.push(registerFunc);
            map.put(rl, stack);
        }
    }

    protected static KasugaPackResource internalRegisterCustomPack(PackType type, String namespace, String name) {
        if (type == PackType.CLIENT_RESOURCES && Envs.isClient()) {
            return registerCustomClientPack(namespace, name);
        } else if (type == PackType.SERVER_DATA && !Envs.isClient()) {
            return registerCustomServerPack(namespace, name);
        }
        return null;
    }

    protected static void updateCustomPack(KasugaPackResource resource) {
        if (Envs.isClient() && resource.getPackType() == PackType.CLIENT_RESOURCES) {
            updateCustomClientPack(resource);
        } else if (!Envs.isClient() && resource.getPackType() == PackType.SERVER_DATA) {
            updateCustomServerPack(resource);
        }
    }

    private static void updateCustomClientPack(KasugaPackResource resource) {
        MultiPackResourceManagerAccessor mprmAccessor = getMPRMClient();
        if (mprmAccessor == null) return;
        internalUpdatePack(mprmAccessor, resource);
    }

    private static void updateCustomServerPack(KasugaPackResource resource) {
        MultiPackResourceManagerAccessor mprmAccessor = getMPRMServer();
        if (mprmAccessor == null) return;
        internalUpdatePack(mprmAccessor, resource);
    }

    private static void internalUpdatePack(MultiPackResourceManagerAccessor manager,
                                             KasugaPackResource resources) {
        Collection<String> namespaces = resources.getNamespaces(resources.getPackType());
        Map<String, FallbackResourceManager> fallbackMap = manager.getNamespacedManagers();
        for (String space : namespaces) {
            FallbackResourceManager fallback = fallbackMap.getOrDefault(space, null);
            if (fallback == null) {
                fallback = new FallbackResourceManager(PackType.CLIENT_RESOURCES, space);
                fallbackMap.put(space, fallback);
            }
            fallback.push(resources);
        }
    }

    public static void registerCustomPack(PackType type, String namespace, String name) {
        if (type == PackType.CLIENT_RESOURCES && Envs.isClient()) {
            ResourceLocation location = new ResourceLocation(namespace, name);
            CustomResourceReloadListener.INSTANCE.getPacks()
                    .put(location, PackType.CLIENT_RESOURCES);
        } else if (type == PackType.SERVER_DATA && !Envs.isClient()) {
            ResourceLocation location = new ResourceLocation(namespace, name);
            CustomResourceReloadListener.INSTANCE.getPacks()
                    .put(location, PackType.SERVER_DATA);
        }
    }

    private static KasugaPackResource registerCustomClientPack(String namespace, String name) {
        MultiPackResourceManagerAccessor mprmAccessor = getMPRMClient();
        if (mprmAccessor == null) {return null;}
        return internalRegisterPack(mprmAccessor, namespace, name);
    }

    /**
     * for the manager type {@code MultiPackResourcePack}, see
     * {@link Minecraft#resourceManager}(line 474),
     * {@link ReloadableResourceManager#ReloadableResourceManager(PackType)}
     */
    public static MultiPackResourceManagerAccessor getMPRMClient() {
        if (!Envs.isClient()) return null;
        ResourceManager m = Minecraft.getInstance().getResourceManager();
        if (!(m instanceof ReloadableResourceManager rrm)) return null;
        ReloadableResourceManagerAccessor rrmAccessor = (ReloadableResourceManagerAccessor) rrm;
        MultiPackResourceManager mprm = (MultiPackResourceManager) rrmAccessor.getResources();
        return  (MultiPackResourceManagerAccessor) mprm;
    }


    private static KasugaPackResource registerCustomServerPack(String namespace, String name) {
        MultiPackResourceManagerAccessor mprmAccessor = getMPRMServer();
        if (mprmAccessor == null) {return null;}
        return internalRegisterPack(mprmAccessor, namespace, name);
    }

    /**
     * for the manager type {@code MultiPackResourceManager}, see
     * {@link MinecraftServer#getServerResources()},
     * {@link net.minecraft.server.WorldStem#WorldStem(CloseableResourceManager, ReloadableServerResources, RegistryAccess.Frozen, WorldData)},
     * {@link net.minecraft.client.gui.screens.worldselection.WorldOpenFlows#createLevelFromExistingSettings(LevelStorageSource.LevelStorageAccess, ReloadableServerResources, RegistryAccess.Frozen, WorldData)},
     * {@link WorldLoader.PackConfig#createResourceManager()}
     */
    public static MultiPackResourceManagerAccessor getMPRMServer() {
        if (Envs.isClient() || KasugaLib.server == null) return null;
        MinecraftServer.ReloadableResources resources = KasugaLib.server.getServerResources();
        MultiPackResourceManager mprm = (MultiPackResourceManager) resources.resourceManager();
        return (MultiPackResourceManagerAccessor) mprm;
    }

    private static KasugaPackResource internalRegisterPack(MultiPackResourceManagerAccessor manager, String namespace, String name) {
        Map<String, FallbackResourceManager> fallbackMap = manager.getNamespacedManagers();
        FallbackResourceManager fallback = fallbackMap.getOrDefault(namespace, null);
        if (fallback == null) {
            fallback = new FallbackResourceManager(PackType.CLIENT_RESOURCES, namespace);
            manager.getNamespacedManagers().put(namespace, fallback);
        }
        KasugaPackResource resource = new KasugaPackResource(PackType.CLIENT_RESOURCES, name, namespace);
        fallback.push(resource);
        return resource;
    }
}
