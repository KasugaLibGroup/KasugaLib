package kasuga.lib.core.base;

import kasuga.lib.core.annos.Util;
import net.minecraft.world.level.block.state.BlockBehaviour;

import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

/**
 * This class is used for get properties that don't link to the block state json file.
 * For its full codes, see {@link kasuga.lib.mixins.mixin.client.MixinBlockModelShaper}
 * After some times of modding, I found that this type of property is really necessary.
 * @param <J> the class of values of the original property.
 * @param <T> the class of the original property.
 */
@Util
public class UnModeledBlockProperty<J extends Comparable<J>, T extends Property<J>> extends Property<J> {

    public final T prop;
    public UnModeledBlockProperty(T prop) {
        super(prop.getName(), prop.getValueClass());
        this.prop = prop;
    }

    public static <J extends Comparable<J>, T extends Property<J>> UnModeledBlockProperty<J, T> create(T prop) {
        return new UnModeledBlockProperty<J, T>(prop);
    }

    @Override
    public @NotNull Collection<J> getPossibleValues() {
        return prop.getPossibleValues();
    }

    @Override
    public @NotNull String getName(Comparable p_61696_) {
        return prop.getName();
    }

    @Override
    public @NotNull Optional<J> getValue(String pValue) {
        return prop.getValue(pValue);
    }
}
