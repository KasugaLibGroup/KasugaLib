package kasuga.lib.registrations.common;

import kasuga.lib.core.annos.Mandatory;
import kasuga.lib.registrations.Reg;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nonnull;

/**
 * 用此注册机来注册你的自定义声音类型和声音事件到minecraft。
 * Use this reg to register your custom sound type and sound event to minecraft.
 */
public class SoundReg extends Reg {
    @Nonnull public final ResourceLocation soundFile;
    private RegistryObject<SoundEvent> registryObject = null;

    /**
     * 创建一个声音注册机。
     * @param registrationKey 你的声音注册机的键。
     * @param soundFileLocation 你的声音文件的资源位置。
     * Create a sound reg.
     * @param registrationKey key of your sound reg.
     * @param soundFileLocation the resource location of your sound file.
     */
    public SoundReg(String registrationKey, @Nonnull ResourceLocation soundFileLocation) {
        super(registrationKey);
        this.soundFile = soundFileLocation;
    }

    /**
     * 将你的配置提交到minecraft。
     * @param registry mod的SimpleRegistry。
     * @return 自身
     * Submit your config to minecraft.
     * @param registry the mod SimpleRegistry.
     * @return self.
     */
    @Override
    @Mandatory
    public SoundReg submit(SimpleRegistry registry) {
        registryObject = registry.sound().register(registrationKey, () -> new SoundEvent(soundFile));
        return this;
    }

    public RegistryObject<SoundEvent> getRegistryObject() {
        return registryObject;
    }

    public SoundEvent getSoundEvent() {
        return registryObject == null ? null : registryObject.get();
    }

    @Override
    public String getIdentifier() {
        return "sound";
    }
}
