package kasuga.lib.registrations;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;

public interface EntityRendererBuilder<T extends Entity> {
    EntityRenderer<T> build(EntityRendererProvider.Context context);
}
