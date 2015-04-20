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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Map;
import java.util.WeakHashMap;

import javax.swing.JComponent;

import org.uweschmidt.dijkstravis.graph.IEdge;
import org.uweschmidt.dijkstravis.graph.INode;
import org.uweschmidt.dijkstravis.graph.algorithm.DijkstraAlgorithmManager;
import org.uweschmidt.dijkstravis.gui.jung.animation.animations.Animation;
import org.uweschmidt.dijkstravis.gui.jung.animation.animations.EdgeLineAnimation;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.util.Context;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.renderers.BasicEdgeRenderer;
import edu.uci.ics.jung.visualization.transform.LensTransformer;
import edu.uci.ics.jung.visualization.transform.MutableTransformer;
import edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator;

/**
 * Based on source code of {@link BasicEdgeRenderer}.
 */
public class MyAnimationEdgeRenderer extends BasicEdgeRenderer<INode, IEdge> {

	private static final BasicStroke MARKER_STROKE = new BasicStroke(15f);
	private static final Color MARKER_COLOR = new Color(255,0,0,150);
	
	private Map<IEdge, Animation<IEdge>> animations = new WeakHashMap<IEdge, Animation<IEdge>>();

	public Animation<IEdge> getAnimation(IEdge e) {
		return animations.get(e);
	}

	public void setAnimation(IEdge e, Animation<IEdge> a) {
		animations.put(e, a);
	}

	public void removeAnimations(Collection<IEdge> edges) {
		for (IEdge e : edges)
			animations.remove(e);
	}

	public void clearAnimations() {
		animations.clear();
	}

