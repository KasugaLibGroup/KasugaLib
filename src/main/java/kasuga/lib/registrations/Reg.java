package kasuga.lib.registrations;

import kasuga.lib.core.annos.Mandatory;
import kasuga.lib.registrations.common.ItemReg;
import kasuga.lib.registrations.exception.RegistryElementNotPresentException;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.CrashReport;
import net.minecraft.client.Minecraft;

/**
 * Reg是KasugaLib注册的核心类。我们使用它来完成大多数类型的注册。
 * 更多信息，请参见{@link SimpleRegistry}
 * Reg is the core class for KasugaLib style registration. We use this to complete most kinds of registrations.
 * For more info, see {@link SimpleRegistry}
 */
public abstract class Reg {

    public final String registrationKey;

    public Reg(String registrationKey) {
        this.registrationKey = registrationKey;
    }

    /**
     * 这个方法必须在所有配置项提交后被调用。这意味着这个方法应该是任何注册元素的最后一部分。调用此方法后，我们将把所有元素交给Forge和Minecraft。
     * @param registry mod的SimpleRegistry。
     * @return 自身
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

    public void crashOnNotPresent(Class<?> clazz, String regType, String method) {
        RegistryElementNotPresentException exception =
                RegistryElementNotPresentException.of(clazz, regType, method);
        Minecraft.crash(CrashReport.forThrowable(exception, "Oops!"));
    }
}
