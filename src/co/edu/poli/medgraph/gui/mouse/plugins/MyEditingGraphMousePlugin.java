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
package co.edu.poli.medgraph.gui.mouse.plugins;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import org.apache.commons.collections15.Factory;
import org.uweschmidt.dijkstravis.graph.IEdge;
import org.uweschmidt.dijkstravis.graph.INode;
import co.edu.poli.medgraph.gui.jung.mouse.MyGraphMouse;

import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.EditingGraphMousePlugin;

public class MyEditingGraphMousePlugin extends EditingGraphMousePlugin<INode, IEdge> {
	
	private MyGraphMouse gm;
	
	public MyEditingGraphMousePlugin(MyGraphMouse gm, Layout<INode, IEdge> layout, Factory<INode> vertexFactory, Factory<IEdge> edgeFactory) {
		super(MouseEvent.BUTTON1_MASK, layout, vertexFactory, edgeFactory);
		this.gm = gm;
		this.cursor = Cursor.getDefaultCursor();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2 && !MyGraphMouse.isOverNode(e)) {
			super.mousePressed(e);
			gm.mouseMoved(e);
			e.consume();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void mouseReleased(MouseEvent e) {
		super.mouseReleased(e);
		// fixes bug when an arrow is pulled and left unconnected
		// (artefact remains), therefore repaint() necessary, triggers repaint
		gm.mouseMoved(e);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void mousePressed(MouseEvent e) {
		final VisualizationViewer<INode, IEdge> vv = (VisualizationViewer<INode, IEdge>) e.getSource();
		final Point2D p = e.getPoint();
		GraphElementAccessor<INode, IEdge> pickSupport = vv.getPickSupport();
		final INode vertex = pickSupport.getVertex(vv.getModel().getGraphLayout(), p.getX(), p.getY());
		if (vertex != null && e.getClickCount() != 2) {
			super.mousePressed(e);
		}
	}
}