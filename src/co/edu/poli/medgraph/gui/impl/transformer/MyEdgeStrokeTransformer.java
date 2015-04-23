
package co.edu.poli.medgraph.gui.impl.transformer;

import co.edu.poli.medgraph.algoritmo.DijkstraAlgorithmManager;
import co.edu.poli.medgraph.grafo.GraphChangeListener;
import co.edu.poli.medgraph.grafo.GraphManager;
import co.edu.poli.medgraph.grafo.IEdge;
import co.edu.poli.medgraph.grafo.IGraph;
import co.edu.poli.medgraph.grafo.INode;
import java.awt.BasicStroke;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import org.apache.commons.collections15.Transformer;


public class MyEdgeStrokeTransformer implements Transformer<IEdge, Stroke>, GraphChangeListener {

	public static final BasicStroke NORMAL_STROKE = new BasicStroke(1.0f);
	public static final BasicStroke THICK_STROKE = new BasicStroke(3.0f);
	private IGraph graph;

	public MyEdgeStrokeTransformer() {
		GraphManager.addGraphChangeListener(this);
	}

	public void graphReplaced(final IGraph graph, final Transformer<INode, Point2D> layout) {
		this.graph = graph;
	}

	public void graphChanged() {
	}

	public Stroke transform(final IEdge edge) {
		
		if (edge.isHighlighted() && !DijkstraAlgorithmManager.isAlgoRunning()) return THICK_STROKE;
		
		final INode dest = graph.getDest(edge);
		switch (edge.getAttribute()) {
			case ADDED_TO_SHORTEST_PATH:
				return NORMAL_STROKE;
			case ON_SHORTEST_PATH:
				switch (dest.getAttribute()) {
					case CURRENTLY_SETTLED:
					case SETTLED:
						return THICK_STROKE;
					default:
						return NORMAL_STROKE;
				}
			case REMOVED_FROM_SHORTEST_PATH:
			case NOT_VISITED:
			case VISITED:
			default:
				return NORMAL_STROKE;
		}
	}

}
