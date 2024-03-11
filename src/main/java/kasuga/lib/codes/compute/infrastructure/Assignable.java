package kasuga.lib.codes.compute.infrastructure;

import kasuga.lib.codes.UniversalAssignable;
import kasuga.lib.codes.compute.data.Namespace;

import java.util.Map;
import java.util.Set;

public interface Assignable extends UniversalAssignable {

    Namespace getNamespace();
    Set<String> variableCodecs();
    void assign(String codec, float value);
    boolean containsVar(String codec);
    float getValue(String codec);
    boolean hasVar();
}
