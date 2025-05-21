package kasuga.lib.mixins.mixin.client;

import kasuga.lib.core.base.UnModeledBlockProperty;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Map;

@Mixin(BlockModelShaper.class)
public abstract class MixinBlockModelShaper {

    private static <T extends Comparable<T>> String getValue(Property<T> pProperty, Comparable<?> pValue) {
        return pProperty.getName((T)pValue);
    }

    /**
     * @author MegumiKasuga
     * @reason To make the {@link UnModeledBlockProperty} usable.
     * That class works for special properties which wouldn't show in block states.
     */
    @Overwrite
    public static String statePropertiesToString(Map<Property<?>, Comparable<?>> pPropertyValues) {
        StringBuilder stringbuilder = new StringBuilder();

        for(Map.Entry<Property<?>, Comparable<?>> entry : pPropertyValues.entrySet()) {
            Property<?> property = entry.getKey();

            // Codes Added.
            if (property instanceof UnModeledBlockProperty) continue;

            if (stringbuilder.length() != 0) {
                stringbuilder.append(',');
            }

            stringbuilder.append(property.getName());
            stringbuilder.append('=');
            stringbuilder.append(getValue(property, entry.getValue()));
        }

        return stringbuilder.toString();
    }

}
