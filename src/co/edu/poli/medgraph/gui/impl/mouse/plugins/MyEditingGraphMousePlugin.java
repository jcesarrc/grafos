
package co.edu.poli.medgraph.gui.impl.mouse.plugins;

import co.edu.poli.medgraph.grafo.IEdge;
import co.edu.poli.medgraph.grafo.INode;
import co.edu.poli.medgraph.gui.impl.mouse.MyGraphMouse;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import org.apache.commons.collections15.Factory;


import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.EditingGraphMousePlugin;

public class MyEditingGraphMousePlugin extends EditingGraphMousePlugin<INode, IEdge> {
	
	private MyGraphMouse gm;
	
	public MyEditingGraphMousePlugin(MyGraphMouse gm, Layout<INode, IEdge> layout, Factory<INode> vertexFactory, Factory<IEdge> edgeFactory) {
		super(MouseEvent.BUTTON1_MASK,  vertexFactory, edgeFactory);
		this.gm = gm;
		this.cursor = Cursor.getDefaultCursor();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2 && !MyGraphMouse.isOverNode(e)) {
			super.mousePressed(e);
			gm.mouseMoved(e);
			e.consume();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void mouseReleased(MouseEvent e) {
		super.mouseReleased(e);
		// fixes bug when an arrow is pulled and left unconnected
		// (artefact remains), therefore repaint() necessary, triggers repaint
		gm.mouseMoved(e);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void mousePressed(MouseEvent e) {
		final VisualizationViewer<INode, IEdge> vv = (VisualizationViewer<INode, IEdge>) e.getSource();
		final Point2D p = e.getPoint();
		GraphElementAccessor<INode, IEdge> pickSupport = vv.getPickSupport();
		final INode vertex = pickSupport.getVertex(vv.getModel().getGraphLayout(), p.getX(), p.getY());
		if (vertex != null && e.getClickCount() != 2) {
			super.mousePressed(e);
		}
	}
}