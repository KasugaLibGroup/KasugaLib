package kasuga.lib.core.javascript.engine;

import kasuga.lib.core.addons.node.NodePackage;
import kasuga.lib.core.addons.node.PackageScanner;
import kasuga.lib.core.util.data_type.Pair;

import java.util.ArrayList;
import java.util.List;

public class JavascriptModuleScope {
    public final List<NodePackage> packages;
    public final JavascriptModuleScope parent;

    public JavascriptModuleScope(){
        packages = new ArrayList<>();
        parent = null;
    }

    public JavascriptModuleScope(JavascriptModuleScope parent){
        packages = new ArrayList<>();
        this.parent = parent;
    }

    public List<Pair<NodePackage, String>> getPackage(String name) {
        List<String> parsedStrings = PackageScanner.splitPath(name);
        List<Pair<NodePackage, String>> result = new ArrayList<>();
        List<Pair<String, String>> mayPackageNames = new ArrayList<>();

        mayPackageNames.add(Pair.of(
                parsedStrings.get(0),
                PackageScanner.joinPath(parsedStrings.subList(1, parsedStrings.size())))
        );

        if(parsedStrings.size() > 1){
            mayPackageNames.add(Pair.of(
                    PackageScanner.joinPath(parsedStrings.subList(0, 2)),
                    PackageScanner.joinPath(parsedStrings.subList(2,parsedStrings.size()))
                )
            );
        }

        for (Pair<String, String> mayPackageName : mayPackageNames) {
            for (NodePackage nodePackage : packages) {
                if(nodePackage.packageName.equals(mayPackageName.getFirst())){
                    result.add(Pair.of(nodePackage, mayPackageName.getSecond()));
                }
            }
        }

        if(parent != null){
            result.addAll(
                    parent.getPackage(name)
            );
        }

        return result;
    }
}
