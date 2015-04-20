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
package org.uweschmidt.dijkstravis.gui.jung.mouse.plugins;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;

import org.apache.commons.collections15.Transformer;
import org.uweschmidt.dijkstravis.graph.GraphChangeListener;
import org.uweschmidt.dijkstravis.graph.GraphManager;
import org.uweschmidt.dijkstravis.graph.IEdge;
import org.uweschmidt.dijkstravis.graph.IGraph;
import org.uweschmidt.dijkstravis.graph.INode;
import org.uweschmidt.dijkstravis.graph.algorithm.DijkstraAlgorithmManager;
import org.uweschmidt.dijkstravis.gui.jung.animation.animations.Animation;
import org.uweschmidt.dijkstravis.gui.jung.animation.renderer.MyAnimationNodeRenderer;

import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractGraphMousePlugin;

public class MyHighlighterGraphMousePlugin extends AbstractGraphMousePlugin implements MouseMotionListener, GraphChangeListener {
	
	private IGraph graph;
	private INode node = null;
	private IEdge edge = null;

	public MyHighlighterGraphMousePlugin() {
		super(0);
		GraphManager.addGraphChangeListener(this);
	}
	
	public void graphChanged() {
	}
	
	public void graphReplaced(IGraph graph, Transformer<INode, Point2D> layout) {
		this.graph = graph;
	}

	public void mouseDragged(MouseEvent e) {
	}

	@SuppressWarnings("unchecked")
	public void mouseMoved(MouseEvent e) {

		VisualizationViewer<INode, IEdge> vv = (VisualizationViewer<INode, IEdge>) e.getSource();		
		Point2D p = e.getPoint();
		GraphElementAccessor<INode, IEdge> pickSupport = vv.getPickSupport();
		
		INode node = pickSupport.getVertex(vv.getModel().getGraphLayout(), p.getX(), p.getY());
		Animation<INode> a = ((MyAnimationNodeRenderer)vv.getRenderer().getVertexRenderer()).getAnimation(node);
		if (node != null && (a == null || a.isFinished())) {
			this.node = node;
			this.edge = null;
			graph.highlight(node);
			vv.repaint();
			return;
		}
		
		if (!DijkstraAlgorithmManager.isAlgoRunning()) {
			IEdge edge = pickSupport.getEdge(vv.getModel().getGraphLayout(), p.getX(), p.getY());
			if (edge != null) {
				this.edge = edge;
				this.node = null;
				graph.highlight(edge);
				vv.repaint();
				return;
			}
		}
		
		if (this.node != null) {
			graph.highlight((INode)null);
			this.node = null;
			vv.repaint();
		} else if (this.edge != null) {
			graph.highlight((IEdge)null);
			this.edge = null;
			vv.repaint();
		}
	}
}
