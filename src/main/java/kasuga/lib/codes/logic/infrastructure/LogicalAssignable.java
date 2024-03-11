package kasuga.lib.codes.logic.infrastructure;

import kasuga.lib.codes.UniversalAssignable;
import kasuga.lib.codes.compute.data.Namespace;
import kasuga.lib.codes.compute.infrastructure.Assignable;

import java.util.Map;

public interface LogicalAssignable extends UniversalAssignable {
    boolean isAssignable();
    void assign(String codec, float value);
    Namespace getNamespace();
}
