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
package org.uweschmidt.dijkstravis.gui.jung.transformer;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import org.apache.commons.collections15.Transformer;
import org.uweschmidt.dijkstravis.graph.GraphChangeListener;
import org.uweschmidt.dijkstravis.graph.GraphManager;
import org.uweschmidt.dijkstravis.graph.IEdge;
import org.uweschmidt.dijkstravis.graph.IGraph;
import org.uweschmidt.dijkstravis.graph.INode;

import edu.uci.ics.jung.algorithms.util.Context;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.ArrowFactory;

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
