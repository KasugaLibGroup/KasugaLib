package kasuga.lib.core.client.block_bench_model.json_data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import lombok.Getter;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
@Getter
public class Outline {

    private final HashMap<UUID, IElement> elements;
    private final HashMap<UUID, Element> leaves;

    public Outline(BlockBenchFile file, JsonArray json) {
        elements = new HashMap<UUID, IElement>();
        leaves = file.getElements();
        scan(json, elements);
    }

    public void scan(JsonArray array, HashMap<UUID, IElement> elements) throws BlockBenchFile.UnableToLoadFileError {
        for (JsonElement element : array) {
            if (element instanceof JsonObject) {
                try {
                    JsonObject object = (JsonObject) element;
                    Group group = new Group(object);
                    elements.put(group.getId(), group);
                    JsonArray childrenArray = object.get("children").getAsJsonArray();
                    if (!childrenArray.isEmpty()) {
                        scan(childrenArray, group.getChildren());
                    }
                } catch (Exception e) {
                    throw new BlockBenchFile.UnableToLoadFileError("Unable to load group", e);
                }
            } else {
                UUID id = UUID.fromString(element.getAsString());
                elements.put(id, leaves.get(id));
            }
        }
    }
}
