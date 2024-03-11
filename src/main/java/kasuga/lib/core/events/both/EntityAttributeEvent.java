package kasuga.lib.core.events.both;

import kasuga.lib.KasugaLib;
import kasuga.lib.registrations.registry.SimpleRegistry;
import kasuga.lib.registrations.common.EntityReg;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashSet;

public class EntityAttributeEvent {

    @SubscribeEvent
    public static void entityAttributeCreation(EntityAttributeCreationEvent event) {
        for(SimpleRegistry registry : KasugaLib.STACKS.getRegistries().values()) {
            HashSet<EntityReg<? extends LivingEntity>> set = registry.getCachedLivingEntities();
            set.forEach(reg -> event.put(reg.getType(), reg.getAttributeSupplier().build()));
        }
    }
}
