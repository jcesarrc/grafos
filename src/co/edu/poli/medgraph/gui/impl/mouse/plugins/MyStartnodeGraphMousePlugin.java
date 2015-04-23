package co.edu.poli.medgraph.gui.impl.mouse.plugins;

import co.edu.poli.medgraph.grafo.IEdge;
import co.edu.poli.medgraph.grafo.IGraph;
import co.edu.poli.medgraph.grafo.INode;
import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractGraphMousePlugin;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;

public class MyStartnodeGraphMousePlugin extends AbstractGraphMousePlugin implements MouseListener {
	public MyStartnodeGraphMousePlugin() {
		super(InputEvent.BUTTON1_MASK);
	}

	@SuppressWarnings("unchecked")
	public void mouseClicked(MouseEvent e) {
		if (checkModifiers(e) && e.getClickCount() == 2) {
			VisualizationViewer<INode, IEdge> vv = (VisualizationViewer<INode, IEdge>) e.getSource();
			Point2D p = e.getPoint();
			GraphElementAccessor<INode, IEdge> pickSupport = vv.getPickSupport();
			INode vertex = pickSupport.getVertex(vv.getModel().getGraphLayout(), p.getX(), p.getY());
			if (vertex != null) {
				IGraph graph = (IGraph) vv.getGraphLayout().getGraph();
				setStartNode(graph, graph.getStart(), vertex);
				e.consume();
			}
		}
	}
	
	public static void setStartNode(IGraph graph, INode oldStart, INode newStart) {
		if (oldStart != null)
			oldStart.setAttribute(INode.Attribute.NOT_VISITED);
		// enable/disable start
		if (graph.getStart() == newStart) {
			newStart.setAttribute(INode.Attribute.NOT_VISITED);
			graph.setStart(null);
		} else {
			newStart.setAttribute(INode.Attribute.START_NODE);
			graph.setStart(newStart);
		}
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}
}