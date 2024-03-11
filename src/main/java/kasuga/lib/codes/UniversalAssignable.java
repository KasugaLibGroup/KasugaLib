package kasuga.lib.codes;

import kasuga.lib.codes.compute.data.Namespace;
import kasuga.lib.codes.compute.infrastructure.Assignable;

import java.util.Map;

public interface UniversalAssignable {
    void assign(String codec, float value);
    Namespace getNamespace();
}
