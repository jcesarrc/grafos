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

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;

import org.uweschmidt.dijkstravis.graph.IEdge;
import org.uweschmidt.dijkstravis.graph.IGraph;
import org.uweschmidt.dijkstravis.graph.INode;

import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractGraphMousePlugin;

public class MyStartnodeGraphMousePlugin extends AbstractGraphMousePlugin implements MouseListener {
	public MyStartnodeGraphMousePlugin() {
		super(InputEvent.BUTTON1_MASK);
	}

	@SuppressWarnings("unchecked")
	public void mouseClicked(MouseEvent e) {
		if (checkModifiers(e) && e.getClickCount() == 2) {
			VisualizationViewer<INode, IEdge> vv = (VisualizationViewer<INode, IEdge>) e.getSource();
			Point2D p = e.getPoint();
			GraphElementAccessor<INode, IEdge> pickSupport = vv.getPickSupport();
			INode vertex = pickSupport.getVertex(vv.getModel().getGraphLayout(), p.getX(), p.getY());
			if (vertex != null) {
				IGraph graph = (IGraph) vv.getGraphLayout().getGraph();
				setStartNode(graph, graph.getStart(), vertex);
				e.consume();
			}
		}
	}
	
	public static void setStartNode(IGraph graph, INode oldStart, INode newStart) {
		if (oldStart != null)
			oldStart.setAttribute(INode.Attribute.NOT_VISITED);
		// enable/disable start
		if (graph.getStart() == newStart) {
			newStart.setAttribute(INode.Attribute.NOT_VISITED);
			graph.setStart(null);
		} else {
			newStart.setAttribute(INode.Attribute.START_NODE);
			graph.setStart(newStart);
		}
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}
}