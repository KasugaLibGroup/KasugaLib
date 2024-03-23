package kasuga.lib.registrations;

import kasuga.lib.registrations.registry.SimpleRegistry;

import java.util.HashMap;
import java.util.LinkedList;

public class BundledReg<T extends Reg> extends Reg {
    private final LinkedList<RegAction<T>> actions;
    private final HashMap<String, LinkedList<RegAction<T>>> specificActions;
    private final LinkedList<String> elements;
    private final HashMap<String, T> regs;
    private RegFactory<T> factory = null;
    public BundledReg(String registrationKey) {
        super(registrationKey);
        actions = new LinkedList<>();
        specificActions = new HashMap<>();
        elements = new LinkedList<>();
        regs = new HashMap<>();
    }

    public BundledReg<T> factory(RegFactory<T> factory) {
        this.factory = factory;
        return this;
    }

    public BundledReg<T> element(String registrationKey) {
        elements.add(registrationKey);
        return this;
    }

    public BundledReg<T> action(RegAction<T> action) {
        actions.add(action);
        return this;
    }

    public BundledReg<T> specificAction(String key, RegAction<T> action) {
        if (specificActions.containsKey(key)) {
            specificActions.get(key).add(action);
        } else {
            LinkedList<RegAction<T>> list = new LinkedList<>();
            list.add(action);
            specificActions.put(key, list);
        }
        return this;
    }

    @Override
    public BundledReg<T> submit(SimpleRegistry registry) {
        if (factory == null) return this;
        for (String key : elements) {
            T reg = factory.build(key);
            for (RegAction<T> action : actions) action.action(reg);

            LinkedList<RegAction<T>> specific = specificActions.getOrDefault(key, null);
            if (specific != null)
                for (RegAction<T> action : specific) action.action(reg);
            regs.put(key, (T) reg.submit(registry));
        }
        return this;
    }

    public HashMap<String, T> getElements() {
        return regs;
    }

    public T getElement(String key) {
        return regs.getOrDefault(key, null);
    }

    public boolean containsElement(String key) {
        return regs.containsKey(key);
    }

    @Override
    public String getIdentifier() {
        return "bundled";
    }

    public interface RegAction<T extends Reg> {
        T action(T reg);
    }

    public interface RegFactory<T extends Reg> {
        T build(String registrationKey);
    }
}
