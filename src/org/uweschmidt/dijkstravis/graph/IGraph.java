
package org.uweschmidt.dijkstravis.graph;

import java.util.Collection;

public interface IGraph {

	public void setStart(INode start);
	public INode getStart();

	public Collection<IEdge> getOutEdges(INode source);
	public Collection<IEdge> getInEdges(INode source);
	public INode getDest(IEdge edge);
	public INode getSource(IEdge edge);
	public Collection<INode> getSuccessors(INode node);
	
	public boolean containsNode(INode node);
	public boolean containsEdge(IEdge edge);
	public IEdge findEdge(INode v1, INode v2);
	
	public boolean addNode(INode node);
	public boolean addEdge(IEdge e, INode v1, INode v2);
	
	public void highlight(final INode node);
	public void highlight(final IEdge edge);

	public Collection<INode> getNodes();

	public int getNumberOfNodes();

}
