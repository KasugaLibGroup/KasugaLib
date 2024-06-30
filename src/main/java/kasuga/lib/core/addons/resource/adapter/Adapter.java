package kasuga.lib.core.addons.resource.adapter;

import kasuga.lib.core.addons.resource.types.FolderPackResources;
import kasuga.lib.core.addons.resource.types.PathPackResources;
import kasuga.lib.mixins.mixin.resources.DelegatingPackResourcesMixin;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackResources;
import net.minecraftforge.resource.DelegatingPackResources;

import java.util.ArrayList;
import java.util.List;

public class Adapter {
    public static List<PackResources> flatResources(List<PackResources> root){
        ArrayList<net.minecraft.server.packs.PackResources> resources = new ArrayList<>();
        for (PackResources packResources : root) {
            if(packResources instanceof DelegatingPackResources delegatingPackResources){
                resources.addAll(flatResources(((DelegatingPackResourcesMixin) delegatingPackResources).getDelegates()));
            }else{
                resources.add(packResources);
            }
        }
        return resources;
    }

    public static List<kasuga.lib.core.addons.resource.types.PackResources> transform(
            List<net.minecraft.server.packs.PackResources> list
    ){
        ArrayList<kasuga.lib.core.addons.resource.types.PackResources> resources = new ArrayList<>();
        for (net.minecraft.server.packs.PackResources packResources : list) {
            if(packResources instanceof FilePackResources fpr){
                resources.add(kasuga.lib.core.addons.resource.types.FilePackResources.adapt(fpr));
            }else if(packResources instanceof net.minecraft.server.packs.FolderPackResources fpr){
                resources.add(FolderPackResources.adapt(fpr));
            }else if(packResources instanceof net.minecraftforge.resource.PathPackResources ppr){
                resources.add(PathPackResources.adapt(ppr));
            }
        }
        return resources;
    }

    public static List<kasuga.lib.core.addons.resource.types.PackResources> adapt(List<PackResources> packResources){
        return transform(flatResources(packResources));
    }
}
