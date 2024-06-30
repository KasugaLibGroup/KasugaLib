package kasuga.lib.core.addons.packagemanager.structure;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import kasuga.lib.core.addons.packagemanager.exceptions.PackageJsonParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class PackageJsonFile {

    public ResourceLoaderFunction loader;
    // Basic Information

    public String packageName;
    public String version;
    public String main;
    public List<PackageDependency> dependencies;
    public List<PackageDependency> devDependencies;
    public List<PackageDependency> peerDependencies;
    public List<PackageDependency> optionalDependencies;
    public List<PackageDependency> bundledDependencies;


    // Meta Information, do not affect runtime behavior

    public Boolean isPrivate;
    public String description;
    public String repository;
    public List<String> keywords;
    public String homepage;
    public String bugs;
    public String license;

    // Package.json Workspace Information

    public List<String> workspaces;

    // External Information

    public List<ExternalField> externalFields;


    JsonObject sourceObject;

    public PackageJsonFile(JsonObject sourceObject, List<Function<JsonObject,ExternalField>> externalFieldParsers) {
        this.sourceObject = sourceObject;
        this.dependencies = parseDependencyLike("dependencies", sourceObject);
        this.devDependencies = parseDependencyLike("devDependencies", sourceObject);
        this.peerDependencies = parseDependencyLike("peerDependencies", sourceObject);
        this.optionalDependencies = parseDependencyLike("optionalDependencies", sourceObject);
        this.bundledDependencies = parseDependencyLike("bundledDependencies", sourceObject);

        this.packageName = assertExistsAndGet(sourceObject, "name", JsonPrimitive::getAsString);
        this.version = assertExistsAndGet(sourceObject, "version", JsonPrimitive::getAsString);
        this.main = getOrDefault(sourceObject, "main", JsonPrimitive::getAsString, "");
        this.description = getOrDefault(sourceObject, "description", JsonPrimitive::getAsString, "");
        this.repository = getOrDefault(sourceObject, "repository", JsonPrimitive::getAsString, "");

        this.isPrivate = getOrDefault(sourceObject, "private", JsonPrimitive::getAsBoolean, false);
        this.keywords = parseArrayLike(sourceObject, "keywords", JsonElement::getAsString);
        this.homepage = getOrDefault(sourceObject, "homepage", JsonPrimitive::getAsString, "");
        this.bugs = getOrDefault(sourceObject, "bugs", JsonPrimitive::getAsString, "");
        this.license = getOrDefault(sourceObject, "license", JsonPrimitive::getAsString, "");


        // @todo: Author parse

        this.workspaces = parseArrayLike(sourceObject, "workspaces", JsonElement::getAsString);

        this.externalFields = new ArrayList<>();
    }

    public static List<PackageDependency> parseDependencyLike(String fieldName, JsonObject sourceObject){
        List<PackageDependency> dependencies = new ArrayList<>();
        if(sourceObject.has(fieldName)){
            JsonObject dependenciesObject = sourceObject.getAsJsonObject(fieldName);
            for(String dependencyName : dependenciesObject.keySet()){
                String version = dependenciesObject.get(dependencyName).getAsString();
                dependencies.add(PackageDependency.create(dependencyName, version));
            }
        }
        return dependencies;
    }

    public static <T> T assertExistsAndGet(JsonObject object, String fieldName, Function<JsonPrimitive,T> getter){
        if(object.has(fieldName)){
            try{
                return getter.apply(object.getAsJsonPrimitive(fieldName));
            }catch (UnsupportedOperationException e){
                throw new PackageJsonParseException("Field " + fieldName + " is not a primitive value.");
            }
        } else {
            throw new PackageJsonParseException("Field " + fieldName + " does not exist in package.json file.");
        }
    }

    public static <T> T getOrDefault(JsonObject object, String fieldName, Function<JsonPrimitive,T> getter, T defaultValue){
        if(object.has(fieldName)){
            try{
                return getter.apply(object.getAsJsonPrimitive(fieldName));
            }catch (UnsupportedOperationException e){
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    public static <T> List<T> parseArrayLike(JsonObject object, String fieldName, Function<JsonElement,T> getter){
        List<T> result = new ArrayList<>();
        if(object.has(fieldName)){
            try{
                for(JsonElement element : object.getAsJsonArray(fieldName)){
                    result.add(getter.apply(element));
                }
            }catch (UnsupportedOperationException e){
                throw new PackageJsonParseException("Field " + fieldName + " has format error.");
            }
        }
        return result;
    }

    public JsonObject getSourceObject() {
        return sourceObject;
    }

    public JsonObject serialize(){
        JsonObject object = new JsonObject();
        object.addProperty("name", packageName);
        object.addProperty("version", version);
        object.addProperty("main", main);
        object.addProperty("description", description);
        object.addProperty("repository", repository);
        object.addProperty("homepage", homepage);
        object.addProperty("bugs", bugs);
        object.addProperty("license", license);

        if(!dependencies.isEmpty()){
            JsonObject dependenciesObject = new JsonObject();
            for(PackageDependency dependency : dependencies){
                dependenciesObject.addProperty(dependency.name, dependency.version);
            }
            object.add("dependencies", dependenciesObject);
        }

        if(!devDependencies.isEmpty()){
            JsonObject devDependenciesObject = new JsonObject();
            for(PackageDependency dependency : devDependencies){
                devDependenciesObject.addProperty(dependency.name, dependency.version);
            }
            object.add("devDependencies", devDependenciesObject);
        }

        if(!peerDependencies.isEmpty()){
            JsonObject peerDependenciesObject = new JsonObject();
            for(PackageDependency dependency : peerDependencies){
                peerDependenciesObject.addProperty(dependency.name, dependency.version);
            }
            object.add("peerDependencies", peerDependenciesObject);
        }

        if(!optionalDependencies.isEmpty()){
            JsonObject optionalDependenciesObject = new JsonObject();
            for(PackageDependency dependency : optionalDependencies){
                optionalDependenciesObject.addProperty(dependency.name, dependency.version);
            }
            object.add("optionalDependencies", optionalDependenciesObject);
        }

        if(!bundledDependencies.isEmpty()){
            JsonObject bundledDependenciesObject = new JsonObject();
            for(PackageDependency dependency : bundledDependencies){
                bundledDependenciesObject.addProperty(dependency.name, dependency.version);
            }
            object.add("bundledDependencies", bundledDependenciesObject);
        }

        object.addProperty("private", isPrivate);

        if(!keywords.isEmpty()){
            JsonArray keywordsArray = new JsonArray();
            for(String keyword : keywords){
                keywordsArray.add(keyword);
            }
            object.add("keywords", keywordsArray);
        }

        if(!workspaces.isEmpty()){
            JsonArray workspacesArray = new JsonArray();
            for(String workspace : workspaces){
                workspacesArray.add(workspace);
            }
            object.add("workspaces", workspacesArray);
        }

        for(ExternalField externalField : externalFields){
            externalField.serialize(object);
        }

        return object;
    }

    public static PackageJsonFile parse(JsonObject object, List<Function<JsonObject,ExternalField>> externalFieldParsers){
        return new PackageJsonFile(object, externalFieldParsers);
    }

    public static PackageJsonFile parse(JsonObject object){
        return new PackageJsonFile(object, List.of());
    }
}
