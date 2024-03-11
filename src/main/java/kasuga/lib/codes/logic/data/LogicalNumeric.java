package kasuga.lib.codes.logic.data;

import kasuga.lib.codes.Code;
import kasuga.lib.codes.compute.data.Namespace;
import kasuga.lib.codes.compute.infrastructure.Assignable;
import kasuga.lib.codes.compute.infrastructure.Formula;
import kasuga.lib.codes.logic.infrastructure.LogicalAssignable;
import kasuga.lib.codes.logic.infrastructure.LogicalData;

import java.util.HashMap;
import java.util.Map;

public class LogicalNumeric implements LogicalData, LogicalAssignable {
    private final Formula formula;
    private final Namespace namespace;

    public LogicalNumeric(Formula formula) {
        this.formula = formula;
        if(formula instanceof Assignable assignable)
            namespace = assignable.getNamespace();
        else
            namespace = null;
    }

    public LogicalNumeric(String formulaString, Namespace namespace) {
        this.formula = namespace.decodeFormula(formulaString);
        this.namespace = namespace;
    }

    public boolean getResult() {return (int) mathResult() != 0;}

    @Override
    public boolean isAtomic() {
        return true;
    }

    @Override
    public void assign(String codec, float value) {
        if(isAssignable()) ((Assignable) formula).assign(codec, value);
    }

    @Override
    public Namespace getNamespace() {
        return namespace;
    }

    @Override
    public String toString() {
        return formula.getString();
    }

    @Override
    public boolean isAssignable() {
        return namespace != null;
    }

    @Override
    public LogicalNumeric clone() {
        return new LogicalNumeric(formula.clone());
    }

    public float mathResult() {return formula.getResult();}

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof LogicalNumeric numeric)) return false;
        return formula.equals(numeric.formula);
    }
}
