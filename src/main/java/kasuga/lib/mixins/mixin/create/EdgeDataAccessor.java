package kasuga.lib.mixins.mixin.create;

import com.simibubi.create.content.trains.graph.EdgeData;
import com.simibubi.create.content.trains.graph.TrackEdge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = EdgeData.class, remap = false)
public interface EdgeDataAccessor {
    @Accessor("edge")
    public TrackEdge getEdge();
}
