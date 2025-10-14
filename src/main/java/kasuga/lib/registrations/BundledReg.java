package kasuga.lib.registrations;

import kasuga.lib.core.annos.Mandatory;
import kasuga.lib.core.annos.Optional;
import kasuga.lib.registrations.registry.SimpleRegistry;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * BundledReg是一个批量注册的注册类。我们可以在一个注册中注册许多元素。
 * 比如，我们想注册100个简单的方块，我们可以使用调用{@link BundledReg#element(String)}这个类100次来放入所有的注册键。
 * @param <T> 这个注册的名字。
 * BundledReg is designed for registration in batch. We could register many element in one reg.
 * For example, we want to register 100 simple blocks, so we could use this just call {@link BundledReg#element(String)}
 * for 100 times to put all registrationKey in.
 * @param <T> The Reg type for batch registration.
 */
public class BundledReg<T extends Reg> extends Reg {
    private final LinkedList<RegAction<T>> actions;
    private final HashMap<String, LinkedList<RegAction<T>>> specificActions;
    private final LinkedList<RegDrive<T>> specificDrive;
    private final LinkedList<String> elements;
    private final HashMap<String, T> regs;
    private RegFactory<T> factory = null;

    /**
     * 用这个类来初始化一个批量注册。
     * @param registrationKey 这个注册的名字。
     * Use this to init a bundled reg.
     * @param registrationKey the name of this reg.
     */
    public BundledReg(String registrationKey) {
        super(registrationKey);
        actions = new LinkedList<>();
        specificActions = new HashMap<>();
        specificDrive = new LinkedList<>();
        elements = new LinkedList<>();
        regs = new HashMap<>();
    }

    /**
     * 这个必须在你调用任何元素之前通过 {@link BundledReg#element(String)}被调用
     * @param factory 一个注册初始化的工厂lambda。
     * @return 自身
     * This must be called before you call any elements via {@link BundledReg#element(String)}
     * while that method would use the factory we have given in this.
     * @param factory A factory lambda for registration initial.
     * @return self.
     */
    @Mandatory
    public BundledReg<T> factory(RegFactory<T> factory) {
        this.factory = factory;
        return this;
    }

    /**
     * 堆叠一个元素进行注册。这个必须在 {@link BundledReg#factory(RegFactory)} 之后被调用
     * @param registrationKey 你的元素的注册键。
     * @return 自身
     * Stack an element in for registration. This must be called after {@link BundledReg#factory(RegFactory)}
     * @param registrationKey the registration key of your element.
     * @return self.
     */
    @Optional
    public BundledReg<T> element(String registrationKey) {
        elements.add(registrationKey);
        regs.put(registrationKey, factory.build(registrationKey));
        return this;
    }

    /**
     * Stack an action in for registration. Action is a lambda refers what you would like to deal with all registration
     * elements. Please pay attention to the annotations under other BundledReg class.
     * @param action the element you would like to give.
     * @return self.
     */
    @Optional
    public BundledReg<T> action(RegAction<T> action) {
        actions.add(action);
        return this;
    }

    /**
     * Stack a specific action in for registration. You can apply an action for on specific registration, so you could
     * apply some unique config for some of your registrations.
     * @param key the key of this specific action aim at.
     * @param action the specific action you would like to give.
     * @return self.
     */
    @Optional
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

    /**
     * 堆叠一个动作驱动进行注册。你可以通过调用这个方法为一些特定的注册应用一些动作。如果你想为多个注册添加多个函数，请使用这个。
     * @param drive 你想要提供的动作驱动。
     * @return 自身
     * Stack an action drive in for registration. You can apply some action for some specific registrations via calling
     * this. If you want to add multiple functions for multiple regs, use this.
     * @param drive the action drive you would like to give.
     * @return self.
     */
    @Optional
    public BundledReg<T> drive(RegDrive<T> drive) {
        this.specificDrive.add(drive);
        return this;
    }

    @Mandatory
    @Override
    public BundledReg<T> submit(SimpleRegistry registry) {
        if (factory == null) return this;
        for (String key : elements) {
            T reg = regs.get(key);
            for (RegAction<T> action : actions) action.action(reg);

            LinkedList<RegAction<T>> specific = specificActions.getOrDefault(key, null);
            for (RegDrive<T> drive : specificDrive)
                drive.action(key, reg);
            if (specific != null)
                for (RegAction<T> action : specific) action.action(reg);
            reg.submit(registry);
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

    public interface RegDrive<T extends Reg> {
        T action(String key, T reg);
    }

    public interface RegFactory<T extends Reg> {
        T build(String registrationKey);
    }
}
