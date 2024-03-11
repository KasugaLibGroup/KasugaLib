package kasuga.lib.core.client.animation.infrastructure;

import kasuga.lib.codes.Code;
import kasuga.lib.codes.compute.data.Namespace;
import kasuga.lib.codes.compute.infrastructure.Assignable;
import kasuga.lib.codes.logic.data.LogicalBool;
import kasuga.lib.codes.logic.infrastructure.LogicalAssignable;
import kasuga.lib.codes.logic.infrastructure.LogicalData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;


@OnlyIn(Dist.CLIENT)
public class Condition extends AnimationElement implements AnimAssignable {
    @Nonnull LogicalData data;
    private final Namespace namespace;
    public Condition(String key, Namespace namespace, String code) {
        super(key);
        data = namespace.decodeLogical(code);
        this.namespace = namespace;
    }

    public Condition(String key, Namespace namespace, LogicalData data) {
        super(key);
        this.data = data;
        this.namespace = namespace;
    }

    public Condition(String key, Namespace namespace, boolean data) {
        this(key, namespace, data ? "True" : "False");
    }

    public static Condition defaultTrue(String key, Namespace namespace) {
        return new Condition(key, namespace, "True");
    }

    public static Condition defaultFalse(String key, Namespace namespace) {
        return new Condition(key, namespace, "False");
    }

    public void fromString(String code) {
        this.data = namespace.decodeLogical(code);
    }

    public boolean result() {
        return data.getResult();
    }

    public @NotNull LogicalData getData() {
        return data;
    }

    public boolean isAssignable() {
        return data instanceof LogicalAssignable;
    }

    @Override
    public Namespace getNamespace() {
        return namespace;
    }
    @Override
    public boolean isValid() {
        return true;
    }

    public void assign(String codec, float value) {
        namespace.assign(codec, value);
    }

    @Override
    public void init() {}
}
