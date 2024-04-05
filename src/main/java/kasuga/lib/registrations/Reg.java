package kasuga.lib.registrations;

import kasuga.lib.core.annos.Mandatory;
import kasuga.lib.registrations.registry.SimpleRegistry;

/**
 * Reg is the core class for KasugaLib style registration. We use this to complete most kinds of registrations.
 * For more info, see {@link SimpleRegistry}
 */
public abstract class Reg {

    public final String registrationKey;

    public Reg(String registrationKey) {
        this.registrationKey = registrationKey;
    }

    /**
     * This method must be called after all config has been given. That means this method should be the last part of
     * any reg element. Call this, we would hand all elements in to forge and minecraft.
     * @param registry the mod SimpleRegistry.
     * @return self.
     */
    @Mandatory
    public abstract Reg submit(SimpleRegistry registry);
    public abstract String getIdentifier();
    public String toString() {
        return getIdentifier() + "." + registrationKey;
    }
}
