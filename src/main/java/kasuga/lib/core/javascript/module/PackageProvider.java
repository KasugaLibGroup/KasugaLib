package kasuga.lib.core.javascript.module;

import kasuga.lib.core.addons.node.NodePackage;
import kasuga.lib.core.util.data_type.Pair;

import java.util.List;

public interface PackageProvider {
    public List<Pair<NodePackage, String>> getPackages(String name);
}
