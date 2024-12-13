package kasuga.lib.core.create.graph;

import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackNode;
import kasuga.lib.core.util.data_type.Couple;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class GraphWalker {
    public static void walk(
            TrackGraph graph,
            List<Couple<TrackNode>> frontier,
            Function<
                    Set<Map.Entry<TrackNode, TrackEdge>>,
                    Collection<Map.Entry<TrackNode, TrackEdge>>
            > sortFunction,
            Predicate<TrackEdge> edgePredicate
    ){
        Set<TrackEdge> visited = new HashSet<>();
        while(!frontier.isEmpty()){
            Couple<TrackNode> couple = frontier.remove(0);
            TrackNode currentNode = couple.getFirst();
            TrackNode previousNode = couple.getSecond();
            Map<TrackNode, TrackEdge> map = graph.getConnectionsFrom(currentNode);
            Collection<Map.Entry<TrackNode, TrackEdge>> sorted = sortFunction.apply(map.entrySet());
            for(Map.Entry<TrackNode, TrackEdge> entry : sorted){
                TrackNode nextNode = entry.getKey();
                TrackEdge edge = entry.getValue();
                if(nextNode == previousNode)
                    continue;
                if(!visited.add(edge))
                    continue;
                TrackEdge oppositeEdge = graph.getConnectionsFrom(nextNode)
                        .get(currentNode);
                visited.add(oppositeEdge);
                if(!edgePredicate.test(edge))
                    continue;
                frontier.add(new Couple<>(nextNode, currentNode));
            }
        }
    }
}
