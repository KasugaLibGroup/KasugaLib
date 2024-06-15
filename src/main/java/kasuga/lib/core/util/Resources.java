package kasuga.lib.core.util;

import com.google.gson.stream.JsonReader;
import kasuga.lib.KasugaLib;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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
            return Map.of(location.getPath(), rm.getResource(location));
        }
        Collection<ResourceLocation> resources = rm.listResources(location.getPath(), l -> true);
        HashMap<String, net.minecraft.server.packs.resources.Resource> result = new HashMap<>();
        for(ResourceLocation location1 : resources) {
            if(!location.getPath().contains(".") || !location.getNamespace().equals(location.getNamespace())) continue;
            if(fullyPath)
                if(location1.getPath().contains("."))
                    result.put(location1.getPath(), getResource(location1));
            else
                if(location1.getPath().contains("."))
                    result.put(location1.getPath().replaceAll(location.getPath() + "/", ""),
                            getResource(location1));
        }
        return result;
    }

    public static net.minecraft.server.packs.resources.Resource getResource(ResourceLocation location) throws IOException {
        ResourceManager rm = Minecraft.getInstance().getResourceManager();
        return rm.getResource(location);
    }

    public static Block getBlock(ResourceLocation location) {
        return ForgeRegistries.BLOCKS.getValue(location);
    }

    public static Item getItem(ResourceLocation location) {
        return ForgeRegistries.ITEMS.getValue(location);
    }

    public static JsonReader openAsJson(Resource resource) {return new JsonReader(new InputStreamReader(resource.getInputStream()));}
}
