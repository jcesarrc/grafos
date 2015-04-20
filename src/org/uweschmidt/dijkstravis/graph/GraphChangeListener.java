package org.uweschmidt.dijkstravis.graph;

import java.awt.geom.Point2D;

import org.apache.commons.collections15.Transformer;


public interface GraphChangeListener {

	// nodes or edges added/removed
	public void graphChanged();
	// entire graph replaced (object changed)
	public void graphReplaced(IGraph graph, Transformer<INode, Point2D> layout);

}
