
package co.edu.poli.medgraph.algoritmo;

public interface AlgorithmProgressListener<T> {
	
	public void stepChanged(int step, T changes);
	public void initialized(int maxSteps);
	public void reset();

}
