package kasuga.lib.core.xml;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import kasuga.lib.core.annos.Util;

import java.util.Map;
import java.util.Set;

@Util
public class JsonConvertor {

    @Util
    public static IXmlObject<?> json2Xml(String key, JsonElement jsonObject) {
        if (jsonObject.isJsonPrimitive()) {
            JsonPrimitive primitive = (JsonPrimitive) jsonObject;
            if (primitive.isNumber())
                return new XmlNumber(key, primitive.getAsDouble());
            return new XmlString(key, primitive.getAsString());
        } else if (jsonObject.isJsonObject()) {

            JsonObject obj = (JsonObject) jsonObject;
            XmlCompound compound;
            if (obj.has("attr")) {
                JsonObject object = obj.getAsJsonObject("attr");
                Set<Map.Entry<String, JsonElement>> entries = object.entrySet();
                IXmlObject<?>[] objs = new IXmlObject[entries.size()];
                int counter = 0;
                for (Map.Entry<String, JsonElement> entry : entries) {
                    objs[counter] = json2Xml(entry.getKey(), entry.getValue());
                    counter++;
                }
                if (obj.entrySet().size() == 2 && obj.has("value")) {
                    JsonElement element = obj.get("value");
                    if (element.isJsonPrimitive()) {
                        JsonPrimitive pri = element.getAsJsonPrimitive();
                        if (pri.isNumber())
                            return new XmlNumber(key, pri.getAsDouble(), objs);
                        return new XmlString(key, pri.getAsString(), objs);
                    }
                }
                compound = new XmlCompound(key, objs);
            } else {
                compound = new XmlCompound(key);
            }
            for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                compound.setValue(key, json2Xml(entry.getKey(), entry.getValue()));
            }
            return compound;
        } else return null;
    }

    @Util
    public static JsonElement xml2Json(IXmlObject<?> xmlObject) {
        if (xmlObject.isPrimitive()) {
            Set<IXmlObject<?>> attr = xmlObject.attributes();
            boolean flag = attr.isEmpty();
            JsonPrimitive pri;
            if (xmlObject instanceof XmlNumber number) {
                pri = new JsonPrimitive(number.getValue(number.key()));
            } else {
                pri = new JsonPrimitive(((XmlString) xmlObject).getValue(xmlObject.key()));
            }
            if (flag) return pri;

            JsonObject obj = new JsonObject();
            obj.add("value", pri);
            JsonObject attributes = new JsonObject();
            for (IXmlObject<?> xml : attr) attributes.add(xml.key(), xml2Json(xml));
            obj.add("attr", attributes);
            return obj;
        } else {
            JsonObject object = new JsonObject();
            XmlCompound compound = (XmlCompound) xmlObject;
            Set<IXmlObject<?>> attr = compound.attributes();
            Set<IXmlObject<?>> vals = compound.getValues();
            JsonObject attributes = new JsonObject();
            for (IXmlObject<?> xml : attr) attributes.add(xml.key(), xml2Json(xml));
            object.add("attr", attributes);
            for (IXmlObject<?> xml : vals) object.add(xml.key(), xml2Json(xml));
            return object;
        }
    }
}
