package kasuga.lib.registrations.client;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.client.render.model.MultiPartModel;
import kasuga.lib.core.client.render.model.SimpleModel;
import kasuga.lib.core.util.Resources;
import kasuga.lib.registrations.Reg;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ModelReg extends Reg {

    public static final String COMBINE_CODEC = "multi_part";
    SimpleModel baked;
    private final HashMap<String, ResourceLocation> mappingForBones;
    private final ResourceLocation location;
    boolean multiPart = false;
    public ModelReg(String registrationKey, ResourceLocation location) {
        super(registrationKey);
        this.location = location;
        baked = new SimpleModel(registrationKey, null);
        mappingForBones = new HashMap<>();
    }

    public String getIdentifier() {
        return "model";
    }

    public ResourceLocation location() {
        return location;
    }

    @Override
    public ModelReg submit(SimpleRegistry registry) {
        registry.model().register(this);
        return this;
    }

    public void compileFile(SimpleRegistry registry) throws IOException {
        ResourceLocation location = new ResourceLocation(registry.namespace, "models/" + this.location.getPath() + ".json");
        Resource resource = getResource(location);
        if(resource == null) return;
        JsonObject json = JsonParser.parseReader(Resources.openAsJson(resource)).getAsJsonObject();
        if(!json.has(COMBINE_CODEC)) return;
        multiPart = true;
        JsonObject members = json.getAsJsonObject(COMBINE_CODEC);
        ArrayList<Map.Entry<String, ResourceLocation>> entries = getMapFromJson(members, "");
        for(int i = 0; i < entries.size(); i++) {
            Map.Entry<String, ResourceLocation> entry = entries.get(i);
            ResourceLocation location1 = getModelLocation(entry.getValue());
            JsonObject obj = presentIfIsMulti(location1);
            if(obj != null) {
                entries.addAll(getMapFromJson(obj, entry.getKey()));
                entries.remove(entry);
                i -- ;
            }
        }
        for(Map.Entry<String, ResourceLocation> entry : entries) {mappingForBones.put(entry.getKey(), entry.getValue());}
    }

    private ResourceLocation getModelLocation(ResourceLocation location) {
        return new ResourceLocation(location.getNamespace(), "models/" + location.getPath() + ".json");
    }

    public static ArrayList<Map.Entry<String, ResourceLocation>> getMapFromJson(JsonObject members, String prefix) {
        String former = prefix.equals("") ? prefix : (prefix + ".");
        Type type = new TypeToken<Map<String, String>>() {}.getType();
        Map<String, String> m = KasugaLib.GSON.fromJson(members, type);
        ArrayList<Map.Entry<String, ResourceLocation>> entries = new ArrayList<>();
        for(String key : m.keySet()) {entries.add(new AbstractMap.SimpleEntry<>(former + key, new ResourceLocation(m.get(key))));}
        return entries;
    }

    private Resource getResource(ResourceLocation location) throws IOException {
        Map<String, Resource> map = Resources.getResources(location, true);
        Resource resource = null;
        if(!map.isEmpty()) {for(String s : map.keySet()) {resource = map.get(s);}}
        return resource;
    }

    private JsonObject presentIfIsMulti(ResourceLocation location) throws IOException {
        Resource resource = getResource(location);
        HashMap<String, ResourceLocation> result = new HashMap<>();
        if(resource == null) return null;
        JsonObject json = JsonParser.parseReader(Resources.openAsJson(resource)).getAsJsonObject();
        if(!json.has(COMBINE_CODEC)) return null;
        return json.getAsJsonObject(COMBINE_CODEC);
    }

    public boolean isMultiPart() {
        return multiPart;
    }

    public HashMap<String, ResourceLocation> getMappings() {
        return mappingForBones;
    }

    public void rebuildAsMultiPart() {
        this.baked = new MultiPartModel(registrationKey);
    }

    public void putModelIn(SimpleModel model) {
        this.baked.copyModelFrom(model);
    }

    public SimpleModel getModel() {
        return baked;
    }
}
