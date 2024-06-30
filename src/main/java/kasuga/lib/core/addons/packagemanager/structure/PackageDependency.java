package kasuga.lib.core.addons.packagemanager.structure;

public class PackageDependency {
    public String name;

    public String version;

    public static PackageDependency create(String name, String version){
        PackageDependency dependency = new PackageDependency();
        dependency.name = name;
        dependency.version = version;
        return dependency;
    }

    public boolean equals(Object obj){
        if(obj instanceof PackageDependency){
            PackageDependency other = (PackageDependency) obj;
            return name.equals(other.name) && version.equals(other.version);
        }
        return false;
    }

}
