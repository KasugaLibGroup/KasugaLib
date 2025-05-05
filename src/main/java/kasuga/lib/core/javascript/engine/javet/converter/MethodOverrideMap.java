package kasuga.lib.core.javascript.engine.javet.converter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;

public class MethodOverrideMap {
    public HashMap<Integer, BitSet> converterMask = new HashMap<>();
    public HashMap<Integer, List<Method>> methods = new HashMap<>();

    public boolean isVoidReturn = true;

    public MethodOverrideMap(){}

    public void initIfAbsent(int parameterCount){

        converterMask
                .computeIfAbsent(parameterCount, (i)->{
                    BitSet bitSet = new BitSet();
                    bitSet.set(0, i,true);
                    return bitSet;
                });

        methods.computeIfAbsent(parameterCount, (i)->new ArrayList<>());
    }
}
