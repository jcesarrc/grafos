package co.edu.poli.medgraph.grafo;

import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.collections15.Transformer;

public class GraphManager {

    private static IGraph graph = null;
    private static Transformer<INode, Point2D> layout = null;
    private static final Set<GraphChangeListener> listeners = new HashSet<>();

    public static void setGraphAndLayout(final IGraph graph, final Transformer<INode, Point2D> layout) {
        GraphManager.graph = graph;
        GraphManager.layout = layout;
        notifyListeners();
    }

    public static void setLayout(Transformer<INode, Point2D> layout) {
        GraphManager.layout = layout;
        notifyListeners();
    }

    public static void setGraph(IGraph graph) {
        GraphManager.graph = graph;
        notifyListeners();
    }

    public static void addGraphChangeListener(GraphChangeListener l) {
        listeners.add(l);
    }

    public static void removeGraphChangeListener(GraphChangeListener l) {
        listeners.remove(l);
    }

    private static void notifyListeners() {
        for (final GraphChangeListener l : listeners) {
            l.graphReplaced(graph, layout);
        }
    }

    public static void graphChanged() {
        for (final GraphChangeListener l : listeners) {
            l.graphChanged();
        }
    }

    public static IGraph getGraph() {
        return graph;
    }

    public static Transformer<INode, Point2D> getLayout() {
        return layout;
    }

}
