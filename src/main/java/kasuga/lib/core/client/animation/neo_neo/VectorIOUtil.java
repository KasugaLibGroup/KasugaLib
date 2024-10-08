package kasuga.lib.core.client.animation.neo_neo;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class VectorIOUtil {

    public static void writeVec3ToJson(JsonObject json, String name, Vec3 vec) {
        JsonObject obj = new JsonObject();
        obj.addProperty("x", vec.x());
        obj.addProperty("y", vec.y());
        obj.addProperty("z", vec.z());
        json.add(name, obj);
    }

    public static Vec3 readVec3FromJson(JsonObject json, String name) {
        if (!json.has(name)) return Vec3.ZERO;
        JsonElement element = json.get(name);
        if (!(element instanceof JsonObject obj)) return Vec3.ZERO;
        double x = obj.get("x").getAsDouble(),
                y = obj.get("y").getAsDouble(),
                z = obj.get("z").getAsDouble();
        return new Vec3(x, y, z);
    }

    public static void writeVec3ListToJson(JsonObject json, String name, List<Vec3> vec3s) {
        if (vec3s.isEmpty()) return;
        int size = vec3s.size();
        JsonObject obj = new JsonObject();
        obj.addProperty("size", size);
        for (int i = 0; i < size; i++) {writeVec3ToJson(obj, String.valueOf(i), vec3s.get(i));}
        json.add(name, obj);
    }

    public static List<Vec3> readVec3ListFromJson(JsonObject json, String name) {
        if (!json.has(name)) return List.of();
        JsonElement element = json.get(name);
        if (!(element instanceof JsonObject obj)) return List.of();
        int size = obj.get("size").getAsInt();
        ArrayList<Vec3> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            result.add(readVec3FromJson(obj, String.valueOf(i)));
        }
        return result;
    }

    public static void writeVec3ToNbt(CompoundTag nbt, String name, Vec3 vec3) {
        CompoundTag tag = new CompoundTag();
        tag.putString("type", "vec3");
        tag.putDouble("x", vec3.x());
        tag.putDouble("y", vec3.y());
        tag.putDouble("z", vec3.z());
        nbt.put(name, tag);
    }

    public static Vec3 getVec3FromNbt(CompoundTag nbt, String name) {
        if (!nbt.contains(name)) return Vec3.ZERO;
        CompoundTag tag = nbt.getCompound(name);
        if (tag.size() != 4) return Vec3.ZERO;
        String type = tag.getString("type");
        if (!type.equals("vec3")) return Vec3.ZERO;
        return new Vec3(tag.getDouble("x"), tag.getDouble("y"), tag.getDouble("z"));
    }

    public static void writeVec3ListToNbt(CompoundTag nbt, String name, List<Vec3> vec3s) {
        CompoundTag tag = new CompoundTag();
        tag.putString("type", "vec3_list");
        tag.putInt("size", vec3s.size());
        int counter = 0;
        for (Vec3 vec3 : vec3s) {
            writeVec3ToNbt(tag, String.valueOf(counter), vec3);
            counter++;
        }
        nbt.put(name, tag);
    }

    public static List<Vec3> getVec3ListFromNbt(CompoundTag nbt, String name) {
        if (!nbt.contains(name)) return List.of();
        CompoundTag tag = nbt.getCompound(name);
        if (!tag.getString("type").equals("vec3_list")) return List.of();
        if (tag.getInt("size") == 0) return List.of();
        ArrayList<Vec3> result = new ArrayList<>(tag.getInt("size"));
        for (int i = 0; i < tag.getInt("size"); i++) {
            result.add(getVec3FromNbt(tag, String.valueOf(i)));
        }
        return result;
    }
}
