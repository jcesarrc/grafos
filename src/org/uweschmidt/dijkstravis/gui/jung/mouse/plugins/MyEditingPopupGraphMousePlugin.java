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

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import org.apache.commons.collections15.Factory;
import org.jdesktop.application.Application;
import org.uweschmidt.dijkstravis.DijkstraVisApp;
import org.uweschmidt.dijkstravis.graph.IGraph;
import org.uweschmidt.dijkstravis.graph.INode;
import org.uweschmidt.dijkstravis.util.SC;

import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.EditingPopupGraphMousePlugin;
import edu.uci.ics.jung.visualization.picking.PickedState;

/**
 * Based on source code of {@link EditingPopupGraphMousePlugin}.
 */
public class MyEditingPopupGraphMousePlugin<V, E> extends EditingPopupGraphMousePlugin<V, E> {
	public MyEditingPopupGraphMousePlugin(Layout<V, E> layout, Factory<V> vertexFactory, Factory<E> edgeFactory) {
		super(layout, vertexFactory, edgeFactory);
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (!e.isShiftDown() && !e.isControlDown() && e.getButton() == MouseEvent.BUTTON3) {
			handlePopup(e);
			e.consume();
		}
	}
	
	@Override
	@SuppressWarnings({ "unchecked", "serial" })
	protected void handlePopup(MouseEvent e) {
        final VisualizationViewer<V,E> vv =
            (VisualizationViewer<V,E>)e.getSource();
        final Layout<V,E> layout = vv.getGraphLayout();
        final Graph<V,E> graph = layout.getGraph();
        final Point2D p = e.getPoint();
        final Point2D ivp = p;
        GraphElementAccessor<V,E> pickSupport = vv.getPickSupport();
        if(pickSupport != null) {
            
            final V vertex = pickSupport.getVertex(layout, ivp.getX(), ivp.getY());
            final E edge = pickSupport.getEdge(layout, ivp.getX(), ivp.getY());
            final PickedState<V> pickedVertexState = vv.getPickedVertexState();
            final PickedState<E> pickedEdgeState = vv.getPickedEdgeState();
            JPopupMenu popup = new JPopupMenu();
            
            if(vertex != null) {
            	final Set<V> picked = pickedVertexState.getPicked();
            	if(picked.size() > 1) {
            		if(graph instanceof UndirectedGraph == false) {
            			JMenu directedMenu = new JMenu(SC.t("create_edge"));
            			for(final V other : picked) {
            				if (other.equals(vertex) || graph.findEdge(vertex, other) != null) continue;
            				directedMenu.add(new AbstractAction("["+vertex+","+other+"]") {
            					public void actionPerformed(ActionEvent e) {
            						graph.addEdge(edgeFactory.create(), vertex, other, EdgeType.DIRECTED);
            						vv.repaint();
            					}
            				});
            			}
            			if (directedMenu.getItemCount() > 0)
            				popup.add(directedMenu);
            		}
                }
            	
            	if (popup.getComponentCount() > 0) popup.addSeparator();
            	
                popup.add(new AbstractAction(String.format(SC.t("delete_node_FS"), vertex)) {
                    public void actionPerformed(ActionEvent e) {
                        pickedVertexState.pick(vertex, false);
                        graph.removeVertex(vertex);
                        vv.repaint();
                    }});
                if(picked.size() > 1) {
                	popup.add(new AbstractAction(SC.t("delete_selected_nodes")) {
                		public void actionPerformed(ActionEvent e) {
                			for(final V vertex : picked) {
                				graph.removeVertex(vertex);
                			}
                			vv.repaint();
                		}});
                }
                if (graph instanceof IGraph) {
                	popup.addSeparator();

                	final IGraph iyg = (IGraph)graph;
                	final INode root = iyg.getStart();
                	popup.add(new AbstractAction(vertex.equals(root) ? SC.t("deselect_start_node") : SC.t("select_start_node")) {
                		public void actionPerformed(ActionEvent e) {
                			MyStartnodeGraphMousePlugin.setStartNode(iyg, root, (INode)vertex);
                			vv.repaint();
                		}});
                	if (!vertex.equals(root)) {
                		final JCheckBoxMenuItem intermediate = new JCheckBoxMenuItem();
                		intermediate.setAction(new AbstractAction(SC.t("intermediate_node")) {
                			public void actionPerformed(ActionEvent e) {
                				((INode)vertex).setIntermediate(intermediate.isSelected());
                				vv.repaint();
                			}});
                		popup.add(intermediate);
                		intermediate.setSelected(((INode)vertex).isIntermediate());
                	}
                	final INode v = ((INode)vertex);
                	popup.add(new AbstractAction(SC.t("set_node_name")) {
                		public void actionPerformed(ActionEvent e) {
							String name = JOptionPane.showInputDialog(Application.getInstance(DijkstraVisApp.class).getMainFrame(), SC.t("set_node_name"), v.getName());
							if (name == null) {
							} else if (name.length() == 0) {
                				v.setName(null);
                			} else {
                				v.setName(name);
                			}
                			vv.repaint();
                		}});
                }
            } else if (edge != null) {
            	Pair<INode> pair = (Pair<INode>)graph.getEndpoints(edge);
                popup.add(new AbstractAction(String.format(SC.t("delete_edge_FS"), String.format("[%s,%s]", pair.getFirst(), pair.getSecond()))) {
                    public void actionPerformed(ActionEvent e) {
                        pickedEdgeState.pick(edge, false);
                        graph.removeEdge(edge);
                        vv.repaint();
                    }});
            } else {
                popup.add(new AbstractAction(SC.t("create_node")) {
                    public void actionPerformed(ActionEvent e) {
                        V newVertex = vertexFactory.create();
                        layout.setLocation(newVertex, vv.getRenderContext().getMultiLayerTransformer().inverseTransform(p));
                        Layout<V,E> layout = vv.getModel().getGraphLayout();
                        for(V vertex : graph.getVertices()) {
                            layout.lock(vertex, true);
                        }
                        graph.addVertex(newVertex);
                        for(V vertex : graph.getVertices()) {
                            layout.lock(vertex, false);
                        }
                        vv.repaint();
                    }
                });
            }
            if(popup.getComponentCount() > 0) {
                popup.show(vv, e.getX(), e.getY());
            }
        }
    }
}