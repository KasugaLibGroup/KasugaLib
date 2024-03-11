package kasuga.lib.codes;

import kasuga.lib.codes.compute.data.Namespace;
import kasuga.lib.codes.compute.data.functions.DoublePrarmFunction;
import kasuga.lib.codes.compute.data.functions.Function;
import kasuga.lib.codes.compute.data.Line;
import kasuga.lib.codes.compute.data.functions.SingleParamFunction;
import kasuga.lib.codes.compute.data.Variable;
import kasuga.lib.codes.compute.data.functions.TripleParamFunction;
import kasuga.lib.codes.compute.infrastructure.Assignable;
import kasuga.lib.codes.compute.infrastructure.Formula;
import kasuga.lib.codes.logic.data.LogicalLine;
import kasuga.lib.codes.logic.infrastructure.LogicalData;
import kasuga.lib.core.util.Start;

import java.util.HashMap;

public class Code {
    public static final Namespace ROOT_NAMESPACE = new Namespace();

    // -------------------------------------------------------------------------------------------------------- //

    public static final SingleParamFunction COS = register1Param("cos", (in) -> (float) Math.cos(in));
    public static final SingleParamFunction SIN = register1Param("sin", (in) -> (float) Math.sin(in));
    public static final SingleParamFunction TAN = register1Param("tan", (in) -> (float) Math.tan(in));
    public static final SingleParamFunction ASIN = register1Param("asin", (in) -> (float) Math.asin(in));
    public static final SingleParamFunction ACOS = register1Param("acos", (in) -> (float) Math.acos(in));
    public static final SingleParamFunction ATAN = register1Param("atan", (in) -> (float) Math.atan(in));
    public static final SingleParamFunction LOG = register1Param("log", (in) -> (float) Math.log(in));
    public static final SingleParamFunction LG = register1Param("lg", (in) -> (float) Math.log10(in));
    public static final SingleParamFunction EXP = register1Param("exp", (in) -> (float) Math.exp(in));
    public static final SingleParamFunction ROUND = register1Param("round", Math::round);
    public static final SingleParamFunction SQRT = register1Param("min", (in1) -> (float) Math.sqrt(in1));
    public static final SingleParamFunction DEG2RAD = register1Param("rad", (in1) -> (float) Math.toRadians(in1));
    public static final SingleParamFunction RAD2DEG = register1Param("deg", (in1) -> (float) Math.toDegrees(in1));
    public static final SingleParamFunction FLOOR = register1Param("floor", (in) -> (float) Math.floor(in));
    public static final SingleParamFunction CEIL = register1Param("ceil", (in) -> (float) Math.ceil(in));

    // -------------------------------------------------------------------------------------------------------- //

    public static final DoublePrarmFunction POW = register2Param("pow", (in1, in2) -> (float) Math.pow(in1, in2));
    public static final DoublePrarmFunction MAX = register2Param("max", Math::max);
    public static final DoublePrarmFunction MIN = register2Param("min", Math::min);

    // -------------------------------------------------------------------------------------------------------- //

    public static final Variable PI = register("pi", (float) Math.PI);
    public static final Variable E = register("e", (float) Math.E);

    // -------------------------------------------------------------------------------------------------------- //

    public static <T extends Function> T register(String codec, T function) {
        return ROOT_NAMESPACE.register(codec, function);
    }

    public static SingleParamFunction register1Param(String codec, SingleParamFunction.Computer computer) {
        return ROOT_NAMESPACE.register1Param(codec, computer);
    }

    public static DoublePrarmFunction register2Param(String codec, DoublePrarmFunction.Computer computer) {
        return ROOT_NAMESPACE.register2Param(codec, computer);
    }

    public static TripleParamFunction register3Param(String codec, TripleParamFunction.Computer computer) {
        return ROOT_NAMESPACE.register3Param(codec, computer);
    }

    public static Variable register(String codec, float value) {
        return ROOT_NAMESPACE.register(codec, value);
    }

    public static Variable getStaticVar(String codec) {return ROOT_NAMESPACE.getStaticVar(codec);}

    public static Function getFunction(String codec) {
        return ROOT_NAMESPACE.getFunction(codec);
    }

    public static HashMap<String, Function> getFunctions() {
        return ROOT_NAMESPACE.FUNCTIONS;
    }

    public static HashMap<String, Variable> getStaticVars() {return ROOT_NAMESPACE.STATIC_VARS;}

    public static Formula decodeFormula(String formulaString, Namespace namespace) {
        Line line = new Line(namespace);
        line.fromString(formulaString);
        Formula formula = line;
        while (formula instanceof Line && formula.getElements().size() == 1) {
            formula =  formula.getElements().get(0);
        }
        return formula;
    }
    public static String encodeFormula(Formula formula) {
        return formula.getString();
    }

    public static LogicalData decodeLogical(String logicalString, Namespace namespace) {
        LogicalData data = new LogicalLine(logicalString, namespace);
        while (data instanceof LogicalLine line && !line.isEmpty() && line.isAtomic()) {
            data = line.getFirst();
        }
        return data;
    }

    public static String encodeLogical(LogicalData data) {
        return data.toString();
    }

    public static Namespace root() {
        return ROOT_NAMESPACE;
    }
}
