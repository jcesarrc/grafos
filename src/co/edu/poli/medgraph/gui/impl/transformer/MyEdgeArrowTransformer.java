package co.edu.poli.medgraph.gui.impl.transformer;

import co.edu.poli.medgraph.grafo.GraphChangeListener;
import co.edu.poli.medgraph.grafo.GraphManager;
import co.edu.poli.medgraph.grafo.IEdge;
import co.edu.poli.medgraph.grafo.IGraph;
import co.edu.poli.medgraph.grafo.INode;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Context;
import edu.uci.ics.jung.visualization.util.ArrowFactory;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import org.apache.commons.collections15.Transformer;

public class MyEdgeArrowTransformer implements Transformer<Context<Graph<INode,IEdge>,IEdge>, Shape>, GraphChangeListener {
	
	public static final Shape NO_ARROW = new Line2D.Float();
	public static final GeneralPath DEFAULT_ARROW = ArrowFactory.getWedgeArrow(7, 7);
	
	private IGraph graph;
	
	public MyEdgeArrowTransformer() {
		GraphManager.addGraphChangeListener(this);
	}
	
        @Override
	public void graphReplaced(IGraph graph, Transformer<INode, Point2D> layout) {
		this.graph = graph;
	}
	
        @Override
	public void graphChanged() {
	}
	
        @Override
	public Shape transform(Context<Graph<INode, IEdge>, IEdge> context) {
		final IEdge edge = context.element;
		
		final IEdge reverse = graph.findEdge(graph.getDest(edge), graph.getSource(edge));
		if (reverse != null) {
			switch (reverse.getAttribute()) {
				case ADDED_TO_SHORTEST_PATH:
				case REMOVED_FROM_SHORTEST_PATH:
				case ON_SHORTEST_PATH:
					return NO_ARROW;
				default:
					return DEFAULT_ARROW;
			}
		}
		
		return DEFAULT_ARROW;
	}
}
