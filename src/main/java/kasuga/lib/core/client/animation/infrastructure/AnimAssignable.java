package kasuga.lib.core.client.animation.infrastructure;

import interpreter.compute.data.Namespace;

import java.util.Map;

public interface AnimAssignable {
    void assign(String codec, float value);
    boolean isAssignable();
    Namespace getNamespace();
}
