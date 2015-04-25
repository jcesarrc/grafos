package co.edu.poli.medgraph.gui.impl.transformer;

import co.edu.poli.medgraph.algoritmo.DijkstraAlgorithmManager;
import co.edu.poli.medgraph.grafo.GraphChangeListener;
import co.edu.poli.medgraph.grafo.GraphManager;
import co.edu.poli.medgraph.grafo.IEdge;
import co.edu.poli.medgraph.grafo.IGraph;
import co.edu.poli.medgraph.grafo.INode;
import java.awt.geom.Point2D;
import org.apache.commons.collections15.Transformer;

public class MyEdgeLabelTransformer implements Transformer<IEdge, String>, GraphChangeListener {

    private IGraph graph;
    private Transformer<INode, Point2D> layout;

    public MyEdgeLabelTransformer() {
        GraphManager.addGraphChangeListener(this);
    }

    @Override
    public void graphReplaced(IGraph graph, Transformer<INode, Point2D> layout) {
        this.graph = graph;
        this.layout = layout;
    }

    @Override
    public void graphChanged() {
    }

    
    @Override
    public String transform(IEdge edge) {

        if (!DijkstraAlgorithmManager.isAlgoRunning()) {
            Point2D source = layout.transform(graph.getSource(edge));
            Point2D dest = layout.transform(graph.getDest(edge));
            edge.setLength(source.distance(dest));
        }

        return null;
    }

}
