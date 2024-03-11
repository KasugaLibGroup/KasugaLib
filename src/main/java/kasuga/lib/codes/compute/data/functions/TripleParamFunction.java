package kasuga.lib.codes.compute.data.functions;

import kasuga.lib.codes.compute.data.Namespace;
import kasuga.lib.codes.compute.exceptions.FormulaSynatxError;

public class TripleParamFunction extends Function {

    final Computer computer;
    public TripleParamFunction(String codec, Namespace namespace, Computer computer) {
        super(codec, namespace);
        this.computer = computer;
    }

    @Override
    public float operate() {
        if(params.size() < 3) throw new FormulaSynatxError(this, 0);
        return computer.getResult(params.get(0).getResult(), params.get(1).getResult(), params.get(2).getResult());
    }

    @Override
    public Function clone() {
        return new TripleParamFunction(this.codec, namespace, this.computer);
    }

    @Override
    public Function clone(Namespace newNamespace) {
        return new TripleParamFunction(this.codec, namespace, this.computer);
    }

    public interface Computer {
        float getResult(float param1, float param2, float param3);
    }
}
