package kasuga.lib.data_gen;

import com.google.gson.JsonObject;

public interface Generator<T> {
    JsonObject getJson();
    String getidentifier();
    Class<? extends T> generatorClass();
}
