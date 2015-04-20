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

import java.awt.Shape;

import org.uweschmidt.dijkstravis.graph.IEdge;
import org.uweschmidt.dijkstravis.graph.INode;

import edu.uci.ics.jung.visualization.RenderContext;

public abstract class Animation<T> {

	protected float progress = -1f;
	
	public void paint(RenderContext<INode, IEdge> rc, T e, Shape shape) {
		if (!isStarted())
			paintBeforeStarted(rc, e, shape);
		else if (inProgress())
			paintInProgress(rc, e, shape);
		else if (isFinished())
			paintAfterFinished(rc, e, shape);
		else
			throw new RuntimeException("Unexpected");
	}
	
	protected abstract void paintBeforeStarted(RenderContext<INode, IEdge> rc, T e, Shape shape);
	protected abstract void paintInProgress(RenderContext<INode, IEdge> rc, T e, Shape shape);
	protected abstract void paintAfterFinished(RenderContext<INode, IEdge> rc, T e, Shape shape);

	public void setProgress(float progress) {
		this.progress = progress;
	}

	public float getProgress() {
		return progress;
	}

	public boolean isFinished() {
		return progress >= 1f;
	}

	public boolean isStarted() {
		return progress > 0f;
	}

	public boolean inProgress() {
		return isStarted() && !isFinished();
	}

}
