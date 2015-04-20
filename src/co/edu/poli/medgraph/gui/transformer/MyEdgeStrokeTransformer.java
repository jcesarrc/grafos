/*
 * Copyright (C) 2008, Uwe Schmidt 
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a 
 * copy of this software and associated documentation files (the "Software"), 
 * to deal in the Software without restriction, including without limitation 
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the 
 * Software is furnished to do so, subject to the following conditions: 
 * 
 * The above copyright notice and this permission notice shall be included in 
 * all copies or substantial portions of the Software. 
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL 
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER 
 * DEALINGS IN THE SOFTWARE. 
 */
package co.edu.poli.medgraph.gui.transformer;

import java.awt.BasicStroke;
import java.awt.Stroke;
import java.awt.geom.Point2D;

import org.apache.commons.collections15.Transformer;
import org.uweschmidt.dijkstravis.graph.GraphChangeListener;
import org.uweschmidt.dijkstravis.graph.GraphManager;
import org.uweschmidt.dijkstravis.graph.IEdge;
import org.uweschmidt.dijkstravis.graph.IGraph;
import org.uweschmidt.dijkstravis.graph.INode;
import org.uweschmidt.dijkstravis.graph.algorithm.DijkstraAlgorithmManager;


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
