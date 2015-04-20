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

import java.awt.geom.Point2D;

import org.apache.commons.collections15.Transformer;
import org.uweschmidt.dijkstravis.graph.GraphChangeListener;
import org.uweschmidt.dijkstravis.graph.GraphManager;
import org.uweschmidt.dijkstravis.graph.IEdge;
import org.uweschmidt.dijkstravis.graph.IGraph;
import org.uweschmidt.dijkstravis.graph.INode;
import org.uweschmidt.dijkstravis.graph.algorithm.DijkstraAlgorithmManager;


public class MyEdgeLabelTransformer implements Transformer<IEdge, String>, GraphChangeListener {
	
	private IGraph graph;
	private Transformer<INode, Point2D> layout;
	
	public MyEdgeLabelTransformer() {
		GraphManager.addGraphChangeListener(this);
	}
	
	public void graphReplaced(IGraph graph, Transformer<INode, Point2D> layout) {
		this.graph = graph;
		this.layout = layout;
	}
	
	public void graphChanged() {
	}
	
	// edge distance is set here
	public String transform(IEdge edge) {
		
		if (!DijkstraAlgorithmManager.isAlgoRunning()) {			
			Point2D source = layout.transform(graph.getSource(edge));
			Point2D dest = layout.transform(graph.getDest(edge));
			edge.setLength(source.distance(dest));
		}
		
		return null;
	}

}
