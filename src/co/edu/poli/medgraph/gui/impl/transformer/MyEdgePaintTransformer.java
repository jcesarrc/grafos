package co.edu.poli.medgraph.gui.impl.transformer;

import co.edu.poli.medgraph.algoritmo.DijkstraAlgorithmManager;
import co.edu.poli.medgraph.grafo.GraphChangeListener;
import co.edu.poli.medgraph.grafo.GraphManager;
import co.edu.poli.medgraph.grafo.IEdge;
import co.edu.poli.medgraph.grafo.IGraph;
import co.edu.poli.medgraph.grafo.INode;
import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.Point2D;
import org.apache.commons.collections15.Transformer;


public class MyEdgePaintTransformer implements Transformer<IEdge, Paint>, GraphChangeListener {

	public static final Color HIGHLIGHT = Color.BLUE;
	public static final Color DEFAULT = Color.BLACK;
	public static final Color SHORTEST_PATH = DEFAULT;
	public static final Color SHORTEST_PATH_SETTLED = DEFAULT;
	
	public static final Color SHORTEST_PATH_REMOVED = Color.RED;
	public static final Color SHORTEST_PATH_ADDED = Color.BLUE;
	public static final Color SHORTEST_PATH_IMPROVED = SHORTEST_PATH_ADDED;
	
	public static final Color UNIMPORTANT_COLOR = Color.LIGHT_GRAY;
	
	public IGraph graph;

	public MyEdgePaintTransformer() {
		GraphManager.addGraphChangeListener(this);
	}

	public void graphReplaced(final IGraph graph, final Transformer<INode, Point2D> layout) {
		this.graph = graph;
	}

	public void graphChanged() {
	}

	public Paint transform(final IEdge edge) {
		if (edge.isHighlighted() && !DijkstraAlgorithmManager.isAlgoRunning())
			return HIGHLIGHT;
		
		if (DijkstraAlgorithmManager.isAlgoRunning()) {
			final INode dest = graph.getDest(edge);
			switch (edge.getAttribute()) {
				case ADDED_TO_SHORTEST_PATH:
					switch (dest.getAttribute()) {
						case PATH_IMPROVED_NEXT_SETTLED:
						case PATH_IMPROVED:
							return SHORTEST_PATH_IMPROVED;
						default:
							return SHORTEST_PATH_ADDED;
					}
				case ON_SHORTEST_PATH:
					switch (dest.getAttribute()) {
						case CURRENTLY_SETTLED:
						case SETTLED:
							return SHORTEST_PATH_SETTLED;
						default:
							return SHORTEST_PATH;
					}
				case REMOVED_FROM_SHORTEST_PATH:
					return SHORTEST_PATH_REMOVED;
				case NOT_VISITED:
				case VISITED:
				default:
					return UNIMPORTANT_COLOR;
			}
		} else {
			return DEFAULT;
		}
	}

}
