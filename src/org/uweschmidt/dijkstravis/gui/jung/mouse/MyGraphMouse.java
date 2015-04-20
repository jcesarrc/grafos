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
package org.uweschmidt.dijkstravis.gui.jung.mouse;

import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.apache.commons.collections15.Factory;
import org.uweschmidt.dijkstravis.graph.IEdge;
import org.uweschmidt.dijkstravis.graph.INode;
import org.uweschmidt.dijkstravis.graph.algorithm.AlgorithmProgressListener;
import org.uweschmidt.dijkstravis.graph.algorithm.DijkstraAlgorithmManager;
import org.uweschmidt.dijkstravis.graph.algorithm.DijkstraStepChanges;
import org.uweschmidt.dijkstravis.graph.jung.MyEdgeFactory;
import org.uweschmidt.dijkstravis.graph.jung.MyNodeFactory;
import org.uweschmidt.dijkstravis.gui.GraphPanel;
import org.uweschmidt.dijkstravis.gui.jung.mouse.plugins.MyEditingGraphMousePlugin;
import org.uweschmidt.dijkstravis.gui.jung.mouse.plugins.MyEditingPopupGraphMousePlugin;
import org.uweschmidt.dijkstravis.gui.jung.mouse.plugins.MyHighlighterGraphMousePlugin;
import org.uweschmidt.dijkstravis.gui.jung.mouse.plugins.MyInfoOverlayGraphMousePlugin;
import org.uweschmidt.dijkstravis.gui.jung.mouse.plugins.MyPickingGraphMousePlugin;
import org.uweschmidt.dijkstravis.gui.jung.mouse.plugins.MyRotatingGraphMousePlugin;
import org.uweschmidt.dijkstravis.gui.jung.mouse.plugins.MyStartnodeGraphMousePlugin;
import org.uweschmidt.dijkstravis.gui.jung.mouse.plugins.MyTranslatingGraphMousePlugin;

import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AnimatedPickingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.EditingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.PickingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.PluggableGraphMouse;
import edu.uci.ics.jung.visualization.control.RotatingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.control.ScalingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.TranslatingGraphMousePlugin;

public class MyGraphMouse extends PluggableGraphMouse implements AlgorithmProgressListener<DijkstraStepChanges> {

	private VisualizationViewer<INode, IEdge> vv;
	private Factory<INode> vertexFactory;
	private Factory<IEdge> edgeFactory;

	private MyStartnodeGraphMousePlugin startNodePlugin;
	private EditingGraphMousePlugin<INode, IEdge> editingPlugin;
	private ScalingGraphMousePlugin scalingPlugin;

	private PickingGraphMousePlugin<INode, IEdge> pickingPlugin;
	private AnimatedPickingGraphMousePlugin<INode, IEdge> animatedPickingPlugin;

	private TranslatingGraphMousePlugin translatingPlugin;
	private RotatingGraphMousePlugin rotatingPlugin;

	private MyInfoOverlayGraphMousePlugin infoPlugin;
	private MyHighlighterGraphMousePlugin mouseOverHighlighter;

	private MyEditingPopupGraphMousePlugin<INode, IEdge> popupEditingPlugin;
	
	private GraphPanel gp;

	public MyGraphMouse(GraphPanel gp, VisualizationViewer<INode, IEdge> vv) {
		DijkstraAlgorithmManager.addAlgorithmProgressListener(this);
		this.vertexFactory = MyNodeFactory.getInstance();
		this.edgeFactory = MyEdgeFactory.getInstance();
		this.gp = gp;
		this.vv = vv;
		loadPlugins();
		addAllPlugins();
		gp.addPropertyChangeListener("graphBackground", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (MyGraphMouse.this.gp.isGraphBackground()) {
					remove(scalingPlugin);
					remove(translatingPlugin);
					remove(rotatingPlugin);
				} else {
					add(scalingPlugin);
					add(translatingPlugin);
					add(rotatingPlugin);
				}
			}
		});
	}

	private void loadPlugins() {
		pickingPlugin = new MyPickingGraphMousePlugin<INode, IEdge>(InputEvent.BUTTON1_MASK, InputEvent.BUTTON1_MASK | Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), InputEvent.BUTTON3_MASK);
		animatedPickingPlugin = new AnimatedPickingGraphMousePlugin<INode, IEdge>(InputEvent.BUTTON1_MASK | InputEvent.CTRL_MASK);
		scalingPlugin = new ScalingGraphMousePlugin(new CrossoverScalingControl(), 0, 1.1f, 1 / 1.1f);
		rotatingPlugin = new MyRotatingGraphMousePlugin(InputEvent.BUTTON3_MASK | MouseEvent.SHIFT_MASK);
		translatingPlugin = new MyTranslatingGraphMousePlugin(InputEvent.BUTTON3_MASK);
		popupEditingPlugin = new MyEditingPopupGraphMousePlugin<INode, IEdge>(null, vertexFactory, edgeFactory);
		startNodePlugin = new MyStartnodeGraphMousePlugin();
		editingPlugin = new MyEditingGraphMousePlugin(this, null, vertexFactory, edgeFactory);
		mouseOverHighlighter = new MyHighlighterGraphMousePlugin();
		infoPlugin = new MyInfoOverlayGraphMousePlugin(vv);
	}

	public ScalingControl getScaler() {
		return scalingPlugin.getScaler();
	}

	private void addAllPlugins() {
		add(infoPlugin);
		add(mouseOverHighlighter);

		if (gp.isNoGraphBackground()) {			
			add(scalingPlugin);
			add(translatingPlugin);
			add(rotatingPlugin);
		}
		
		add(editingPlugin);
		add(startNodePlugin);
		add(pickingPlugin);
		add(popupEditingPlugin);
	}

	public void setZoomAtMouse(boolean zoomAtMouse) {
		scalingPlugin.setZoomAtMouse(zoomAtMouse);
	}

	public void setLayout(Layout<INode, IEdge> layout) {
		editingPlugin.setLayout(layout);
		popupEditingPlugin.setLayout(layout);
	}

	public void stepChanged(int step, DijkstraStepChanges changes) {
	}

	public void initialized(int maxSteps) {
		remove(pickingPlugin);
		remove(animatedPickingPlugin);
		remove(editingPlugin);
		remove(startNodePlugin);
		remove(popupEditingPlugin);
		
		// deselect all nodes and edges
		vv.getPickedVertexState().clear();
        vv.getPickedEdgeState().clear();
	}

	public void reset() {
		addAllPlugins();
	}

	private static enum Over {
		NODE, EDGE, NOTHING
	};

	@SuppressWarnings("unchecked")
	private static Over isOver(final MouseEvent e) {
		final VisualizationViewer<INode, IEdge> vv = (VisualizationViewer<INode, IEdge>) e.getSource();
		final Point2D p = e.getPoint();
		final GraphElementAccessor<INode, IEdge> pickSupport = vv.getPickSupport();
		final INode vertex = pickSupport.getVertex(vv.getModel().getGraphLayout(), p.getX(), p.getY());
		if (vertex != null)
			return Over.NODE;
		final IEdge edge = pickSupport.getEdge(vv.getModel().getGraphLayout(), p.getX(), p.getY());
		if (edge != null)
			return Over.EDGE;
		return Over.NOTHING;
	}

	public static boolean isOverNode(final MouseEvent e) {
		return isOver(e) == Over.NODE;
	}

	public static boolean isOverEdge(final MouseEvent e) {
		return isOver(e) == Over.EDGE;
	}
}
