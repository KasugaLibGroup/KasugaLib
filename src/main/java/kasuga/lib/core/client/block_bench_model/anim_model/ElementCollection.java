package kasuga.lib.core.client.block_bench_model.anim_model;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public interface ElementCollection {

    HashMap<UUID, AnimElement> getChildren();

    default @Nullable AnimElement getChild(@NotNull UUID uuid) {
        return getChildren().getOrDefault(uuid, null);
    }

    default boolean hasChild(@NotNull UUID uuid) {
        return getChildren().containsKey(uuid);
    }

    default void addChild(@NotNull UUID uuid, @NotNull AnimElement child) {
        getChildren().put(uuid, child);
    }
}
