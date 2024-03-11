package kasuga.lib.codes.compute.infrastructure;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public interface Formula {

    String FRONT_BRACKET_CODEC = "(";
    String BACK_BRACKET_CODEC = ")";
    String getString();
    String getIdentifier();
    float getResult();
    List<Formula> getElements();
    boolean isAtomic();
    boolean shouldRemove();
    void fromString(String string);
    Formula clone();
    void flipOutput(boolean flip);
    boolean isOutputFlipped();
}
