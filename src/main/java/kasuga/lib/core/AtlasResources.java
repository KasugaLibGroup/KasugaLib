package kasuga.lib.core;

import kasuga.lib.core.resource.Resources;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AtlasResources {

    String namespace;
    HashMap<ResourceLocation, Resource> resourcesMap;

    public AtlasResources(String namespace) {
        this.namespace = namespace;
        resourcesMap = new HashMap<>();
    }
    public void runLoadingResources() throws IOException {
        Map<String, Resource> cache = Resources.getResources(new ResourceLocation(namespace, "textures"), false);
        cache.forEach((path, resource) -> {
            if (!path.startsWith(namespace + ":" + "textures/block") && !path.startsWith(namespace + ":" + "textures/item")) {
                resourcesMap.put(
                        new ResourceLocation(namespace, path.replaceFirst(namespace + ":" + "textures/", "")
                                .replace(".png", "")), resource);
            }
        });
    }

    public void registerAtlas(SpriteSource.Output output) {
        resourcesMap.forEach(output::add);
    }
}
