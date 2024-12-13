package kasuga.lib.core.create.graph;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.content.trains.graph.TrackNodeLocation;
import kasuga.lib.core.util.data_type.Couple;
import net.minecraft.world.phys.Vec3;

import java.util.*;
import java.util.function.Predicate;

public class GraphSimplifier {
    public static class SimplifiedTrackVertex {
        private final TrackNode node;

        SimplifiedTrackVertex(TrackNode node){
            this.node = node;
        }
        Set<SimplifiedTrackEdge> edges = new HashSet<>();
        public void addEdge(SimplifiedTrackEdge edge){
            if(edge.vertices[0] == null)
                edge.vertices[0] = this;
            else if (edge.vertices[1] == null) {
                edge.vertices[1] = this;
            } else throw new IllegalStateException("Edge already has two vertices");
            edges.add(edge);
        }

        public void removeEdge(SimplifiedTrackEdge edge){
            if(edge.vertices[0] == this)
                edge.vertices[0] = null;
            else if(edge.vertices[1] == this)
                edge.vertices[1] = null;
            else throw new IllegalStateException("Edge does not contain this vertex");
            edges.remove(edge);
        }

        public JsonObject toJSON() {
            JsonObject json = new JsonObject();
            Vec3 location = node.getLocation().getLocation();
            json.addProperty("x", location.x());
            json.addProperty("y", location.y());
            json.addProperty("z", location.z());
            return json;
        }
    }
    public static class SimplifiedTrackEdge {
        Set<TrackEdge> edges = new HashSet<>(); // Only for construction.
        SimplifiedTrackVertex[] vertices = new SimplifiedTrackVertex[2];
        BitSet flags = new BitSet();

        public SimplifiedTrackVertex getFirstAvailableVertex(){
            if(vertices[0] != null)
                return vertices[0];
            if(vertices[1] != null)
                return vertices[1];
            return null;
        }

        public JsonObject toJSON(HashMap<SimplifiedTrackVertex, Integer> vertexTracker) {
            JsonObject json = new JsonObject();
            json.addProperty("from", vertexTracker.get(vertices[0]));
            json.addProperty("to", vertexTracker.get(vertices[1]));
            json.addProperty("is_turn", flags.get(FLAG_IS_TURN));
            return json;
        }
    }

    public static int FLAG_IS_TURN = 0;

    public static class SimplifiedSubGraph{
        Set<SimplifiedTrackVertex> vertices = new HashSet<>();
        Set<SimplifiedTrackEdge> edges = new HashSet<>();

        public SimplifiedTrackVertex createVertex(TrackNode node){
            SimplifiedTrackVertex vertex = new SimplifiedTrackVertex(node);
            vertices.add(vertex);
            return vertex;
        }

        public void addEdge(SimplifiedTrackEdge edge){
            edges.add(edge);
        }

        public boolean notifyCleanup(SimplifiedTrackVertex vertex) {
            boolean isEmptied = vertex.edges.isEmpty();
            if(isEmptied)
                vertices.remove(vertex);
            return isEmptied;
        }

        public JsonObject toJSON(){
            JsonObject json = new JsonObject();
            json.addProperty("type", "simplified");
            JsonArray verticesArray = new JsonArray();
            HashMap<SimplifiedTrackVertex, Integer> vertexTracker = new HashMap<>();
            int id = 0;
            for (SimplifiedTrackVertex vertex : vertices) {
                verticesArray.add(vertex.toJSON());
                vertexTracker.put(vertex, id++);
            }
            JsonArray edgesArray = new JsonArray();
            for (SimplifiedTrackEdge edge : edges) {
                edgesArray.add(edge.toJSON(vertexTracker));
            }
            json.add("vertices", verticesArray);
            json.add("edges", edgesArray);
            return json;
        }

        public void removeEdge(SimplifiedTrackEdge right) {
            edges.remove(right);
        }
    }

    public static SimplifiedSubGraph simpilify(TrackGraph graph){
        TrackEdge beginerEdge = findTrackWithoutBezier(graph);
        if(beginerEdge == null)
            return new SimplifiedSubGraph();
        List<Couple<TrackNode>> frontier = new ArrayList<>();
        frontier.add(new Couple<>(beginerEdge.node1, null));
        return simpilify(graph, frontier, (edge)->true);
    }

    public static SimplifiedSubGraph simpilify(
            TrackGraph graph,
            List<Couple<TrackNode>> frontier,
            Predicate<TrackEdge> edgeBoundaryPredicate
    ){
        SimplifiedSubGraph simplifiedGraph = new SimplifiedSubGraph();


        HashMap<TrackNode, SimplifiedTrackVertex> nodes = new HashMap<>();

        Predicate<TrackEdge> reader = (edge)->{
            SimplifiedTrackVertex from = getOrCreateVertex(simplifiedGraph, nodes, edge.node1);
            SimplifiedTrackVertex to = getOrCreateVertex(simplifiedGraph, nodes, edge.node2);
            SimplifiedTrackEdge simplifiedEdge = new SimplifiedTrackEdge();
            simplifiedEdge.flags.set(FLAG_IS_TURN, edge.isTurn());
            from.addEdge(simplifiedEdge);
            to.addEdge(simplifiedEdge);
            simplifiedEdge.edges.add(edge);
            simplifiedGraph.addEdge(simplifiedEdge);
            return edgeBoundaryPredicate.test(edge);
        };

        GraphWalker.walk(graph, frontier , map -> map, reader);

        List<SimplifiedTrackVertex> shouldCleanup = new ArrayList<>();

        for (SimplifiedTrackVertex vertex : simplifiedGraph.vertices) {
            if(vertex.edges.size() != 2)
                continue;
            if(
                    vertex.edges.stream().anyMatch(edge -> edge.flags.get(FLAG_IS_TURN))
            )
                continue;

            Iterator<SimplifiedTrackEdge> iterator = vertex.edges.iterator();

            SimplifiedTrackEdge left = iterator.next();
            SimplifiedTrackEdge right = iterator.next();
            vertex.removeEdge(left);
            vertex.removeEdge(right);
            shouldCleanup.add(vertex);
            SimplifiedTrackVertex nextVertex = right.getFirstAvailableVertex();
            nextVertex.removeEdge(right);
            nextVertex.addEdge(left);
            left.edges.addAll(right.edges);
            simplifiedGraph.removeEdge(right);
        }

        for (SimplifiedTrackVertex vertex : shouldCleanup) {
            simplifiedGraph.notifyCleanup(vertex);
        }

        return simplifiedGraph;
    }

    public static SimplifiedTrackVertex getOrCreateVertex(
            SimplifiedSubGraph graph,
            Map<TrackNode, SimplifiedTrackVertex> nodes,
            TrackNode node
    ){
        return nodes.computeIfAbsent(node, (n)->{
            SimplifiedTrackVertex vertex = graph.createVertex(n);
            return vertex;
        });
    }

    public static TrackEdge findTrackWithoutBezier(TrackGraph graph){
        for(TrackNodeLocation nodeLocation : graph.getNodes()){
            TrackNode node = graph.locateNode(nodeLocation);
            if(node == null)
                continue;

            for(var edgeEntry : graph.getConnectionsFrom(node).entrySet()) {
                TrackNode next = edgeEntry.getKey();
                TrackEdge edge = edgeEntry.getValue();
                if(!edge.isTurn()){
                    return edge;
                }
            }
        }
        return null;
    }
}
