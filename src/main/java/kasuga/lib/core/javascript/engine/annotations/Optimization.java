package kasuga.lib.core.javascript.engine.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class Optimization {

    @Target({ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface DisableRuntimeAccessibilityCheck{}
}
