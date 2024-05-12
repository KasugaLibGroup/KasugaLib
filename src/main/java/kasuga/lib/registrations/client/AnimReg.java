package kasuga.lib.registrations.client;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.client.animation.data.Animation;
import kasuga.lib.registrations.Reg;
import kasuga.lib.registrations.exception.RegistryElementNotPresentException;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.resources.ResourceLocation;

import java.util.logging.Logger;

public class AnimReg extends Reg {
    private final ResourceLocation location;
    Animation animation = null;
    public AnimReg(String animationKey, ResourceLocation location) {
        super(animationKey);
        this.location = location;
    }

    @Override
    public AnimReg submit(SimpleRegistry registry) {
        registry.animation().put(this.registrationKey, this);
        return this;
    }

    public void onResourceLoading() {
        this.animation = Animation.decode(location, registrationKey).orElse(null);
        if (animation == null)
            crashOnNotPresent(AnimReg.class, getIdentifier(), "There seems to be some problem with your anim!");
    }

    public Animation getInstance() {
        if (animation == null) {
            KasugaLib.MAIN_LOGGER.error("Called too early!");
            return null;
        }
        return animation.clone();
    }

    @Override
    public String getIdentifier() {
        return "animation";
    }
}
