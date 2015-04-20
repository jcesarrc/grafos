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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Locale;

import javax.swing.ImageIcon;

import org.apache.commons.collections15.Transformer;
import org.uweschmidt.dijkstravis.graph.GraphChangeListener;
import org.uweschmidt.dijkstravis.graph.GraphManager;
import org.uweschmidt.dijkstravis.graph.IEdge;
import org.uweschmidt.dijkstravis.graph.IGraph;
import org.uweschmidt.dijkstravis.graph.INode;
import org.uweschmidt.dijkstravis.graph.algorithm.DijkstraAlgorithmManager;
import org.uweschmidt.dijkstravis.graph.algorithm.DijkstraStepChanges;
import org.uweschmidt.dijkstravis.gui.GraphPanel;
import org.uweschmidt.dijkstravis.gui.jung.animation.animations.Animation;
import org.uweschmidt.dijkstravis.gui.jung.animation.renderer.MyAnimationEdgeRenderer;
import org.uweschmidt.dijkstravis.gui.jung.animation.renderer.MyAnimationNodeRenderer;
import org.uweschmidt.dijkstravis.language.LocaleChangeListener;
import org.uweschmidt.dijkstravis.language.LocaleManager;
import org.uweschmidt.dijkstravis.util.SC;

import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.VisualizationServer.Paintable;
import edu.uci.ics.jung.visualization.control.AbstractGraphMousePlugin;

public class MyInfoOverlayGraphMousePlugin extends AbstractGraphMousePlugin implements MouseMotionListener, LocaleChangeListener, GraphChangeListener {

	private final static Color RECT_COLOR = new Color(0, 0, 0, 150);
	private final static ImageIcon ICON_LEFT = new ImageIcon(GraphPanel.class.getResource("resources/icons/32/mouse_left.png"));
	private final static ImageIcon ICON_RIGHT = new ImageIcon(GraphPanel.class.getResource("resources/icons/32/mouse_right.png"));

	private INode node = null;
	private IEdge edge = null;
	private IGraph graph = null;
	
	private MyAnimationEdgeRenderer edgeRenderer;
	private MyAnimationNodeRenderer nodeRenderer;
	
	private VisualizationViewer<INode, IEdge> vv;
	private String CLICK_RIGHT, EMPTY_DRAG;
	private String CLICK_LEFT, CLICK_LEFT_NODE, CLICK_LEFT_EDGE;
	private String DRAG_LEFT, DRAG_LEFT_NODE, EMPTY_CLICK;
	private String DRAG_RIGHT, DRAG_RIGHT_NODE, DRAG_RIGHT_EDGE;
	
	public MyInfoOverlayGraphMousePlugin(VisualizationViewer<INode, IEdge> vv) {
		super(0);
		this.vv = vv;
		graph = (IGraph)vv.getGraphLayout().getGraph();
		edgeRenderer = (MyAnimationEdgeRenderer) vv.getRenderer().getEdgeRenderer();
		nodeRenderer = (MyAnimationNodeRenderer) vv.getRenderer().getVertexRenderer();
		
		vv.addPostRenderPaintable(new InfoPaintable());
		GraphManager.addGraphChangeListener(this);
		LocaleManager.addLocaleChangeListener(this);
		localeChanged();
	}
	
	public void graphReplaced(IGraph graph, Transformer<INode, Point2D> layout) {
		this.graph = graph;
	}

	public void graphChanged() {
	}
	
	
	public void localeChanged() {
		CLICK_RIGHT = SC.t("%s: %s", "click", "show_context_menu");
		EMPTY_CLICK = "";//SC.t("%s:", "click");
		
		CLICK_LEFT = SC.t("%s: %s", "dbl_click", "create_node");
		CLICK_LEFT_EDGE = CLICK_LEFT; 
		CLICK_LEFT_NODE = SC.t("%s: %s", "click", "select_node");
		
		DRAG_LEFT = SC.t("%s: %s", "drag", "select_nodes");
		EMPTY_DRAG = "";//SC.t("%s:", "drag"); 
		DRAG_LEFT_NODE = SC.t("%s: %s", "drag", "create_edge");

		DRAG_RIGHT = SC.t("%s: %s", "drag", "pan_graph");
		DRAG_RIGHT_EDGE = DRAG_RIGHT;
		DRAG_RIGHT_NODE = SC.t("%s: %s", "drag", "move_selected_nodes");
		
		vv.repaint();
	}

