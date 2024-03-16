package kasuga.lib.core.client.animation.data.timer;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import interpreter.compute.data.Namespace;
import interpreter.compute.data.Numeric;
import interpreter.compute.data.Variable;
import interpreter.compute.infrastructure.Formula;
import interpreter.logic.data.LogicalBool;
import interpreter.logic.infrastructure.LogicalData;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.client.animation.infrastructure.AnimAssignable;
import kasuga.lib.core.client.animation.infrastructure.AnimationElement;
import kasuga.lib.core.client.animation.infrastructure.Condition;
import kasuga.lib.core.util.data_type.Pair;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.*;


@OnlyIn(Dist.CLIENT)
public class TimeLine extends AnimationElement implements AnimAssignable {
    Formula max, min, org;
    private final HashMap<Condition, Formula> chasers;
    private final HashMap<Condition, Formula> tickingChasers;
    private final HashMap<Condition, ArrayList<String>> controlledElements;
    private final HashMap<Condition, ArrayList<String>> tickingControlledElements;
    private final Namespace namespace;
    Condition condition;

    public TimeLine(String key, Namespace namespace) {
        super(key);
        controlledElements = new HashMap<>();
        chasers = new HashMap<>();
        tickingChasers = new HashMap<>();
        tickingControlledElements = new HashMap<>();
        this.namespace = namespace;
    }

    @Override
    public void init() {
        namespace.assign(key(), org.getResult());
    }

    public static TimeLine decode(String key, Namespace namespace, JsonObject object) {
        LogicalData data = LogicalBool.defaultTrue();
        if (object.has("condition")) {
            String element = object.get("condition").getAsString();
            if (element.equals("true") || element.equals("false")) {
                data = new LogicalBool(element.equals("true"));
            } else {
                data = namespace.decodeLogical(element);
            }
        }
        TimeLine timeLine = new TimeLine(key, namespace);
        Condition condition = new Condition("condition", namespace, data);
        Formula max = timeLine.createFormula(object, "max", 100f);
        Formula min = timeLine.createFormula(object, "min", 0f);
        Formula org = timeLine.createFormula(object, "org", 0f);
        namespace.registerInstance(key, new Variable(key + ".max", namespace, max.getResult()));
        namespace.registerInstance(key, new Variable(key + ".min", namespace, min.getResult()));
        namespace.registerInstance(key, new Variable(key + ".org", namespace, org.getResult()));
        namespace.registerInstance(key, new Variable(key, namespace, 0f));
        timeLine.getChasers(object, namespace, false);
        timeLine.getChasers(object, namespace, true);
        timeLine.condition = condition;
        timeLine.max = max;
        timeLine.min = min;
        timeLine.org = org;
        return timeLine;
    }

    private void getChasers(JsonObject object, Namespace namespace, boolean ticking) {
        String name = ticking ? "ticking" : "chaser";
        if (object.has(name)) {
            Pair<HashMap<Condition, Formula>, HashMap<Condition, ArrayList<String>>> maps =
                    getMapFromJson(namespace, object.get(name));
            if(ticking) {
                this.tickingChasers.putAll(maps.getFirst());
                this.tickingControlledElements.putAll(maps.getSecond());
            } else {
                this.chasers.putAll(maps.getFirst());
                this.controlledElements.putAll(maps.getSecond());
            }
        }
    }

    public static Pair<HashMap<Condition, Formula>, HashMap<Condition, ArrayList<String>>>
    getMapFromJson(Namespace namespace, JsonElement members) {
        Type type = new TypeToken<Map<String, String>>() {}.getType();
        Map<String, String> m = KasugaLib.GSON.fromJson(members, type);
        HashMap<Condition, Formula> result = new HashMap<>();
        HashMap<Condition, ArrayList<String>> controlledElements = new HashMap<>();
        for(String con : m.keySet()) {
            String string = m.get(con);
            String[] spt = string.split("=");
            Condition condition1 = new Condition("condition", namespace, namespace.decodeLogical(con));
            if(spt.length == 2) {
                String[] vars = spt[0].replace(" ", "").split(",");
                Formula formula = namespace.decodeFormula(spt[1]);
                result.put(condition1, formula);
                controlledElements.put(condition1, new ArrayList<>(List.of(vars)));
                for (String var : vars) {
                    if(!namespace.containsInstance(var))
                        namespace.registerInstance(var, new Variable(var, namespace, formula.getResult()));
                }
            } else if (spt.length == 1) {
                result.put(condition1, namespace.decodeFormula(spt[0]));
            }
        }
        return Pair.of(result, controlledElements);
    }

