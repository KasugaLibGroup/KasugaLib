package kasuga.lib.core.client.render.curve;

import com.google.common.collect.Lists;
import interpreter.Code;
import interpreter.compute.data.Namespace;
import interpreter.compute.infrastructure.Formula;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.client.render.texture.Vec2f;
import kasuga.lib.core.util.data_type.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FunctionCurveTemplate implements CurveTemplate {
    private final Namespace namespace;
    private Formula formula;
    private @NotNull String independentVar;
    private float left, right, step;
    private final List<Vec2f> cache;
    public FunctionCurveTemplate(String function, float left, float right, float step) {
        this.namespace = new Namespace(Code.root());
        formula = namespace.decodeFormula(function);
        this.left = left;
        this.right = right;
        this.step = step;
        this.cache = Lists.newArrayList();
        independentVar = "x";
    }

    public void setIndependentVar(@NotNull String independentVar) {
        this.independentVar = independentVar;
    }

    public void setLeft(float left) {
        this.left = left;
    }

    public void setRight(float right) {
        this.right = right;
    }

    public void setStep(float step) {
        this.step = step;
    }

    public void setFunction(String formula) {
        this.formula = namespace.decodeFormula(formula);
    }

    @Override
    public List<Vec2f> getPointList() {
        return cache;
    }

    public void compile() {
        cache.clear();
        float length = right - left;
        if (length * step < 0) {
            KasugaLib.MAIN_LOGGER.error("Predictions are not likely to be fulfilled.");
            return;
        }
        boolean flag = length < 0;
        for (float i = left; flag ? (i > this.right) : (i < right); i += step) {
            namespace.assign(independentVar, i);
            float result = formula.getResult();
            getPointList().add(new Vec2f(i, result));
        }
    }

    @Override
    public float getStep() {
        return step;
    }

    @Override
    public Pair<Float, Float> getRange() {
        return Pair.of(left, right);
    }
}
