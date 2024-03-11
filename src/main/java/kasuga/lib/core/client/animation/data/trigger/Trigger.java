package kasuga.lib.core.client.animation.data.trigger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import kasuga.lib.codes.compute.data.Namespace;
import kasuga.lib.codes.compute.data.Variable;
import kasuga.lib.codes.compute.infrastructure.Formula;
import kasuga.lib.codes.logic.data.LogicalBool;
import kasuga.lib.codes.logic.infrastructure.LogicalData;
import kasuga.lib.core.client.animation.infrastructure.AnimAssignable;
import kasuga.lib.core.client.animation.infrastructure.AnimationElement;
import kasuga.lib.core.client.animation.infrastructure.Condition;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@OnlyIn(Dist.CLIENT)
public class Trigger extends AnimationElement implements AnimAssignable {
    Condition condition;
    private final ArrayList<Formula> functions;
    private final HashMap<Formula, ArrayList<String>> controlled;
    private final Namespace namespace;
    public Trigger(String key, Namespace namespace) {
        super(key);
        this.namespace = namespace;
        functions = new ArrayList<>();
        controlled = new HashMap<>();
        condition = Condition.defaultTrue("condition", namespace);
    }

    public void init() {}

    public static Trigger decode(String key, Namespace namespace, JsonObject object) {
        LogicalData data = LogicalBool.defaultTrue();
        if (object.has("condition")) {
            String element = object.get("condition").getAsString();
            if (element.equals("true") || element.equals("false")) {
                data = new LogicalBool(element.equals("true"));
            } else {
                data = namespace.decodeLogical(element);
            }
        }
        Condition condition1 = new Condition("condition", namespace, data);
        Trigger trigger = new Trigger(key, namespace);
        JsonArray funcs = object.getAsJsonArray("action");
        for (JsonElement element : funcs) {
            trigger.addFunction(element.getAsString());
        }
        trigger.condition = condition1;
        return trigger;
    }

    public void action() {
        if(!condition.result()) return;
        for (Formula trg : functions) {
            float result = trg.getResult();
            if (controlled.containsKey(trg)) {
                for (String var : controlled.get(trg))
                    namespace.assign(var, result);
            }
        }
    }

    public void addFunction(String function) {
        function = function.replace(" ", "");
        String[] strs = function.split("=");
        if(strs.length == 1) {
            this.functions.add(namespace.decodeFormula(function));
        } else if (strs.length == 2) {
            String[] vars = strs[0].split(",");
            Formula formula = namespace.decodeFormula(strs[1]);
            functions.add(formula);
            controlled.put(formula, new ArrayList<>(List.of(vars)));
            for (String var : vars) {
                if(!namespace.containsInstance(var))
                    namespace.registerInstance(var, new Variable(var, namespace, formula.getResult()));
            }
        }
    }

    public ArrayList<Formula> getFunctions() {
        return functions;
    }


    @Override
    public boolean isAssignable() {
        return namespace.hasInstance();
    }

    @Override
    public Namespace getNamespace() {
        return namespace;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void assign(String codec, float value) {
        namespace.assign(codec, value);
    }
}