	private class InfoPaintable implements Paintable {
		
		private static final int XPAD = 10, X = 10, Y = 10, H = 30, BOX_W = 235;
		
		public InfoPaintable() {
		}
		
		public void paint(Graphics g) {
			final boolean algo = DijkstraAlgorithmManager.isAlgoRunning();
			String s = null;
			String dragLeft = null, clickLeft = null, dragRight = null;
			String clickRight = algo ? EMPTY_CLICK : CLICK_RIGHT;
			if (node != null && graph.containsNode(node)) {

				String att = null;
				Animation<INode> a = nodeRenderer.getAnimation(node);
				DijkstraStepChanges changes = DijkstraAlgorithmManager.getCurrentStepChanges();
				
				if (a != null && !a.isFinished()) {
					att = SC.t("animation_in_progress");
				} else {
					switch (node.getAttribute()) {
						case PATH_IMPROVED:
						case PATH_IMPROVED_NEXT_SETTLED:
							att = String.format(SC.t("path_improved_FS"), changes.getOldDistance(node));
							break;
						case CURRENTLY_SETTLED:
							att = SC.t("node_expanding");
							break;
						case NOT_VISITED:
							att = SC.t("node_not_visited");
							break;
						case PATH_FOUND:
						case PATH_FOUND_NEXT_SETTLED:
							att = SC.t("path_found");
							break;
						case SETTLED:
							att = SC.t("path_settled");
							break;
						case START_NODE:
							att = SC.t("start_node");
							break;
						case VISITED:
						case VISITED_NEXT_SETTLED:
							att = SC.t("node_visited");
							break;
					}
				}
				
				String dist = node.getDistance() == Double.POSITIVE_INFINITY ? SC.t("infinity") : String.format(Locale.ENGLISH, "%.1f", node.getDistance());
				s = String.format("%s %s%s" + (algo ? ", %s: %s, %s" : (node == graph.getStart() ? ", " + SC.t("start_node") : "")), SC.t("node"), node.getId(), node.getName() == null ? "" : " ("+node.getName()+")", SC.t("path_length"), dist, att);
				dragLeft = DRAG_LEFT_NODE;
				clickLeft = CLICK_LEFT_NODE;
				dragRight = algo ? (GraphPanel.getInstance().isGraphBackground() ? EMPTY_DRAG : DRAG_RIGHT) : DRAG_RIGHT_NODE;
			} else if (edge != null && graph.containsEdge(edge)) {
				
				String att = null;
				Animation<IEdge> a = edgeRenderer.getAnimation(edge);
				INode dest = graph.getDest(edge);
				
				if (a != null && !a.isFinished()) {
					att = SC.t("animation_in_progress");
				} else {
					switch (edge.getAttribute()) {
						case ADDED_TO_SHORTEST_PATH:
							att = String.format(SC.t("edge_added_FS"), dest.getId());
							break;
						case REMOVED_FROM_SHORTEST_PATH:
							att = String.format(SC.t("edge_removed_FS"), dest.getId());
							break;
						case NOT_VISITED:
						case VISITED:
							att = SC.t("edge_no_shortest_path");
							break;
						case ON_SHORTEST_PATH:
							switch (graph.getDest(edge).getAttribute()) {
								case CURRENTLY_SETTLED:
								case SETTLED:
									att = String.format(SC.t("edge_shortest_path_FS"), dest.getId());
									break;
								default:
									att = String.format(SC.t("edge_path_FS"), dest.getId());
							}
							break;
					}
				}
				
				s = String.format(Locale.ENGLISH, "%s [%s,%s], %s: %.1f" + (algo ? ", %s" : ""), SC.t("edge"), graph.getSource(edge), graph.getDest(edge), SC.t("length"), edge.getLength(), att);
				dragLeft = EMPTY_DRAG;
				clickLeft = CLICK_LEFT_EDGE;
				dragRight = GraphPanel.getInstance().isGraphBackground() ? EMPTY_DRAG : DRAG_RIGHT_EDGE;
			} else {
				dragLeft = DRAG_LEFT;
				clickLeft = CLICK_LEFT;
				dragRight = GraphPanel.getInstance().isGraphBackground() ? EMPTY_DRAG : DRAG_RIGHT;
			}
			
			Color oldColor = g.getColor();
			Font oldFont = g.getFont();
			
			if (s != null) {
				g.setFont(new Font("Courier", Font.PLAIN, 14));
				final Rectangle2D rect = g.getFontMetrics().getStringBounds(s, g);
				int W = (int)rect.getWidth() + 2*XPAD;
				
				g.setColor(RECT_COLOR);
				g.fillRoundRect(X, Y, W, H, 10, 10);
				g.setColor(Color.WHITE);
				g.drawString(s, X + XPAD, Y + 20);
			}
			
			g.setFont(oldFont.deriveFont(13f));
			if (!algo)
				drawBox(g, false, X, (int)vv.getSize().getHeight() - 10, dragLeft, clickLeft);
			if (!(algo && GraphPanel.getInstance().isGraphBackground()))
				drawBox(g, true, (int)vv.getSize().getWidth()-10-BOX_W, (int)vv.getSize().getHeight() - 10, dragRight, clickRight);
			
			if (graph.getNumberOfNodes() == 0) {
				String s1 = SC.t("graph_edit_getting_started1");
				String s2 = SC.t("graph_edit_getting_started2");
				final Rectangle2D rect = g.getFontMetrics().getStringBounds(s1, g);
				int W = (int)rect.getWidth() + 2*XPAD;
				int H = 50;
				int X = (int)vv.getCenter().getX()-W/2;
				int Y = (int)vv.getCenter().getY()-2*H;
				g.setColor(RECT_COLOR);
				g.fillRoundRect(X, Y, W, H, 10, 10);
				g.setColor(Color.WHITE);
				g.drawString(s1, X + XPAD, Y + 20);
				g.drawString(s2, X + XPAD, Y + 40);
//				g.setColor(RECT_COLOR);
//				g.drawLine(X+W/2-10, Y+H, InfoPaintable.X + BOX_W/2, (int)vv.getSize().getHeight()-50);
//				g.drawLine(X+W/2+10, Y+H, (int)vv.getSize().getWidth()-10-BOX_W/2, (int)vv.getSize().getHeight()-50);
			}
			
			g.setFont(oldFont);
			g.setColor(oldColor);
		}
		
