package kasuga.lib.core.client.animation.infrastructure;

import kasuga.lib.core.client.render.model.MultiPartModel;
import kasuga.lib.core.client.render.model.SimpleModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


@OnlyIn(Dist.CLIENT)
public class MappingLayer {
    private SimpleModel model;
    private final HashMap<String, SimpleModel> mapping;

    public MappingLayer() {
        this.mapping = new HashMap<>();
    }
    public MappingLayer(SimpleModel model) {
        this();
        this.model = model;
    }

    public void buildMapping() {
        mapping.put("this", model);
        if(model instanceof MultiPartModel multiPartModel) {
            HashMap<String, SimpleModel> cache = new HashMap<>(multiPartModel.getBoneMap());
            for(SimpleModel model1 : cache.values()) {scanModel(model1, "");}
        }
    }

    public SimpleModel getModel() {
        return model;
    }

    public void setModel(SimpleModel model) {
        this.model = model;
    }

    public void rebuildMapping(SimpleModel model) {
        setModel(model);
        rebuildMapping();
    }
    public void rebuildMapping() {
        this.mapping.clear();
        buildMapping();
    }

    public HashMap<String, SimpleModel> getMapping() {
        return mapping;
    }

    public void replaceMapping(String oldKeyName, String newKeyName) {
        HashSet<String> keySet = new HashSet<>();
        mapping.keySet().forEach(codec -> {
            if (codec.contains(oldKeyName)) keySet.add(codec);
        });
        for(String key : keySet) {
            mapping.put(key.replace(oldKeyName, newKeyName), mapping.get(key));
            mapping.remove(key);
        }
    }

    private void scanModel(SimpleModel model, String prefix) {
        String keyName = prefix.equals("") ? model.key : (prefix + "." + model.key);
        mapping.put(keyName, model);
        if(model instanceof MultiPartModel multiPartModel) {
            Map<String, SimpleModel> boneMap = multiPartModel.getBoneMap();
            for (SimpleModel modelx : boneMap.values()) {
                scanModel(modelx, keyName);
            }
        }
    }
}
