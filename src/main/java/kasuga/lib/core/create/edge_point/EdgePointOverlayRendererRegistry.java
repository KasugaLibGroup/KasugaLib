package kasuga.lib.core.create.edge_point;

import com.simibubi.create.content.trains.graph.EdgePointType;

import java.util.HashMap;

public class EdgePointOverlayRendererRegistry {
    protected static HashMap<EdgePointType<?>, EdgePointOverlayRenderer> RENDERERS = new HashMap<>();
    public static EdgePointOverlayRenderer getRendererFor(EdgePointType<?> type) {
        return RENDERERS.get(type);
    }

    public static void register(EdgePointType<?> type, EdgePointOverlayRenderer renderer){
        RENDERERS.put(type, renderer);
    }
}
