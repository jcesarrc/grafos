
package org.uweschmidt.dijkstravis.graph.algorithm;

public interface AlgorithmProgressListener<T> {
	
	public void stepChanged(int step, T changes);
	public void initialized(int maxSteps);
	public void reset();

}