	/**
	 * Draws the edge <code>e</code>, whose endpoints are at
	 * <code>(x1,y1)</code> and <code>(x2,y2)</code>, on the graphics
	 * context <code>g</code>. The <code>Shape</code> provided by the
	 * <code>EdgeShapeFunction</code> instance is scaled in the x-direction so
	 * that its width is equal to the distance between <code>(x1,y1)</code>
	 * and <code>(x2,y2)</code>.
	 */
	// /*
	@Override
	protected void drawSimpleEdge(final RenderContext<INode, IEdge> rc, Layout<INode, IEdge> layout, final IEdge e) {

		final GraphicsDecorator g = rc.getGraphicsContext();
		Graph<INode, IEdge> graph = layout.getGraph();
		Pair<INode> endpoints = graph.getEndpoints(e);
		INode v1 = endpoints.getFirst();
		INode v2 = endpoints.getSecond();
		Point2D p1 = layout.transform(v1);
		Point2D p2 = layout.transform(v2);
		p1 = rc.getMultiLayerTransformer().transform(Layer.LAYOUT, p1);
		p2 = rc.getMultiLayerTransformer().transform(Layer.LAYOUT, p2);
		float x1 = (float) p1.getX();
		float y1 = (float) p1.getY();
		float x2 = (float) p2.getX();
		float y2 = (float) p2.getY();

		boolean isLoop = v1.equals(v2);
		Shape s2 = rc.getVertexShapeTransformer().transform(v2);
		Shape edgeShape = rc.getEdgeShapeTransformer().transform(Context.<Graph<INode, IEdge>, IEdge> getInstance(graph, e));

		boolean edgeHit = true;
		boolean arrowHit = true;
		Rectangle deviceRectangle = null;
		JComponent vv = rc.getScreenDevice();
		if (vv != null) {
			Dimension d = vv.getSize();
			deviceRectangle = new Rectangle(0, 0, d.width, d.height);
		}

		AffineTransform xform = AffineTransform.getTranslateInstance(x1, y1);

		if (isLoop) {
			// this is a self-loop. scale it is larger than the vertex
			// it decorates and translate it so that its nadir is
			// at the center of the vertex.
			Rectangle2D s2Bounds = s2.getBounds2D();
			xform.scale(s2Bounds.getWidth(), s2Bounds.getHeight());
			xform.translate(0, -edgeShape.getBounds2D().getWidth() / 2);
		} else if (rc.getEdgeShapeTransformer() instanceof EdgeShape.Orthogonal) {
			float dx = x2 - x1;
			float dy = y2 - y1;
			GeneralPath gp = new GeneralPath();
			gp.moveTo(0, 0);// the xform will do the translation to x1,y1
			if (x1 > x2) {
				if (y1 > y2) {
					gp.lineTo(dx, 0);
					gp.lineTo(dx, dy);
				} else {
					gp.lineTo(dx, 0);
					gp.lineTo(dx, dy);
				}
			} else {
				if (y1 > y2) {
					gp.lineTo(dx, 0);
					gp.lineTo(dx, dy);

				} else {
					gp.lineTo(dx, 0);
					gp.lineTo(dx, dy);
				}

			}

			edgeShape = gp;

		} else {
			// this is a normal edge. Rotate it to the angle between
			// vertex endpoints, then scale it to the distance between
			// the vertices
			float dx = x2 - x1;
			float dy = y2 - y1;
			float thetaRadians = (float) Math.atan2(dy, dx);
			xform.rotate(thetaRadians);
			float dist = (float) Math.sqrt(dx * dx + dy * dy);
			xform.scale(dist, 1.0);
		}

		edgeShape = xform.createTransformedShape(edgeShape);

		MutableTransformer vt = rc.getMultiLayerTransformer().getTransformer(Layer.VIEW);
		if (vt instanceof LensTransformer) {
			vt = ((LensTransformer) vt).getDelegate();
		}
		edgeHit = vt.transform(edgeShape).intersects(deviceRectangle);

		if (edgeHit == true) {

			Paint oldPaint = g.getPaint();

			Paint fill_paint = rc.getEdgeFillPaintTransformer().transform(e);
			Paint draw_paint = rc.getEdgeDrawPaintTransformer().transform(e);
			
			if (e.isHighlighted() && DijkstraAlgorithmManager.isAlgoRunning() && (getAnimation(e) == null || getAnimation(e).isFinished())) {
				Stroke old = g.getStroke();
				g.setPaint(MARKER_COLOR);
				g.setStroke(MARKER_STROKE);
				g.draw(edgeShape);
				g.setStroke(old);
			}
			
			Animation<IEdge> a = getAnimation(e);
			boolean animate = a != null;

			if (animate) {
				a.paint(rc, e, edgeShape);
			} else {
				// check for reverse edge that was already painted
				IEdge edge = graph.findEdge(v2, v1);
				boolean dontPaint =
					!DijkstraAlgorithmManager.isAlgoRunning() && !e.isHighlighted()
					&& edge != null && (edge.getId() < e.getId()
					&& getAnimation(edge) == null);
				
				// get Paints for filling and drawing
				// (filling is done first so that drawing and label use same Paint)
				if (!dontPaint) {
					if (fill_paint != null) {
						g.setPaint(fill_paint);
						g.fill(edgeShape);
					}
					if (draw_paint != null) {
						g.setPaint(draw_paint);
						g.draw(edgeShape);
					}
				}
			}


			float scalex = (float) g.getTransform().getScaleX();
			float scaley = (float) g.getTransform().getScaleY();
			// see if arrows are too small to bother drawing
			if (scalex < .3 || scaley < .3)
				return;


			if (rc.getEdgeArrowPredicate().evaluate(Context.<Graph<INode, IEdge>, IEdge> getInstance(graph, e))) {
				
				Shape destVertexShape = rc.getVertexShapeTransformer().transform(graph.getEndpoints(e).getSecond());

				AffineTransform xf = AffineTransform.getTranslateInstance(x2, y2);
				destVertexShape = xf.createTransformedShape(destVertexShape);
				
				Paint arrowFillPaint = rc.getArrowFillPaintTransformer().transform(e);
				Paint arrowDrawPaint = rc.getArrowDrawPaintTransformer().transform(e);
				if (animate && a instanceof EdgeLineAnimation) {
					arrowFillPaint = ((EdgeLineAnimation)a).getArrowPaint(arrowFillPaint);
					arrowDrawPaint = ((EdgeLineAnimation)a).getArrowPaint(arrowDrawPaint);
				}

				arrowHit = rc.getMultiLayerTransformer().getTransformer(Layer.VIEW).transform(destVertexShape).intersects(deviceRectangle);
				if (arrowHit) {

					AffineTransform at = getArrowTransform(rc, new GeneralPath(edgeShape), destVertexShape);
					if (at == null)
						return;
					Shape arrow = rc.getEdgeArrowTransformer().transform(Context.<Graph<INode, IEdge>, IEdge> getInstance(graph, e));
					arrow = at.createTransformedShape(arrow);
					g.setPaint(arrowFillPaint);
					g.fill(arrow);
					g.setPaint(arrowDrawPaint);
					g.draw(arrow);
				}
				if (graph.getEdgeType(e) == EdgeType.UNDIRECTED) {
					Shape vertexShape = rc.getVertexShapeTransformer().transform(graph.getEndpoints(e).getFirst());
					xf = AffineTransform.getTranslateInstance(x1, y1);
					vertexShape = xf.createTransformedShape(vertexShape);

					arrowHit = rc.getMultiLayerTransformer().getTransformer(Layer.VIEW).transform(vertexShape).intersects(deviceRectangle);

					if (arrowHit) {
						AffineTransform at = getReverseArrowTransform(rc, new GeneralPath(edgeShape), vertexShape, !isLoop);
						if (at == null)
							return;
						Shape arrow = rc.getEdgeArrowTransformer().transform(Context.<Graph<INode, IEdge>, IEdge> getInstance(graph, e));
						arrow = at.createTransformedShape(arrow);
						g.setPaint(arrowFillPaint);
						g.fill(arrow);
						g.setPaint(arrowDrawPaint);
						g.draw(arrow);
					}
				}
			}

			// restore old paint
			g.setPaint(oldPaint);
		}
	}

}
