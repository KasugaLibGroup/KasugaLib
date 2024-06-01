package kasuga.lib.registrations.common;

import kasuga.lib.registrations.Reg;
import kasuga.lib.registrations.registry.SimpleRegistry;

public class ArgumentTypeReg extends Reg {
    public ArgumentTypeReg(String registrationKey) {
        super(registrationKey);
    }

    @Override
    public ArgumentTypeReg submit(SimpleRegistry registry) {
        return this;
    }

    @Override
    public String getIdentifier() {
        return null;
    }
}
