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
package org.uweschmidt.dijkstravis.gui.jung.animation.animations;

import java.awt.Paint;
import java.awt.Shape;

import org.uweschmidt.dijkstravis.graph.IEdge;
import org.uweschmidt.dijkstravis.graph.INode;

import edu.uci.ics.jung.visualization.RenderContext;

public class NodeFlashAnimation extends NodeFillAnimation {
	
	private Paint flashPaint1, flashPaint2;
	
	public NodeFlashAnimation(Paint flashPaint1, Paint flashPaint2) {
		this.flashPaint1 = flashPaint1;
		this.flashPaint2 = flashPaint2;
	}

	@Override
	protected void paintAfterFinished(RenderContext<INode, IEdge> rc, INode v, Shape shape) {
		Paint fillPaint = rc.getVertexFillPaintTransformer().transform(v);
		paintNode(rc, v, shape, fillPaint);
	}

	@Override
	protected void paintBeforeStarted(RenderContext<INode, IEdge> rc, INode v, Shape shape) {
		Paint fillPaint = rc.getVertexFillPaintTransformer().transform(v);
		paintNode(rc, v, shape, fillPaint);
	}

	@Override
	protected void paintInProgress(RenderContext<INode, IEdge> rc, INode v, Shape shape) {
		Paint fillPaint = progress <= .5 ? flashPaint1 : flashPaint2;
		paintNode(rc, v, shape, fillPaint);
	}

}
