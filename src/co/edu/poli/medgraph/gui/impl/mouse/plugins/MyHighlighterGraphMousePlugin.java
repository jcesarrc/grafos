package co.edu.poli.medgraph.gui.impl.mouse.plugins;

import co.edu.poli.medgraph.algoritmo.DijkstraAlgorithmManager;
import co.edu.poli.medgraph.grafo.GraphChangeListener;
import co.edu.poli.medgraph.grafo.GraphManager;
import co.edu.poli.medgraph.grafo.IEdge;
import co.edu.poli.medgraph.grafo.IGraph;
import co.edu.poli.medgraph.grafo.INode;
import co.edu.poli.medgraph.gui.impl.animation.animations.Animation;
import co.edu.poli.medgraph.gui.impl.animation.renderer.MyAnimationNodeRenderer;
import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractGraphMousePlugin;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import org.apache.commons.collections15.Transformer;

public class MyHighlighterGraphMousePlugin extends AbstractGraphMousePlugin implements MouseMotionListener, GraphChangeListener {
	
	private IGraph graph;
	private INode node = null;
	private IEdge edge = null;

	public MyHighlighterGraphMousePlugin() {
		super(0);
		GraphManager.addGraphChangeListener(this);
	}
	
	public void graphChanged() {
	}
	
	public void graphReplaced(IGraph graph, Transformer<INode, Point2D> layout) {
		this.graph = graph;
	}

	public void mouseDragged(MouseEvent e) {
	}

	@SuppressWarnings("unchecked")
	public void mouseMoved(MouseEvent e) {

		VisualizationViewer<INode, IEdge> vv = (VisualizationViewer<INode, IEdge>) e.getSource();		
		Point2D p = e.getPoint();
		GraphElementAccessor<INode, IEdge> pickSupport = vv.getPickSupport();
		
		INode node = pickSupport.getVertex(vv.getModel().getGraphLayout(), p.getX(), p.getY());
		Animation<INode> a = ((MyAnimationNodeRenderer)vv.getRenderer().getVertexRenderer()).getAnimation(node);
		if (node != null && (a == null || a.isFinished())) {
			this.node = node;
			this.edge = null;
			graph.highlight(node);
			vv.repaint();
			return;
		}
		
		if (!DijkstraAlgorithmManager.isAlgoRunning()) {
			IEdge edge = pickSupport.getEdge(vv.getModel().getGraphLayout(), p.getX(), p.getY());
			if (edge != null) {
				this.edge = edge;
				this.node = null;
				graph.highlight(edge);
				vv.repaint();
				return;
			}
		}
		
		if (this.node != null) {
			graph.highlight((INode)null);
			this.node = null;
			vv.repaint();
		} else if (this.edge != null) {
			graph.highlight((IEdge)null);
			this.edge = null;
			vv.repaint();
		}
	}
}
