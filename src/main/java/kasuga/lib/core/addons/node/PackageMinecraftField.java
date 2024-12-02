package kasuga.lib.core.addons.node;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;

public record PackageMinecraftField(
        List<String> clientEntries,
        List<String> serverEntries,
        List<String> commonEntries,
        List<String> initializationEntries,
        List<String> clientDebuggerEntries,
        String assetsFolder
) {

    public static PackageMinecraftField parse(JsonObject sourceObject) {
        return new PackageMinecraftField(
                NodePackage.optionalRead(sourceObject, "client", NodePackage::parseStringList),
                NodePackage.optionalRead(sourceObject, "server", NodePackage::parseStringList),
                NodePackage.optionalRead(sourceObject, "common", NodePackage::parseStringList),
                NodePackage.optionalRead(sourceObject, "init", NodePackage::parseStringList),
                NodePackage.optionalRead(sourceObject, "debug-client", NodePackage::parseStringList),
                NodePackage.optionalRead(sourceObject, "assets", JsonElement::getAsString)
        );
    }

    public static PackageMinecraftField empty(){
        return new PackageMinecraftField(null, null, null, null, null, null);
    }
}