		private void drawBox(Graphics g, boolean right, int X, int Y, String s1, String s2) {
			g.setColor(RECT_COLOR);
			g.fillRoundRect(X, Y - 40, BOX_W, 40, 10, 10);
			g.drawImage((right ? ICON_RIGHT : ICON_LEFT).getImage(), X + (right ? BOX_W - 32 : 0), Y - 35, null);
			g.setColor(Color.WHITE);
			g.drawString(s1, X + (right ? 8 : 32), Y - 40 + 18);
			g.drawString(s2, X + (right ? 8 : 32), Y - 40 + 32);
		}

		public boolean useTransform() {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public void mouseMoved(MouseEvent e) {
		final Point2D p = e.getPoint();
		final GraphElementAccessor<INode, IEdge> pickSupport = vv.getPickSupport();
		
		final INode node = pickSupport.getVertex(vv.getModel().getGraphLayout(), p.getX(), p.getY());
		if (node != null/* && this.node != node*/) {
			this.node = node;
			this.edge = null;
			vv.repaint();
			return;
		}

		final IEdge edge = pickSupport.getEdge(vv.getModel().getGraphLayout(), p.getX(), p.getY());
		if (edge != null/* && this.edge != edge*/) {
			this.node = null;
			this.edge = edge;
			vv.repaint();
			return;
		}
		
		if (this.node != node || this.edge != edge) {
			this.node = null;
			this.edge = null;		
			vv.repaint();
		}
		
	}

	public void mouseDragged(MouseEvent e) {
	}
	
}