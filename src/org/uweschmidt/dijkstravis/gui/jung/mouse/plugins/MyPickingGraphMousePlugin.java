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

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.PickingGraphMousePlugin;
import edu.uci.ics.jung.visualization.picking.PickedState;

/**
 * Based on source code of {@link PickingGraphMousePlugin}.
 */
public class MyPickingGraphMousePlugin<V, E> extends PickingGraphMousePlugin<V, E> {
	
	private int moveModifiers;
	
	public MyPickingGraphMousePlugin(int selectionModifiers, int addToSelectionModifiers, int moveModifiers) {
		super(selectionModifiers, addToSelectionModifiers);
		this.moveModifiers = moveModifiers;
	}
	
    @Override
	@SuppressWarnings("unchecked")
    public void mousePressed(MouseEvent e) {
        down = e.getPoint();
        VisualizationViewer<V,E> vv = (VisualizationViewer)e.getSource();
        GraphElementAccessor<V,E> pickSupport = vv.getPickSupport();
        PickedState<V> pickedVertexState = vv.getPickedVertexState();
        PickedState<E> pickedEdgeState = vv.getPickedEdgeState();
        if(pickSupport != null && pickedVertexState != null) {
            Layout<V,E> layout = vv.getGraphLayout();
            if(e.getModifiers() == modifiers || e.getModifiers() == moveModifiers) {
                rect.setFrameFromDiagonal(down,down);
                // p is the screen point for the mouse event
                Point2D p = e.getPoint();
                // take away the view transform
                Point2D ip = p;//vv.getRenderContext().getBasicTransformer().inverseViewTransform(p);

                vertex = pickSupport.getVertex(layout, ip.getX(), ip.getY());
                if(vertex != null) {
                    if(pickedVertexState.isPicked(vertex) == false) {
                    	pickedVertexState.clear();
                    	pickedVertexState.pick(vertex, true);
                    }
                    // layout.getLocation applies the layout transformer so
                    // q is transformed by the layout transformer only
                    Point2D q = layout.transform(vertex);
                    // transform the mouse point to graph coordinate system
                    Point2D gp = vv.getRenderContext().getMultiLayerTransformer().inverseTransform(Layer.LAYOUT, ip);

                    offsetx = (float) (gp.getX()-q.getX());
                    offsety = (float) (gp.getY()-q.getY());
                } else if((edge = pickSupport.getEdge(layout, ip.getX(), ip.getY())) != null) {
                    pickedEdgeState.clear();
                    pickedEdgeState.pick(edge, true);
                } else {
                    vv.addPostRenderPaintable(lensPaintable);
                	pickedEdgeState.clear();
                    pickedVertexState.clear();
                }
                
            } else if(e.getModifiers() == addToSelectionModifiers) {
                vv.addPostRenderPaintable(lensPaintable);
                rect.setFrameFromDiagonal(down,down);
                Point2D p = e.getPoint();
                // remove view transform
                Point2D ip = p;//vv.getRenderContext().getBasicTransformer().inverseViewTransform(p);
                vertex = pickSupport.getVertex(layout, ip.getX(), ip.getY());
                if(vertex != null) {
                    boolean wasThere = pickedVertexState.pick(vertex, !pickedVertexState.isPicked(vertex));
                    if(wasThere) {
                        vertex = null;
                    } else {

                        // layout.getLocation applies the layout transformer so
                        // q is transformed by the layout transformer only
                        Point2D q = layout.transform(vertex);
                        // translate mouse point to graph coord system
                        Point2D gp = vv.getRenderContext().getMultiLayerTransformer().inverseTransform(Layer.LAYOUT, ip);

                        offsetx = (float) (gp.getX()-q.getX());
                        offsety = (float) (gp.getY()-q.getY());
                    }
                } else if((edge = pickSupport.getEdge(layout, ip.getX(), ip.getY())) != null) {
                    pickedEdgeState.pick(edge, !pickedEdgeState.isPicked(edge));
                }
            }
        }
        if(vertex != null) e.consume();
    }
	
    @Override
	@SuppressWarnings("unchecked")
    public void mouseDragged(MouseEvent e) {
        if(locked == false) {
            VisualizationViewer<V,E> vv = (VisualizationViewer)e.getSource();
            if(vertex != null) {
            	if (e.getModifiers() == moveModifiers) {
            		Point p = e.getPoint();
            		Point2D graphPoint = vv.getRenderContext().getMultiLayerTransformer().inverseTransform(p);
            		Point2D graphDown = vv.getRenderContext().getMultiLayerTransformer().inverseTransform(down);
            		Layout<V,E> layout = vv.getGraphLayout();
            		double dx = graphPoint.getX()-graphDown.getX();
            		double dy = graphPoint.getY()-graphDown.getY();
            		PickedState<V> ps = vv.getPickedVertexState();
            		
            		for(V v : ps.getPicked()) {
            			Point2D vp = layout.transform(v);
            			vp.setLocation(vp.getX()+dx, vp.getY()+dy);
            			layout.setLocation(v, vp);
            		}
            		down = p;
            	}
            } else {
                Point2D out = e.getPoint();
                if(e.getModifiers() == this.addToSelectionModifiers ||
                        e.getModifiers() == modifiers) {
                    rect.setFrameFromDiagonal(down,out);
                }
            }
            if(vertex != null && e.getModifiers() == moveModifiers) e.consume();
            vv.repaint();
        }
    }
    
    @Override
    public void mouseEntered(MouseEvent e) {
    }

}
