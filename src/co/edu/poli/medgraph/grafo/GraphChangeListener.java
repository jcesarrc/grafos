package co.edu.poli.medgraph.grafo;

import java.awt.geom.Point2D;
import org.apache.commons.collections15.Transformer;

public interface GraphChangeListener {

	// Vertices anadidos o removidos
	public void graphChanged();
	// Cambios en el grafo completo
	public void graphReplaced(IGraph graph, Transformer<INode, Point2D> layout);

}
