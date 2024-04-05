package kasuga.lib.core.client;

import kasuga.lib.core.annos.Inner;
import kasuga.lib.core.util.Resources;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.IOUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

@Inner
public class ModelMappings {
    public static final String IDENTIFIER = "blockstates";
    private final String namespace;
    private boolean map_finished = false;
    HashMap<ResourceLocation, ResourceLocation> map = new HashMap<>();

    public ModelMappings(String namespace) {
        this.namespace = namespace;
    }

    public String namespace() {
        return namespace;
    }

    public void map() throws IOException {
        Map<String, Resource> resources = Resources.getFullPathResources(new ResourceLocation(namespace, IDENTIFIER), false);
        for(String name : resources.keySet()) {
            if(!name.endsWith(".json") || !name.startsWith(IDENTIFIER)) continue;
            String plainName = IDENTIFIER + name.substring(name.lastIndexOf("/"));
            map.put(new ResourceLocation(namespace, plainName), new ResourceLocation(namespace, name));
        }
        map_finished = true;
    }

    public void addMapping(ResourceLocation location0, ResourceLocation location1) {
        ResourceLocation location = new ResourceLocation(location1.getNamespace(), location1.getPath());
        map.put(location0, location);
    }

    public boolean isMapFinished() {
        return map_finished;
    }

    public ResourceLocation getMappings(ResourceLocation location) {
        return map.getOrDefault(location, location);
    }

    public boolean containsMapping(ResourceLocation location) {
        return map.containsKey(location);
    }
}
