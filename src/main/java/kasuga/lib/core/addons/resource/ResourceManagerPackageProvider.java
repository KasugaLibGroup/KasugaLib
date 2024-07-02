package kasuga.lib.core.addons.resource;

import kasuga.lib.core.addons.node.NodePackage;
import kasuga.lib.core.addons.node.NodePackageLoader;
import kasuga.lib.core.addons.node.PackageScanner;
import kasuga.lib.core.util.data_type.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.ArrayList;
import java.util.List;

public class ResourceManagerPackageProvider {
    public List<Pair<NodePackage,List<NodePackage>>> packages = new ArrayList<>();
    public ResourceManagerPackageProvider(ResourceManager resourceManager) {
        List<ResourceProvider> providers = ResourceAdapter.adapt(Minecraft.getInstance().getResourceManager().listPacks().toList());
        providers.forEach((provider)->{
            Pair<NodePackage,List<NodePackage>> result = PackageScanner.scan(provider);
            if(result != null){
                packages.add(result);
            }
        });
    }

    public void register(NodePackageLoader loader){
        ArrayList<NodePackage> nodePackages = new ArrayList<>();
        for(Pair<NodePackage,List<NodePackage>> aPackage : packages){
            loader.addPackage(aPackage.getFirst());
            List<NodePackage> workspacePackages = aPackage.getSecond();
            if(workspacePackages != null){
                workspacePackages.forEach(loader::addPackage);
                nodePackages.addAll(workspacePackages);
            }
        }
        loader.addPackages(nodePackages);
    }

    public void unregister(NodePackageLoader loader){
        for(Pair<NodePackage,List<NodePackage>> aPackage : packages){
            loader.removePackage(aPackage.getFirst());
            List<NodePackage> workspacePackages = aPackage.getSecond();
            if(workspacePackages != null){
                workspacePackages.forEach(loader::removePackage);
            }
        }
    }
}
