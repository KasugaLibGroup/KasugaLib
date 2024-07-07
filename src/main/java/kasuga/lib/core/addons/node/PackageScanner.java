package kasuga.lib.core.addons.node;

import com.google.common.base.Splitter;
import com.google.gson.JsonObject;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.addons.resource.FlatFilesystem;
import kasuga.lib.core.addons.resource.HierarchicalFilesystem;
import kasuga.lib.core.addons.resource.ResourceProvider;
import kasuga.lib.core.util.data_type.Pair;
import kasuga.lib.core.util.glob.GlobMatcher;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Pattern;

public class PackageScanner {
    public static Splitter PATH_SPLITTER = Splitter
            .on(Pattern.compile("[\\\\/]"))
            .omitEmptyStrings()
            .trimResults();
    public static Pair<NodePackage,List<NodePackage>> scan(ResourceProvider provider){
        if(!provider.exists("/package.json")){
            return null;
        }
        List<NodePackage> result = new ArrayList<>();
        NodePackage rootPackage = null;
        try{
            InputStream stream = provider.open("/package.json");
            InputStreamReader reader = new InputStreamReader(stream);
            JsonObject sourceObject = KasugaLib.GSON.fromJson(reader, JsonObject.class);
            rootPackage = NodePackage.parse(sourceObject, new PackageReader("/", provider));
            if(rootPackage.workspaces != null) {
                List<List<String>> workspaces = new ArrayList<>();
                for (String workspace : rootPackage.workspaces) {
                    ArrayList<String> workspacePath = new ArrayList<>(splitPath(workspace));
                    workspacePath.add("package.json");
                    workspaces.add(workspacePath);
                }
                GlobMatcher matcher = new GlobMatcher(workspaces);
                result.addAll(match(matcher, provider));
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        if(rootPackage == null){
            return null;
        }

        return Pair.of(rootPackage,result);
    }

    private static List<NodePackage> match(GlobMatcher matcher, ResourceProvider provider) {
        if(provider instanceof HierarchicalFilesystem hierarchical){
            return matchHierarchical(matcher,hierarchical);
        }else if(provider instanceof FlatFilesystem flat){
            return matchFlat(matcher,flat);
        }
        throw new IllegalArgumentException("Unsupported provider type");
    }

    private static List<NodePackage> matchHierarchical(GlobMatcher matcher, HierarchicalFilesystem hierarchical) {
        List<List<String>> workspaces = matcher.match((pPath)->{
            try {
                return hierarchical.list(joinPath(pPath));
            } catch (IOException e) {
                return List.of();
            }
        },(scan)->true);
        return collect(hierarchical, workspaces);
    }

    public static List<NodePackage> matchFlat(GlobMatcher matcher, FlatFilesystem flat) {
        List<List<String>> workspaces = matcher.collect(flat.listEntries().map(PackageScanner::splitPath));
        return collect(flat, workspaces);
    }

    public static List<NodePackage> collect(ResourceProvider provider, List<List<String>> workspaces){
        List<NodePackage> result = new ArrayList<>();
        for(List<String> workspace : workspaces){
            String path = joinPath(workspace.subList(0, workspace.size()-1));
            try{
                InputStream stream = provider.open(path + "/package.json");
                InputStreamReader reader = new InputStreamReader(stream);
                JsonObject sourceObject = KasugaLib.GSON.fromJson(reader, JsonObject.class);
                NodePackage nodePackage =
                        NodePackage.parse(
                                sourceObject,
                                new PackageReader(
                                        path,
                                        provider
                                )
                        );
                result.add(nodePackage);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return result;
    }

    public static List<String> splitPath(String path){
        return PATH_SPLITTER.splitToList(path);
    }

    public static String joinPath(Collection<String> path){
        return String.join("/", path);
    }

    @SafeVarargs
    public static List<String> resolve(List<String> ...paths){
        ArrayList<String> result = new ArrayList<>();
        for (List<String> path : paths) {
            for (String pathItem : path) {
                if (Objects.equals(pathItem, "."))
                    continue;
                if(Objects.equals(pathItem, "..") && !result.isEmpty())
                    result.remove(result.size() - 1);
                else
                    result.add(pathItem);
            }
        }
        return result;
    }
}
