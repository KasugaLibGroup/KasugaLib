package kasuga.lib.core.addons.resource;

import kasuga.lib.core.util.Envs;
import kasuga.lib.mixins.mixin.resources.DelegatingResourcePackMixin;
import kasuga.lib.mixins.mixin.resources.FilePackResourceMixin;
import net.minecraft.client.resources.DefaultClientPackResources;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.FolderPackResources;
import net.minecraft.server.packs.PackResources;
import net.minecraftforge.resource.DelegatingResourcePack;
import net.minecraftforge.resource.PathResourcePack;
import net.minecraft.server.packs.VanillaPackResources;

import java.util.ArrayList;
import java.util.List;

public class ResourceAdapter {
    public static List<PackResources> flatten(List<PackResources> resources){
        List<PackResources> result = new ArrayList<>();
        for (PackResources resource : resources) {
            if(resource instanceof DelegatingResourcePack delegating){
                result.addAll(flatten(((DelegatingResourcePackMixin) delegating).getDelegates()));
            }else{
                result.add(resource);
            }
        }
        return result;
    }

    public static List<ResourceProvider> transform(List<PackResources> resources) {
        List<ResourceProvider> result = new ArrayList<>();
        for (PackResources resource : resources) {
            if(resource instanceof FilePackResources file){
                result.add(new VanillaFileResourcePackProvider(((FilePackResourceMixin) file).invokeGetOrCreateZipFile()));
            }else if(resource instanceof PathResourcePack path){
                result.add(new VanillaPathResourcePackProvider(path.getSource(),path));
            }else if(resource instanceof FolderPackResources folder){
                result.add(new VanillaFolderResourcePackProvider(folder.file));
            }else if(resource instanceof VanillaPackResources){
                continue;
            }else{
                if(Envs.isDevEnvironment()){
                    throw new IllegalArgumentException("Unknown resource type: " + resource.getClass());
                }
                System.err.println("Unknown resource type: " + resource.getClass());
            }
        }
        return result;
    }

    public static List<ResourceProvider> adapt(List<PackResources> resources){
        for (PackResources packResources : flatten(resources)) {
            System.out.printf("Asset pack: "+packResources.getName() + " - "+packResources.getClass().getName() + "\n");
        }
        return transform(flatten(resources));
    }
}
