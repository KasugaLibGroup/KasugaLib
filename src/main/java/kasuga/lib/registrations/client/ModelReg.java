package kasuga.lib.registrations.client;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.annos.Inner;
import kasuga.lib.core.annos.Mandatory;
import kasuga.lib.core.client.render.model.MultiPartModel;
import kasuga.lib.core.client.render.model.SimpleModel;
import kasuga.lib.core.resource.Resources;
import kasuga.lib.registrations.Reg;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A reg for KasugaLib style model registration. By using this, you could get a KasugaLib type model.
 * For more info about models, see {@link SimpleModel} and {@link MultiPartModel}, these models could
 * have their own data-gen animations, for more info about animations, see {@link kasuga.lib.core.client.animation.data.Animation}
 */
public class ModelReg extends Reg {
    public static final String COMBINE_CODEC = "multi_part";
    SimpleModel baked;
    private final HashMap<String, ResourceLocation> mappingForBones;
    private final ResourceLocation location;
    boolean multiPart = false;

    /**
     * Use this to create a KasugaLib style model registration.
     * @param registrationKey the name of your model reg.
     * @param location the location of your model. the root of your location is "namespace:models/" folder
     */
    public ModelReg(String registrationKey, ResourceLocation location) {
        super(registrationKey);
        this.location = location;
        baked = new SimpleModel(registrationKey, null);
        mappingForBones = new HashMap<>();
    }


    @Override
    @Mandatory
    public ModelReg submit(SimpleRegistry registry) {
        registry.model().register(this);
        return this;
    }

    public String getIdentifier() {
        return "model";
    }

    /**
     * The location of the model.
     * @return model location.
     */
    public ResourceLocation location() {
        return location;
    }

    /**
     * Is this model a MultiPartModel?
     * @return if the model is multiPart.
     */
    public boolean isMultiPart() {
        return multiPart;
    }

    /**
     * Attention: You had better call {@link SimpleModel#clone()} to get a cloned model for usage
     * after the model registry has been fired. Therefore, call this getter method in your renderer's constructor
     * instead of declare it just in the namespace. Calling this before the registry fired would lead to an empty model.
     * Directly use the model you got here would lead to a model animation chaos.
     * @return the model you have registered.
     */
    public SimpleModel getModel() {
        return baked;
    }

    @Inner
    public HashMap<String, ResourceLocation> getMappings() {
        return mappingForBones;
    }

    @Inner
    public void rebuildAsMultiPart() {
        this.baked = new MultiPartModel(registrationKey);
    }

    @Inner
    public void putModelIn(SimpleModel model) {
        this.baked.copyModelFrom(model);
    }

    @Inner
    public void compileFile(SimpleRegistry registry) throws IOException {
        ResourceLocation location = new ResourceLocation(registry.namespace, "models/" + this.location.getPath() + ".json");
        Resource resource = getResource(location);
        if(resource == null) return;
        JsonObject json = JsonParser.parseReader(new JsonReader(resource.openAsReader())).getAsJsonObject();
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

    @Inner
    private ResourceLocation getModelLocation(ResourceLocation location) {
        return new ResourceLocation(location.getNamespace(), "models/" + location.getPath() + ".json");
    }

    @Inner
    private static ArrayList<Map.Entry<String, ResourceLocation>> getMapFromJson(JsonObject members, String prefix) {
        String former = prefix.equals("") ? prefix : (prefix + ".");
        Type type = new TypeToken<Map<String, String>>() {}.getType();
        Map<String, String> m = KasugaLib.GSON.fromJson(members, type);
        ArrayList<Map.Entry<String, ResourceLocation>> entries = new ArrayList<>();
        for(String key : m.keySet()) {entries.add(new AbstractMap.SimpleEntry<>(former + key, new ResourceLocation(m.get(key))));}
        return entries;
    }

    @Inner
    private Resource getResource(ResourceLocation location) throws IOException {
        Map<String, Resource> map = Resources.getResources(location, true);
        Resource resource = null;
        if(!map.isEmpty()) {for(String s : map.keySet()) {resource = map.get(s);}}
        return resource;
    }

    @Inner
    private JsonObject presentIfIsMulti(ResourceLocation location) throws IOException {
        Resource resource = getResource(location);
        if(resource == null) return null;
        JsonObject json = JsonParser.parseReader(new JsonReader(resource.openAsReader())).getAsJsonObject();
        if(!json.has(COMBINE_CODEC)) return null;
        return json.getAsJsonObject(COMBINE_CODEC);
    }
}
