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

import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.Point2D;

import org.apache.commons.collections15.Transformer;
import org.uweschmidt.dijkstravis.graph.GraphChangeListener;
import org.uweschmidt.dijkstravis.graph.GraphManager;
import org.uweschmidt.dijkstravis.graph.IEdge;
import org.uweschmidt.dijkstravis.graph.IGraph;
import org.uweschmidt.dijkstravis.graph.INode;
import org.uweschmidt.dijkstravis.graph.algorithm.DijkstraAlgorithmManager;


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
