
package org.uweschmidt.dijkstravis.graph.algorithm;

import org.uweschmidt.dijkstravis.graph.IGraph;
import org.uweschmidt.dijkstravis.graph.INode;

public interface GraphAlgorithm<T> {
	
	public void initialize(INode... nodes);
	public void stepBackward();
	public void stepForward();
	public void run();
	public boolean isAtTheBeginning();
	public boolean isFinished();
	public void reset();
	public void setGraph(IGraph graph);
	
	public INode getStart();
	
	public void addProgressListener(AlgorithmProgressListener<T> l);
	public void removeProgressListener(AlgorithmProgressListener<T> l);

}