    private Formula createFormula(JsonObject parentObject, String key, float defaultValue) {
        if(!parentObject.has(key)) return new Numeric(defaultValue);
        return createFormula(parentObject.get(key), defaultValue);
    }

    private Formula createFormula(JsonElement jsonObject, float defaultValue) {
        Formula result = new Numeric(defaultValue);
        try {
            result = new Numeric(jsonObject.getAsNumber().floatValue());
        } catch (ClassCastException e) {
            result = namespace.decodeFormula(jsonObject.getAsString());
        } catch (Exception ignored) {}
        return result;
    }

    public void action() {
        innerAction(chasers, controlledElements);
    }

    public void actionTicking() {
        innerAction(tickingChasers, tickingControlledElements);
    }

    private void innerAction(HashMap<Condition, Formula> chaser, HashMap<Condition, ArrayList<String>> controlled) {
        for(Condition condition : chaser.keySet()) {
            if(!condition.result()) continue;
            float[] values = updateValues();
            float result = chaser.get(condition).getResult();
            float value = Math.max(Math.min(values[0], result), values[1]);
            for (String var : controlled.get(condition)) {
                if(var.equals(key()))
                    namespace.assign(var, value);
                else
                    namespace.assign(var, result);
            }
        }
    }

    public float[] updateValues() {
        float max = getMax().getResult();
        float min = getMin().getResult();
        float org = getOrg().getResult();
        namespace.assign(key() + ".max", max);
        namespace.assign(key() + ".min", min);
        namespace.assign(key() + ".org", org);
        return new float[]{max, min, org};
    }

    public void setCondition(String condition) {
        this.condition = new Condition(key(), namespace, condition);
    }

    public Condition getCondition() {
        return condition;
    }
    public void setMax(String max) {
        this.max = namespace.decodeFormula(max);
    }
    public Formula getMax() {
        return max;
    }
    public void setMin(String min) {
        this.min = namespace.decodeFormula(min);
    }
    public Formula getMin() {
        return min;
    }

    public void setOrg(String org) {
        this.org = namespace.decodeFormula(org);
    }

    public Formula getOrg() {
        return org;
    }

    public Namespace getNamespace() {
        return namespace;
    }

    public void addChaser(String condition, String varsAndChaser) {
        String[] strs = varsAndChaser.replace(" ", "").split("=");
        if(strs.length == 2) {
            String[] vars = strs[0].split(",");
            addChaser(condition, strs[1], vars);
        }
    }

    public void addChaser(String condition, String chaser, String... controlled) {
        Formula formula = namespace.decodeFormula(chaser);
        Condition cond = new Condition(key(), namespace, condition);
        controlledElements.put(cond, new ArrayList<>(List.of(controlled)));
        addChaser(cond, formula);
    }

    private void addChaser(Condition condition, Formula formula) {
        chasers.put(condition, formula);
    }

    public void removeChaser(Condition condition) {
        chasers.remove(condition);
    }

    public void removeChaser(int index) {
        chasers.remove(index);
    }

    public @Nullable Pair<Condition, Formula> getChaser(Condition condition) {
        if (chasers.containsKey(condition))
            return Pair.of(condition, chasers.get(condition));
        return null;
    }

    public Formula[] getAllActing() {
        Formula[] formulas = new Formula[chasers.size()];
        int counter = 0;
        for(Formula formula : chasers.values()) {
            formulas[counter] = formula;
            counter++;
        }
        return formulas;
    }

    public HashMap<Condition, Formula> getChasers() {
        return chasers;
    }

    public boolean isValid() {
        return max != null && min != null && org != null && !chasers.isEmpty() && condition != null;
    }


    public void assign(String codec, float value) {
        namespace.assign(codec, value);
    }

    @Override
    public boolean isAssignable() {
        return isValid() && namespace.hasInstance();
    }
}
