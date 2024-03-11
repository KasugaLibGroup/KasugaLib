package kasuga.lib.codes.compute.data;

import kasuga.lib.codes.compute.infrastructure.Assignable;
import kasuga.lib.codes.compute.infrastructure.Formula;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Variable implements Formula, Assignable {
    private String codec;
    private float value;
    private boolean flip = false;
    private final Namespace namespace;

    public Variable(String codec, Namespace namespace) {
        this.codec = codec;
        this.namespace = namespace;
    }

    public Variable(String codec, Namespace namespace, float value) {
        this(codec, namespace);
        this.value = value;
    }

    public String appendCodec(String append) {
        if(!append.equals("")) codec = codec + "." + append;
        return codec;
    }

    public static boolean isVar(String input) {
        return input.replaceAll("([a-z])([a-z1-9])*(_([a-z1-9]+))*", "").equals("");
    }

    @Override
    public String getString() {
        return (flip ? "-" : "") + codec;
    }

    @Override
    public String getIdentifier() {
        return "var";
    }

    @Override
    public float getResult() {
        return flip ? - value : value;
    }

    @Override
    public List<Formula> getElements() {
        return List.of(this);
    }

    @Override
    public boolean isAtomic() {
        return true;
    }

    @Override
    public boolean shouldRemove() {
        return false;
    }

    @Override
    public void fromString(String string) {
        if(isVar(string)) this.codec = string;
    }

    @Override
    public Formula clone() {
        return new Variable(this.codec, this.namespace, this.value);
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
    public Namespace getNamespace() {
        return namespace;
    }

    @Override
    public Set<String> variableCodecs() {
        return Set.of(this.codec);
    }

    @Override
    public void assign(String codec, float value) {
        if(codec.equals(this.codec))
            this.value = value;
    }

    @Override
    public boolean containsVar(String codec) {
        return codec.equals(this.codec);
    }

    @Override
    public float getValue(String codec) {
        return getResult();
    }

    @Override
    public boolean hasVar() {
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Variable variable)) return false;
        return variable.codec.equals(codec);
    }
}
