package kasuga.lib.registrations.common;

import kasuga.lib.registrations.Reg;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nonnull;

public class SoundReg extends Reg {
    @Nonnull public final ResourceLocation soundFile;
    private RegistryObject<SoundEvent> registryObject = null;
    public SoundReg(String registrationKey, @Nonnull ResourceLocation soundFileLocation) {
        super(registrationKey);
        this.soundFile = soundFileLocation;
    }

    @Override
    public Reg submit(SimpleRegistry registry) {
        registryObject = registry.sound().register(registrationKey, () -> SoundEvent.createVariableRangeEvent(soundFile));
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
