package kasuga.lib.core.client.animation.infrastructure;

import kasuga.lib.codes.UniversalAssignable;
import kasuga.lib.codes.compute.data.Namespace;
import kasuga.lib.codes.compute.infrastructure.Assignable;

import java.util.Map;

public interface AnimAssignable {
    void assign(String codec, float value);
    boolean isAssignable();
    Namespace getNamespace();
}
