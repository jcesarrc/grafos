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
package co.edu.poli.medgraph.gui.animation.renderer;

import java.awt.Paint;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.List;

import org.uweschmidt.dijkstravis.graph.IEdge;
import org.uweschmidt.dijkstravis.graph.INode;
import co.edu.poli.medgraph.gui.animation.animations.Animation;
import co.edu.poli.medgraph.gui.animation.animations.EdgeLineAnimation;
import co.edu.poli.medgraph.gui.transformer.MyEdgePaintTransformer;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.renderers.BasicRenderer;

/**
 * Sorts edges into three sets before they are painted. This is done to prevent
 * currently "unimportant" edges to be painted over "important" ones.<br>
 * Edges not on a path are painted first. Secondly, edges on a path without change
 * at the current step are painted. Edges which are currently animated or changed 
 * are painted last. 
 */
public class MyAnimationRenderer extends BasicRenderer<INode, IEdge> {

	private final List<IEdge> firstEdges, secondEdges, thirdEdges;
	private MyAnimationEdgeRenderer edgeRenderer = null;

	public MyAnimationRenderer() {
		firstEdges = new LinkedList<IEdge>();
		secondEdges = new LinkedList<IEdge>();
		thirdEdges = new LinkedList<IEdge>();
	}

	@Override
	public void render(final RenderContext<INode, IEdge> renderContext, final Layout<INode, IEdge> layout) {
		firstEdges.clear();
		secondEdges.clear();
		thirdEdges.clear();

		if (getEdgeRenderer() instanceof MyAnimationEdgeRenderer)
			edgeRenderer = (MyAnimationEdgeRenderer) getEdgeRenderer();

		for (final IEdge e : layout.getGraph().getEdges()) {

			if (e.isHighlighted()) {
				thirdEdges.add(e);
				continue;
			}
				
			switch (e.getAttribute()) {
				case NOT_VISITED:
				case VISITED:
					firstEdges.add(e);
					break;
				case ON_SHORTEST_PATH:
					secondEdges.add(e);
					break;
				case ADDED_TO_SHORTEST_PATH:
				case REMOVED_FROM_SHORTEST_PATH:
					if (edgeRenderer != null) {
						Animation<IEdge> a = edgeRenderer.getAnimation(e);
						if (a != null) {
							if (a.inProgress()) {
								thirdEdges.add(e);
							} else if (a instanceof EdgeLineAnimation) {
								Paint p = ((EdgeLineAnimation) a).getArrowPaint(null);
								if (p == MyEdgePaintTransformer.UNIMPORTANT_COLOR)
									firstEdges.add(e);
								else
									secondEdges.add(e);
							} else
								secondEdges.add(e);
							break;
						}
					}
					thirdEdges.add(e);
					break;
				default:
					throw new RuntimeException("Didn't expect edge attribute " + e.getAttribute());
			}
		}

		// order in which edges are rendered
		renderEdgeList(renderContext, layout, firstEdges);
		renderEdgeList(renderContext, layout, secondEdges);
		renderEdgeList(renderContext, layout, thirdEdges);

		// paint all vertices
		try {
			for (INode v : layout.getGraph().getVertices()) {
				renderVertex(renderContext, layout, v);
				renderVertexLabel(renderContext, layout, v);
			}
		} catch (ConcurrentModificationException cme) {
			renderContext.getScreenDevice().repaint();
		}
	}

	private void renderEdgeList(final RenderContext<INode, IEdge> renderContext, final Layout<INode, IEdge> layout, final List<IEdge> edges) {
		// paint all edges
		try {
			for (IEdge e : edges) {
				renderEdge(renderContext, layout, e);
				renderEdgeLabel(renderContext, layout, e);
			}
		} catch (ConcurrentModificationException cme) {
			renderContext.getScreenDevice().repaint();
		}
	}

}
