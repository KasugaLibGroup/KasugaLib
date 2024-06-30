package kasuga.lib.core.addons;

import kasuga.lib.core.addons.packagemanager.structure.ResourceLoaderFunction;
import kasuga.lib.core.addons.resource.types.PackResources;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.io.InputStream;

public class ResourceAddonLoaderFunction implements ResourceLoaderFunction {
    private final PackResources resource;
    private final String namespace;
    private final String path;

    public ResourceAddonLoaderFunction(PackResources resources, String namespace, String path) {
        this.resource = resources;
        this.namespace = namespace;
        this.path = path;
    }

    @Override
    public boolean has(String path) {
        return resource.hasResource(ResourceAddonLoader.SCRIPT_TYPE, new ResourceLocation(namespace, path));
    }

    @Override
    public InputStream get(String path) throws IOException {
        return resource.getResource(ResourceAddonLoader.SCRIPT_TYPE, new ResourceLocation(namespace, path));
    }

    public String getPath(String subPath){
        return this.path + (subPath.startsWith("/") ? subPath : "/" + subPath);
    }
}
