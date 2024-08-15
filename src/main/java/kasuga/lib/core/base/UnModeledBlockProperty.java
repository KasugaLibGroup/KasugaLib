package kasuga.lib.core.base;

import kasuga.lib.core.annos.Util;
import net.minecraft.world.level.block.state.BlockBehaviour;

import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

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
