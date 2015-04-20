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

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

import org.apache.commons.collections15.Transformer;
import org.uweschmidt.dijkstravis.graph.GraphChangeListener;
import org.uweschmidt.dijkstravis.graph.GraphManager;
import org.uweschmidt.dijkstravis.graph.IEdge;
import org.uweschmidt.dijkstravis.graph.IGraph;
import org.uweschmidt.dijkstravis.graph.INode;
import org.uweschmidt.dijkstravis.graph.algorithm.DijkstraAlgorithmManager;

import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.RenderContext;

public class MyNodeFillPaintTransformer implements Transformer<INode, Paint>, GraphChangeListener {

	public static final Color NOT_VISITED = Color.GRAY;
	public static final Color START_NODE = Color.CYAN;
	public static final Color VISITED = Color.YELLOW;
	public static final Color SETTLED = Color.GREEN;

	public static final Color CURRENTLY_SETTLED = Color.GREEN;
	public static final Color PATH_FOUND = VISITED;
	public static final Color PATH_IMPROVED = VISITED;

	public static final Color EDITOR_COLOR = Color.GREEN;

	private Transformer<INode, Point2D> layout;
	private RenderContext<INode, IEdge> rc;

	public MyNodeFillPaintTransformer(final RenderContext<INode, IEdge> rc) {
		GraphManager.addGraphChangeListener(this);
		this.rc = rc;
	}

	public void graphChanged() {
	}

	public void graphReplaced(IGraph graph, Transformer<INode, Point2D> layout) {
		this.layout = layout;
	}

	public Paint transform(INode node) {
		switch (node.getAttribute()) {
			case START_NODE:
				return START_NODE;
			case SETTLED:
				return SETTLED;
			case NOT_VISITED:
				return DijkstraAlgorithmManager.isAlgoRunning() ? NOT_VISITED : EDITOR_COLOR;
			case VISITED:
				return VISITED;
			case PATH_FOUND:
				return PATH_FOUND;
			case PATH_IMPROVED:
				return PATH_IMPROVED;
			case CURRENTLY_SETTLED:
				return CURRENTLY_SETTLED;
			case PATH_FOUND_NEXT_SETTLED:
			case PATH_IMPROVED_NEXT_SETTLED:
			case VISITED_NEXT_SETTLED:
				return gradientPaint(rc.getMultiLayerTransformer().transform(Layer.LAYOUT, layout.transform(node)), rc.getVertexShapeTransformer().transform(node).getBounds(), VISITED, SETTLED);
		}
		return null;
	}

	public static GradientPaint gradientPaint(Point2D p, Rectangle r, Color one, Color two) {
		r.translate((int) p.getX(), (int) p.getY() + (int) r.getHeight() / 4);
		return new GradientPaint((float) r.getMinX(), (float) r.getMinY(), one, (float) r.getMinX(), ((float) r.getMaxY()), two, false);
	}

}
