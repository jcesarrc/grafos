package co.edu.poli.medgraph.grafo.impl;
import co.edu.poli.medgraph.algoritmo.AlgorithmProgressListener;
import co.edu.poli.medgraph.algoritmo.DijkstraAlgorithmManager;
import co.edu.poli.medgraph.algoritmo.DijkstraStepChanges;
import co.edu.poli.medgraph.grafo.GraphManager;
import co.edu.poli.medgraph.grafo.IEdge;
import co.edu.poli.medgraph.grafo.IGraph;
import co.edu.poli.medgraph.grafo.INode;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.util.Pair;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("serial")
public class MyGraph extends DirectedSparseGraph<INode, IEdge> implements IGraph, AlgorithmProgressListener<DijkstraStepChanges> {

	private INode root;
	private INode highlightedNode;
	private IEdge highlightedEdge;
	private List<Object> highlighted = new LinkedList<Object>();
	
	public MyGraph() {
		DijkstraAlgorithmManager.addAlgorithmProgressListener(this);
	}
	
	public void initialized(int maxSteps) {
		resetOldHighlighted();		
	}
	
	public void reset() {
		resetOldHighlighted();
	}
	
	public void stepChanged(int step, DijkstraStepChanges changes) {	
		if (highlightedNode != null) {
			INode backup = highlightedNode;
			resetOldHighlighted();
			highlight(backup);
		}
	}

	private void setHighlighted(final INode node, final boolean state) {
		this.highlightedNode = node;
		
		INode current = highlightedNode;

		if (current != null && current != getStart()) {
			current.setHighlighted(state);
			highlighted.add(current);
		}

		while (current != null && current != getStart()) {
			if (current.getPredecessorEdge() == null) break;
			current.getPredecessorEdge().setHighlighted(state);
			highlighted.add(current.getPredecessorEdge());
			current = getSource(current.getPredecessorEdge());
		}
	}

	public synchronized void highlight(final INode node) {
		resetOldHighlighted();
		if (node != null) {
			setHighlighted(node, true);
		}
	}
	
	public synchronized void highlight(final IEdge edge) {
		resetOldHighlighted();
		if (edge != null) {
			edge.setHighlighted(true);
			highlightedEdge = edge;
		}
	}
	
	private synchronized void resetOldHighlighted() {
		if (highlightedNode != null) {
			for (Object o : highlighted) {
				if (o instanceof IEdge)
					((IEdge)o).setHighlighted(false);
				else if (o instanceof INode)
					((INode)o).setHighlighted(false);
			}
			highlighted.clear();
		}
		if (highlightedEdge != null) {
			highlightedEdge.setHighlighted(false);
		}
		highlightedNode = null;
		highlightedEdge = null;
	}

	public void setStart(final INode root) {
		this.root = root;
		if (root != null)
			root.setIntermediate(false);
	}

	public INode getStart() {
            // Si se borra el nodo inicial
		if (containsVertex(root))
			return root;
		else
			return null;
	}

	@Override
	public Collection<IEdge> getOutEdges(final INode vertex) {
		return super.getOutEdges(vertex);
	}

	@Override
	public INode getDest(final IEdge directed_edge) {
		return super.getDest(directed_edge);
	}

	public Collection<INode> getNodes() {
		return getVertices();
	}

	public int getNumberOfNodes() {
		return getVertexCount();
	}
	
	@Override
	public boolean addEdge(final IEdge e, final INode v1, final INode v2) {
		return addEdge(e, new Pair<INode>(v1, v2));
	}

	@Override
	public boolean addEdge(final IEdge edge, final Pair<? extends INode> endpoints) {
		final INode v1 = endpoints.getFirst();
		final INode v2 = endpoints.getSecond();
		if (findEdge(v1, v2) == null && v1 != v2) {
			final boolean addEdge = super.addEdge(edge, endpoints);
			GraphManager.graphChanged();
			return addEdge;
		} else
			return false;
	}

	@Override
	public boolean removeEdge(IEdge edge) {
		final boolean removeEdge = super.removeEdge(edge);
		GraphManager.graphChanged();
		return removeEdge;
	}
	
	public boolean addNode(INode node) {
		return addVertex(node);
	}

	@Override
	public boolean addVertex(INode vertex) {
		final boolean addVertex = super.addVertex(vertex);
		GraphManager.graphChanged();
		return addVertex;
	}
	
	@Override
	public boolean removeVertex(INode vertex) {
		final boolean removeVertex = super.removeVertex(vertex);
		GraphManager.graphChanged();
		return removeVertex;
	}
	
	public boolean containsNode(INode node) {
		return containsVertex(node);
	}

}
