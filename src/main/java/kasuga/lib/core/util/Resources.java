package kasuga.lib.core.util;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.annos.Inner;
import kasuga.lib.core.annos.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
}
