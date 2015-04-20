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
package co.edu.poli.medgraph.gui.animation.animations;

import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;

import org.uweschmidt.dijkstravis.graph.IEdge;
import org.uweschmidt.dijkstravis.graph.INode;

import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator;

public abstract class NodeFillAnimation extends Animation<INode> {
	
	protected void paintNode(RenderContext<INode, IEdge> rc, INode v, Shape shape, Paint fillPaint) {
		GraphicsDecorator g = rc.getGraphicsContext();
		Paint oldPaint = g.getPaint();

		if (fillPaint != null) {
			g.setPaint(fillPaint);
			g.fill(shape);
			g.setPaint(oldPaint);
		}
		Paint drawPaint = rc.getVertexDrawPaintTransformer().transform(v);
		if (drawPaint != null) {
			g.setPaint(drawPaint);
			Stroke oldStroke = g.getStroke();
			Stroke stroke = rc.getVertexStrokeTransformer().transform(v);
			if (stroke != null) {
				g.setStroke(stroke);
			}
			g.draw(shape);
			g.setPaint(oldPaint);
			g.setStroke(oldStroke);
		}
	}

}
