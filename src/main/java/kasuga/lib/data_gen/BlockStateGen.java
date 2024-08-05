package kasuga.lib.data_gen;

import com.google.gson.JsonObject;
import kasuga.lib.core.annos.Beta;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Collection;
import java.util.HashMap;
import java.util.function.Supplier;

@Beta
public class BlockStateGen<T extends Block> implements Generator<Block> {
    private final Supplier<T> block;
    private T cachedBlock = null;
    private Collection<Property<?>> cachedProps = null;

    public BlockStateGen(Supplier<T> block) {
        this.block = block;
    }

    public void init() {
        cachedBlock = block.get();
        cachedProps = cachedBlock.getStateDefinition().getProperties();
    }

    @Override
    public JsonObject getJson() {
        return null;
    }

    @Override
    public String getidentifier() {
        return null;
    }

    @Override
    public Class<T> generatorClass() {
        return (Class<T>) cachedBlock.getClass();
    }
}
