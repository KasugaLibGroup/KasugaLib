package kasuga.lib.codes.compute.data.functions;

import kasuga.lib.codes.compute.data.Namespace;
import kasuga.lib.codes.compute.infrastructure.Formula;
import kasuga.lib.codes.compute.data.Line;
import kasuga.lib.codes.compute.infrastructure.Assignable;
import kasuga.lib.codes.compute.exceptions.FormulaSynatxError;
import kasuga.lib.codes.compute.infrastructure.Pretreatable;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class Function implements Formula, Assignable, Pretreatable {
    final String codec;
    public final List<Formula> params;
    final Namespace namespace;
    boolean flip = false;
    public static final String DOTS = ",";
    public Function(String codec, Namespace namespace) {
        this.codec = codec;
        params = new ArrayList<>();
        this.namespace = namespace;
    }

    public Function(String codec, Namespace namespace, List<Formula> params) {
        this.codec = codec;
        this.params = params;
        this.namespace = namespace;
    }

    public Function(String codec, Namespace namespace, Formula... params) {
        this.codec = codec;
        this.params = new ArrayList<>(List.of(params));
        this.namespace = namespace;
    }

    public Function(String codec, Namespace namespace, String paramString) {
        this(codec, namespace);
        fromString(paramString);
    }

    public String getCodec() {
        return codec;
    }

    public String toString() {
        return getString();
    }

    @Override
    public String getString() {
        StringBuilder builder = new StringBuilder();
        if(flip)
            builder.append("-");
        builder.append(codec).append(FRONT_BRACKET_CODEC);
        if(params.size() == 1) {
            builder.append(params.get(0).getString());
        } else {
            for (Formula formula : params) {
                if (params.indexOf(formula) == params.size() - 1)
                    builder.append(formula.getString());
                else
                    builder.append(formula.getString()).append(", ");
            }
        }
        return builder.append(BACK_BRACKET_CODEC).toString();
    }

    public int paramListLength() {
        return params.size();
    }


    @Override
    public String getIdentifier() {
        return "function";
    }

    @Override
    public float getResult() {
        return flip ? - operate() : operate();
    }

    public abstract float operate();

    @Override
    public List<Formula> getElements() {
        return List.of(this);
    }

    @Override
    public boolean isAtomic() {
        return false;
    }

    @Override
    public boolean shouldRemove() {
        return false;
    }

    @Override
    public void flipOutput(boolean flip) {
        this.flip = flip;
    }

    @Override
    public boolean isOutputFlipped() {
        return flip;
    }

    @Override
    public void fromString(String string) {
        String str = string.replaceAll(" ", "");
        Integer[] dots = getAllDots(string);
        if(dots.length == 0) {
            addParamsFromLine(new Line(str, namespace));
        } else if (dots.length == 1) {
            if(dots[0] == str.length() - 1)
                throw new FormulaSynatxError(this, dots[0]);
            String front = string.substring(0, dots[0]);
            String back = string.substring(dots[0] + 1);
            addParamsFromLine(new Line(front, namespace));
            addParamsFromLine(new Line(back, namespace));
        } else {
            String param = string.substring(0, dots[0]);
            addParamsFromLine(new Line(param, namespace));
            for(int i = 0; i < dots.length - 1; i++) {
                param = string.substring(dots[i] + 1, dots[i + 1]);
                addParamsFromLine(new Line(param, namespace));
            }
            param = string.substring(dots[dots.length - 1] + 1);
            addParamsFromLine(new Line(param, namespace));
        }
    }

    void addParamsFromLine(Line line) {
        if(line.isAtomic() && !line.shouldRemove()) {
            this.params.add(line.getElements().get(0));
        } else if (!line.shouldRemove()) {
            this.params.add(line);
        }
    }

    Integer[] getAllDots(String paramString) {
        if(!paramString.contains(DOTS)) return new Integer[0];
        int counter = 0;
        char dots = DOTS.charAt(0);
        ArrayList<Integer> result = new ArrayList<>();
        for(int i = 0; i < paramString.length(); i++) {
            char regex = paramString.charAt(i);
            if(regex == '(') {
                counter++;
            } else if (regex == ')') {
                counter--;
            } else if (regex == dots && counter == 0) {
                result.add(i);
            }
        }
        return result.toArray(new Integer[0]);
    }

    public abstract Function clone();
    public abstract Function clone(Namespace newNamespace);

    @Override
    public Set<String> variableCodecs() {
        return namespace.instanceNames();
    }

    @Override
    public void assign(String codec, float value) {
        if(namespace.containsInstance(codec))
            namespace.getInstance(codec).assign(codec, value);
    }

    @Override
    public boolean containsVar(String codec) {
        return namespace.containsInstance(codec);
    }

    @Override
    public float getValue(String codec) {
        if(!containsVar(codec)) throw new FormulaSynatxError(this, 0);
        return namespace.getInstance(codec).getValue(codec);
    }

    @Override
    public boolean hasVar() {
        return namespace.instanceVarSize() > 0;
    }

    @Override
    public void preTreatment() {
        for(Formula formula : params) {
            if(formula instanceof Pretreatable pretreatable)
                pretreatable.preTreatment();
        }
    }

    public List<Formula> getParams() {
        return params;
    }

    public int paramCount() {
        return params.size();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Function function)) return false;
        return function.toString().equals(toString());
    }

    @Override
    public Namespace getNamespace() {
        return namespace;
    }
}
