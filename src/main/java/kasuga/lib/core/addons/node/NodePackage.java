package kasuga.lib.core.addons.node;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class NodePackage {
    public PackageReader reader;
    public String packageName;
    public String version;
    public String main;
    public List<String> workspaces;
    public final PackageMinecraftField minecraft;

    public NodePackage(String packageName, String version, String main, List<String> workspaces, PackageReader reader, PackageMinecraftField minecraft) {
        this.packageName = packageName;
        this.version = version;
        this.main = main;
        this.workspaces = workspaces;
        this.reader = reader;
        this.minecraft = minecraft;
    }

    public static NodePackage parse(JsonObject sourceObject, PackageReader reader) {
        String packageName = sourceObject.get("name").getAsString();
        String version = sourceObject.get("version").getAsString();
        String main = optionalRead(sourceObject, "main", JsonElement::getAsString);
        List<String> workspaces = new ArrayList<>();
        if(sourceObject.has("workspaces")){
            for (JsonElement workspace : sourceObject.getAsJsonArray("workspaces")) {
                workspaces.add(workspace.getAsString());
            }
        }
        PackageMinecraftField minecraft = null;
        if(sourceObject.has("minecraft"))
            minecraft = PackageMinecraftField.parse(sourceObject.getAsJsonObject("minecraft"));
        return new NodePackage(packageName, version, main, workspaces, reader, minecraft);
    }

    public static <T> T optionalRead(JsonObject sourceObject, String key, Function<JsonElement,T> reader){
        if(sourceObject.has(key)){
            return reader.apply(sourceObject.get(key));
        }
        return null;
    }

    public static List<String> parseStringList(JsonElement jsonElement) {
        List<String> list = new ArrayList<>();
        for (JsonElement element : jsonElement.getAsJsonArray()) {
            list.add(element.getAsString());
        }
        return list;
    }
}