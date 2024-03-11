package kasuga.lib.codes.compute.data;

import kasuga.lib.codes.compute.data.functions.DoublePrarmFunction;
import kasuga.lib.codes.compute.data.functions.Function;
import kasuga.lib.codes.compute.data.functions.SingleParamFunction;
import kasuga.lib.codes.compute.data.functions.TripleParamFunction;
import kasuga.lib.codes.compute.infrastructure.Assignable;
import kasuga.lib.codes.compute.infrastructure.Formula;
import kasuga.lib.codes.logic.data.LogicalLine;
import kasuga.lib.codes.logic.infrastructure.LogicalData;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Set;

public class Namespace {
    public final HashMap<String, Function> FUNCTIONS;
    public final HashMap<String, Variable> STATIC_VARS;
    public final HashMap<String, Assignable> INSTANT_VARS;
    @Nullable private Namespace parent;

    public Namespace() {
        this(null);
    }

    public Namespace(@Nullable Namespace parent) {
        FUNCTIONS = new HashMap<>();
        STATIC_VARS = new HashMap<>();
        INSTANT_VARS = new HashMap<>();
        if(parent != null) {
            this.parent = parent;
            this.FUNCTIONS.putAll(parent.FUNCTIONS);
            this.STATIC_VARS.putAll(parent.STATIC_VARS);
            this.INSTANT_VARS.putAll(parent.INSTANT_VARS);
        }
    }

    public @Nullable Namespace parent() {
        return parent;
    }

    public HashMap<String, Function> functions() {
        return FUNCTIONS;
    }

    public HashMap<String, Assignable> variables() {
        HashMap<String, Assignable> vars = new HashMap<>();
        vars.putAll(STATIC_VARS);
        vars.putAll(INSTANT_VARS);
        return vars;
    }

    public <T extends Function> T register(String codec, T function) {
        FUNCTIONS.put(codec, function);
        return function;
    }

    public SingleParamFunction register1Param(String codec, SingleParamFunction.Computer computer) {
        SingleParamFunction function = new SingleParamFunction(codec, this, computer);
        FUNCTIONS.put(codec, function);
        return function;
    }

    public DoublePrarmFunction register2Param(String codec, DoublePrarmFunction.Computer computer) {
        DoublePrarmFunction function = new DoublePrarmFunction(codec, this, computer);
        FUNCTIONS.put(codec, function);
        return function;
    }

    public TripleParamFunction register3Param(String codec, TripleParamFunction.Computer computer) {
        TripleParamFunction function = new TripleParamFunction(codec, this, computer);
        FUNCTIONS.put(codec, function);
        return function;
    }

    public @Nullable Function createFunctionInstance(String codec) {
        if(FUNCTIONS.containsKey(codec))
            return FUNCTIONS.get(codec).clone(this);
        return null;
    }


    public Variable register(String codec, float value) {
        Variable variable = new Variable(codec, this, value);
        STATIC_VARS.put(codec, variable);
        return variable;
    }

    public void registerInstance(String codec, Assignable assignable) {
        this.INSTANT_VARS.put(codec, assignable);
    }

    public boolean hasInstance() {
        return !INSTANT_VARS.isEmpty();
    }

    public void assign(String codec, float value) {
        if(INSTANT_VARS.containsKey(codec))
            INSTANT_VARS.get(codec).assign(codec, value);
    }

    public Formula decodeFormula(String formulaString) {
        Line line = new Line(formulaString, this);
        Formula formula = line;
        while (formula instanceof Line && formula.getElements().size() == 1) {
            formula =  formula.getElements().get(0);
        }
        return formula;
    }

    public LogicalData decodeLogical(String logicalString) {
        LogicalData data = new LogicalLine(logicalString, this);
        while (data instanceof LogicalLine line && !line.isEmpty() && line.isAtomic()) {
            data = line.getFirst();
        }
        return data;
    }

    public int instanceVarSize() {
        return INSTANT_VARS.size();
    }

    public boolean containsInstance(String codec) {
        return INSTANT_VARS.containsKey(codec);
    }

    public Assignable getInstance(String codec) {
        return INSTANT_VARS.getOrDefault(codec, null);
    }

    public Set<String> instanceNames() {
        return INSTANT_VARS.keySet();
    }

    public Variable getStaticVar(String codec) {return STATIC_VARS.getOrDefault(codec, null);}

    public Function getFunction(String codec) {
        return FUNCTIONS.getOrDefault(codec, null);
    }

    @Override
    public Namespace clone() {
        Namespace namespace = new Namespace(this);
        namespace.parent = this.parent;
        return namespace;
    }
}
