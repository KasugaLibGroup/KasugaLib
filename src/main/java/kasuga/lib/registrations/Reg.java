package kasuga.lib.registrations;

import kasuga.lib.registrations.registry.SimpleRegistry;

public abstract class Reg {
    public final String registrationKey;

    public Reg(String registrationKey) {
        this.registrationKey = registrationKey;
    }
    public abstract Reg submit(SimpleRegistry registry);
    public abstract String getIdentifier();

    public String toString() {
        return getIdentifier() + "." + registrationKey;
    }
}
