package kasuga.lib.core.graph;

import kasuga.lib.core.util.data_type.Pair;
import org.jgrapht.Graph;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

public abstract class GraphManager<K, V, E> {
    protected HashMap<K, Graph<V, E>> graphs = new HashMap<>();
    protected abstract Graph<V, E> createGraph();

    protected abstract K createKey();

    public K createNode(V vertex) {
        return createNode(createKey(), vertex);
    }

    public K createNode(K key, V vertex) {
        Graph<V, E> graph = createGraph();
        this.graphs.put(key, graph);
        graph.addVertex(vertex);
        return key;
    }

    public void removeNode(V vertex) {
        K key = getGraphIdByNode(vertex);
        if(key == null)
            return;
        Graph<V, E> graph = this.graphs.remove(key);
        if(graph.removeVertex(vertex)){
            propagateSplit(key, (Graph<V, E>) graph);
        }
    }

    private void propagateSplit(K key, Graph<V, E> graph) {
        ArrayDeque<Graph<V,E>> newGraphs = GraphAlgo.split(graph, this::createGraph);
        this.graphs.put(key, newGraphs.pop());
        for (Graph<V, E> newGraph : newGraphs) {
            this.graphs.put(createKey(), newGraph);
        }
    }

    public void addConnection(V source, V target, E edge) {
        K sourceKey = getGraphIdByNode(source);
        K targetKey = getGraphIdByNode(target);
        if(sourceKey == targetKey) {
            Graph<V, E> graph = this.graphs.get(sourceKey);
            graph.addEdge(source, target, edge);
        } else {
            Graph<V, E> sourceGraph = this.graphs.get(sourceKey);
            Graph<V, E> targetGraph = this.graphs.get(targetKey);
            for (V v : targetGraph.vertexSet()) {
                sourceGraph.addVertex(v);
            }
            for (E e : targetGraph.edgeSet()) {
                V from = targetGraph.getEdgeSource(e);
                V to = targetGraph.getEdgeTarget(e);
                sourceGraph.addEdge(from, to, e);
            }
            sourceGraph.addEdge(source, target, edge);
            this.graphs.remove(targetKey);
        }
    }

    public void removeConnection(V vertex, V target) {
        K graphKey = getGraphIdByNode(vertex);
        if(graphKey == null)
            return;
        Graph<V, E> graph = this.graphs.get(graphKey);
        E edgesToRemove = graph.getEdge(vertex, target);
        graph.removeEdge(edgesToRemove);
        propagateSplit(graphKey, (Graph<V, E>) graph);
    }

    public void synchronizeNode(V vertex, Supplier<List<Pair<V, E>>> connectionsSupplier) {
        if(getGraphIdByNode(vertex) != null) {
            return;
        }
        K original = createNode(vertex);
        Graph<V, E> graph = this.graphs.get(original);
        List<Pair<V, E>> connections = connectionsSupplier.get();
        for (Pair<V, E> conn : connections) {
            K connGraphId = getGraphIdByNode(conn.getFirst());
            if(connGraphId != null) {
                this.addConnection(vertex, conn.getFirst(), conn.getSecond());
            } else {
                graph.addVertex(conn.getFirst());
                graph.addEdge(vertex, conn.getFirst(), conn.getSecond());
            }
        }
    }

    public K getGraphIdByNode(V vertex) {
        for (K key : this.graphs.keySet()) {
            Graph<V, E> graph = this.graphs.get(key);
            if(graph.containsVertex(vertex)) {
                return key;
            }
        }
        return null;
    }

    public Graph<V, E> getGraphByNode(V vertex) {
        K key = getGraphIdByNode(vertex);
        if(key == null)
            return null;
        return this.graphs.get(key);
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof GraphManager<?,?,?> other))
            return false;
        return this.graphs.equals(other.graphs);
    }
}
