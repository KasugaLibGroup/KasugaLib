package kasuga.lib.core.util;

import kasuga.lib.KasugaLib;
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

public class Resources {

    public static Map<String, net.minecraft.server.packs.resources.Resource> getResources(ResourceLocation location, boolean isFile) throws IOException {
        ResourceManager rm = Minecraft.getInstance().getResourceManager();
        return innerGetResources(rm, location, isFile, false);
    }

    public static Map<String, net.minecraft.server.packs.resources.Resource> getFullPathResources(ResourceLocation location, boolean isFile) throws IOException {
        ResourceManager rm = Minecraft.getInstance().getResourceManager();
        return innerGetResources(rm, location, isFile, true);
    }

    public static Map<String, net.minecraft.server.packs.resources.Resource> innerGetResources(ResourceManager rm, ResourceLocation location, boolean isFile, boolean fullyPath) throws IOException {
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

    public static net.minecraft.server.packs.resources.Resource getResource(ResourceLocation location) throws IOException {
        ResourceManager rm = Minecraft.getInstance().getResourceManager();
        return rm.getResourceOrThrow(location);
    }

    public static Block getBlock(ResourceLocation location) {
        return ForgeRegistries.BLOCKS.getValue(location);
    }

    public static Item getItem(ResourceLocation location) {
        return ForgeRegistries.ITEMS.getValue(location);
    }
}
