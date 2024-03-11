package kasuga.lib.codes.compute.data.functions;

import kasuga.lib.codes.compute.data.Namespace;
import kasuga.lib.codes.compute.exceptions.FormulaSynatxError;

public class DoublePrarmFunction extends Function {

    final Computer computer;
    public DoublePrarmFunction(String codec, Namespace namespace, Computer computer) {
        super(codec, namespace);
        this.computer = computer;
    }

    @Override
    public float operate() {
        if(params.size() < 2) throw new FormulaSynatxError(this, 0);
        return computer.getResult(params.get(0).getResult(), params.get(1).getResult());
    }

    @Override
    public Function clone() {
        return new DoublePrarmFunction(codec, namespace, computer);
    }

    @Override
    public Function clone(Namespace namespace) {
        return new DoublePrarmFunction(codec, namespace, computer);
    }

    public interface Computer{
        float getResult(float param1, float param2);
    }
}
