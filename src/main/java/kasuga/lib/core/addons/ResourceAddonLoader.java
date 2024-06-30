package kasuga.lib.core.addons;

import com.google.common.base.Splitter;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.addons.packagemanager.structure.PackageJsonFile;
import kasuga.lib.core.addons.resource.adapter.AllEntriesListProvider;
import kasuga.lib.core.addons.resource.adapter.QuickListProvider;
import kasuga.lib.core.addons.resource.adapter.RawResourceAdapter;
import kasuga.lib.core.addons.resource.types.PackResources;
import kasuga.lib.core.addons.resource.adapter.PackType;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ResourceAddonLoader {
    public static final PackType SCRIPT_TYPE = new PackType("script");
    public static final Splitter PATH_SPLITTER = Splitter.on(Pattern.compile("[/\\\\]"));
    Map<PackResources, List<PackageJsonFile>> map = new HashMap<>();

    public void add(List<PackResources> packResources){
        packResources.forEach(this::add);
    }

    public void add(PackResources packResources){
        List<PackageJsonFile> packages = scanPackets(packResources);
        map.put(packResources, packages);
    }

    public void remove(PackResources packResources){
        map.remove(packResources);
    }

    public List<PackageJsonFile> scanPackets(PackResources root){
        if(root instanceof AllEntriesListProvider provider){
            return scanPackets(provider);
        }else if(root instanceof QuickListProvider provider){
            return scanPackets(provider);
        }
        return Collections.emptyList();
    }

    private List<PackageJsonFile> scanPackets(QuickListProvider provider) {
        return scanPackets(provider, "/");
    }

    private List<PackageJsonFile> scanPackets(QuickListProvider provider, String path) {
        PackageJsonFile packageJsonFile = readPackageJson(provider, path + (path.endsWith("/") ? "" : "/") + "package.json");
        if(packageJsonFile == null)
            return Collections.emptyList();
        List<PackageJsonFile> result = new ArrayList<>();
        result.add(packageJsonFile);

        if(!path.equals("/"))
            return List.of(packageJsonFile);

        for(String workspace : packageJsonFile.workspaces){
            if(workspace.contains("*")){
                List<String> allDesiredWorkspaceEntries = findAllMatches(provider, workspace);
                for(String entry : allDesiredWorkspaceEntries){
                    result.addAll(scanPackets(provider, path + entry));
                }
            }else{
                result.addAll(scanPackets(provider, workspace));
            }
        }
        return result;
    }

    private List<String> findAllMatches(QuickListProvider provider, String workspace) {
        List<String> parts = PATH_SPLITTER.splitToList(workspace);
        if(parts.isEmpty())
            return Collections.emptyList();
        return findAllMatchesPart(provider, "/", parts);
    }

    private List<String> findAllMatchesPart(QuickListProvider provider, String path, List<String> parts) {
        Stream<String> fileList = provider.list(SCRIPT_TYPE, path);
        if(parts.size() == 1){
            return fileList.map((name)->path + (path.endsWith("/") ? "" : "/") + name)
                    .collect(Collectors.toList());
        }
        String currentPart = parts.get(0);
        if(Objects.equals(currentPart, "*")){
            List<String> result = new ArrayList<>();
            for(String entry : fileList.toList()){
                result.addAll(findAllMatchesPart(provider, entry, parts.subList(1, parts.size())));
            }
            fileList.close();
            return result;
        }else{
            if(fileList.anyMatch((entry)->entry.equals(currentPart))){
                return
                        findAllMatchesPart(provider, path + currentPart + "/", parts.subList(1, parts.size()))
                                .stream().map((name)->{
                                    return path + (path.endsWith("/") ? "" : "/") + name;
                                }).toList();
            }
            fileList.close();
            return Collections.emptyList();
        }
    }


    private List<PackageJsonFile> scanPackets(AllEntriesListProvider provider) {
        Set<String> allPackageJsons = provider
                .getAllEntriesStream()
                .filter(entry -> entry.endsWith("package.json"))
                .map(entry -> entry.substring(0, entry.length() - "package.json".length()))
                .collect(Collectors.toUnmodifiableSet());
        return scanPackets(provider,allPackageJsons, "/");
    }

    private List<PackageJsonFile> scanPackets(AllEntriesListProvider provider, Set<String> allPackageJsons, String currentEntry){
        String desiredEntry = currentEntry + (currentEntry.endsWith("/") ? "" : "/") + "package.json";
        PackageJsonFile packageJsonFile = readPackageJson(provider, desiredEntry);
        if(packageJsonFile == null)
            return Collections.emptyList();
        List<PackageJsonFile> result = new ArrayList<>();
        result.add(packageJsonFile);
        if(currentEntry.equals("/"))
            return List.of(packageJsonFile);

        for(String workspace : packageJsonFile.workspaces){
            if(workspace.contains("*")){
                if(workspace.startsWith("/")){
                    workspace = workspace.substring(1);
                }
                List<String> allDesiredWorkspaceEntries = findAllMatches(allPackageJsons, workspace);
                for(String entry : allDesiredWorkspaceEntries){
                    result.addAll(scanPackets(provider, allPackageJsons, entry));
                }
            }else{
                result.addAll(scanPackets(provider, allPackageJsons, workspace));
            }
        }

        return result;
    }

    private List<String> findAllMatches(Set<String> allPackageJsons, String globPattern) {
        // For glob pattern, we should find all matched entries
        return allPackageJsons
                .stream()
                .map((name)->name.startsWith("/") ? name.substring(1) : name)
                .filter((name)->{
                    int j = 0;
                    for(int i=0;i<name.length();i++){
                        if(j == globPattern.length()){
                            return false;
                        }
                        if(globPattern.charAt(j) == '*'){
                            if(j == globPattern.length() - 1){
                                return true;
                            }

                            if(name.charAt(i) != '/' && name.charAt(i) != '\\'){
                                continue;
                            }

                            j++;
                        }
                        if(name.charAt(i) == globPattern.charAt(j)) {
                            j++;
                        }
                    }
                    return false;
                })
                .toList();
    }

    private PackageJsonFile readPackageJson(RawResourceAdapter packResources, String entry){
        try{
            if(!packResources.hasResource(SCRIPT_TYPE, entry))
                return null;
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
        try(InputStream inputStream = packResources.getResource(SCRIPT_TYPE,entry)){
            Reader reader = new InputStreamReader(inputStream);
            JsonObject jsonObject = KasugaLib.GSON.fromJson(reader, JsonObject.class);
            return new PackageJsonFile(jsonObject, List.of());
        }catch (IOException | JsonSyntaxException e){
            e.printStackTrace();
            return null;
        }
    }

    public void clear() {
        map.clear();
    }
}
