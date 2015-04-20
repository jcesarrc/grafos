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
package org.uweschmidt.dijkstravis.gui.jung.animation.renderer;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.util.Collection;
import java.util.Map;
import java.util.WeakHashMap;

import org.uweschmidt.dijkstravis.graph.IEdge;
import org.uweschmidt.dijkstravis.graph.INode;
import org.uweschmidt.dijkstravis.graph.algorithm.DijkstraAlgorithmManager;
import org.uweschmidt.dijkstravis.gui.jung.animation.animations.Animation;

import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.picking.PickedState;
import edu.uci.ics.jung.visualization.renderers.BasicVertexRenderer;
import edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator;

/**
 * Based on source code of {@link BasicVertexRenderer}.
 */
public class MyAnimationNodeRenderer extends BasicVertexRenderer<INode, IEdge> {

	private Map<INode, Animation<INode>> animations = new WeakHashMap<INode, Animation<INode>>();
	
	private Color colorOne = Color.white;
	private Color pickedColorOne = Color.darkGray;

	public Animation<INode> getAnimation(INode e) {
		return animations.get(e);
	}

	public void setAnimation(INode e, Animation<INode> a) {
		animations.put(e, a);
	}

	public void removeAnimations(Collection<INode> nodes) {
		for (INode e : nodes)
			animations.remove(e);
	}

	public void clearAnimations() {
		animations.clear();
	}

	@Override
	protected void paintShapeForVertex(RenderContext<INode, IEdge> rc, INode v, Shape shape) {

		Animation<INode> a = getAnimation(v);
		boolean animate = a != null;

		GraphicsDecorator g = rc.getGraphicsContext();
		Paint oldPaint = g.getPaint();
		Paint fillPaint = rc.getVertexFillPaintTransformer().transform(v);

		if (animate) {
			a.paint(rc, v, shape);
		} else if (DijkstraAlgorithmManager.isAlgoRunning()){
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
		} else {
			Color colorTwo = (Color)fillPaint;
			PickedState<INode> pickedState = rc.getPickedVertexState();
	        Rectangle r = shape.getBounds();
	        if(pickedState != null && pickedState.isPicked(v)) {
	        	fillPaint = new GradientPaint((float)r.getMinX(), (float)r.getMinY(), pickedColorOne,
	            		(float)r.getMinX(), ((float)r.getMaxY()), colorTwo, false);
	        } else {
	        	fillPaint = new GradientPaint((float)r.getMinX(), (float)r.getMinY(), colorOne,
	        		(float)r.getMinX(), ((float)r.getMaxY()), colorTwo, false);
	        }
	        if(fillPaint != null) {
	            g.setPaint(fillPaint);
	            g.fill(shape);
	            g.setPaint(oldPaint);
	        }
	        Paint drawPaint = rc.getVertexDrawPaintTransformer().transform(v);
	        if(drawPaint != null) {
	            g.setPaint(drawPaint);
	        }
	        Stroke oldStroke = g.getStroke();
	        Stroke stroke = rc.getVertexStrokeTransformer().transform(v);
	        if(stroke != null) {
	            g.setStroke(stroke);
	        }
	        g.draw(shape);
	        g.setPaint(oldPaint);
	        g.setStroke(oldStroke);
		}
		
	}

}
