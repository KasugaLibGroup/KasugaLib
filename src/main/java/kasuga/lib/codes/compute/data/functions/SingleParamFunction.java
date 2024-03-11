package kasuga.lib.codes.compute.data.functions;

import kasuga.lib.codes.compute.data.Namespace;
import kasuga.lib.codes.compute.exceptions.FormulaSynatxError;

public class SingleParamFunction extends Function {
    final Computer computer;
    public SingleParamFunction(String codec, Namespace namespace, Computer computer) {
        super(codec, namespace);
        this.computer = computer;
    }

    @Override
    public float operate() {
        if(params.isEmpty()) throw new FormulaSynatxError(this, 0);
        return computer.getResult(params.get(0).getResult());
    }

    @Override
    public SingleParamFunction clone() {
        return new SingleParamFunction(this.codec, namespace, computer);
    }

    @Override
    public Function clone(Namespace newNamespace) {
        return new SingleParamFunction(codec, newNamespace, computer);
    }

    public interface Computer {
        float getResult(float input);
    }
}
