package kasuga.lib.core.graph;

import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class GraphAlgo {
    public static <V, E> ArrayDeque<Graph<V, E>> split(Graph<V, E> graph, Supplier<Graph<V, E>> graphSupplier) {
        ConnectivityInspector<V, E> inspector = new ConnectivityInspector<>(graph);
        List<Set<V>> connectedSets = inspector.connectedSets();
        ArrayDeque<Graph<V, E>> graphs = new ArrayDeque<>(connectedSets.size() + 1);

        for (Set<V> vertexSet : connectedSets) {
            Graph<V, E> subgraph = copyOfSubGraph(graph, graphSupplier, vertexSet);
            graphs.add(subgraph);
        }

        return graphs;
    }

    protected static <V, E> Graph<V, E> copyOfSubGraph(Graph<V, E> graph, Supplier<Graph<V, E>> graphSupplier, Set<V> verts) {
        Graph<V, E> subGraph = graphSupplier.get();
        for (V vertex : verts) {
            subGraph.addVertex(vertex);
        }

        for (E e : graph.edgeSet()) {
            V from = graph.getEdgeSource(e);
            V to = graph.getEdgeTarget(e);
            if (verts.contains(from) && verts.contains(to)) {
                subGraph.addEdge(from, to, e);
            }
        }

        return subGraph;
    }
}
