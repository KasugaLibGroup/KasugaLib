package kasuga.lib.mixins.mixin.create;

import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackNode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value= TrackGraph.class, remap = false)
public interface TrackGraphAccessor {
    @Accessor(value = "connectionsByNode")
    public Map<TrackNode, Map<TrackNode, TrackEdge>> getConnectionsByNode();
}
