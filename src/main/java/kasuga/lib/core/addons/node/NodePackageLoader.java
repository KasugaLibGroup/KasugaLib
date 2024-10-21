package kasuga.lib.core.addons.node;

import kasuga.lib.core.addons.resource.HierarchicalFilesystem;
import kasuga.lib.core.javascript.JavascriptContext;
import kasuga.lib.core.javascript.JavascriptThread;
import kasuga.lib.core.javascript.JavascriptThreadGroup;
import kasuga.lib.core.util.glob.GlobMatcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NodePackageLoader {
    public HashMap<String, NodePackage> packages = new HashMap<>();
    private JavascriptThreadGroup group;
    private EntryType entryType;

    public NodePackageLoader(){}

    public void addPackage(NodePackage nodePackage){
        System.out.printf("Package discovered: %s\n",nodePackage.packageName);
        packages.put(nodePackage.packageName, nodePackage);
        group.getModuleLoader().registerPackage(nodePackage);
        createRuntime(nodePackage);
    }

    public void addPackages(List<NodePackage> nodePackages){
        for (NodePackage nodePackage : nodePackages) {
            System.out.printf("Package discovered: %s\n",nodePackage.packageName);
            packages.put(nodePackage.packageName, nodePackage);
            group.getModuleLoader().registerPackage(nodePackage);
        }
        for (NodePackage nodePackage : nodePackages) {
            createRuntime(nodePackage);
        }
    }

    public void removePackage(NodePackage nodePackage){
        packages.remove(nodePackage.packageName, nodePackage);
        group.getModuleLoader().unregisterPackage(nodePackage);
    }

    public void bindRuntime(JavascriptThreadGroup group, EntryType type) {
        this.group = group;
        this.entryType = type;
    }

    protected void createRuntime(NodePackage nodePackage){
        if(nodePackage.minecraft == null || group == null)
            return;
        switch (entryType){
            case CLIENT:
                createRuntimeForEntryType(nodePackage, nodePackage.minecraft.commonEntries());
                createRuntimeForEntryType(nodePackage, nodePackage.minecraft.clientEntries());
        }
    }

    protected void createRuntimeForEntryType(NodePackage nodePackage, List<String> entries){
        if(entries == null || entries.isEmpty())
            return;
        JavascriptThread thread = group.getOrCreate(nodePackage, "Package " + nodePackage.packageName);

        GlobMatcher matcher = new GlobMatcher(entries.stream().map(entry->PackageScanner.PATH_SPLITTER.splitToList(entry)).toList());

        ArrayList <String> entriesList = new ArrayList<>();
        if(nodePackage.reader.isHierarchical()){
            HierarchicalFilesystem hf = nodePackage.reader.asRawHierarchical();
            String path = nodePackage.reader.getPath();
            entriesList.addAll(matcher.match((p)->{
                String fullPath = path + "/" + PackageScanner.joinPath(p);
                try {
                    return hf.list(fullPath);
                } catch (IOException e) {
                    return List.of();
                }
            },(p)->true).stream().map(PackageScanner::joinPath).toList());
        }else if(nodePackage.reader.isFlat()){
            entriesList.addAll(
                    matcher.collect(
                            nodePackage
                                    .reader.asRawFlat().listEntries()
                                    .filter((p)->p.startsWith(nodePackage.reader.getPath()))
                                    .map((p)->p.substring(nodePackage.reader.getPath().length()))
                                    .map(PackageScanner::splitPath)
                            ).stream()
                            .map(PackageScanner::joinPath)
                            .toList()
            );
        }
        for (String entry : entriesList) {
            System.out.printf("Create entry \"%s\" for module %s\n",entry,nodePackage.packageName);
            JavascriptContext context = thread.createContext(entriesList, "Package " + nodePackage.packageName + " Entry " + entry);
            context.runTask(()->{
                context.loadModuleVoid(nodePackage.packageName + "/" + entry);
            });
        }
    }
}
