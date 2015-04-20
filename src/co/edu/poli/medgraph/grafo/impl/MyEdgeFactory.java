package co.edu.poli.medgraph.grafo.impl;
import org.apache.commons.collections15.Factory;


public class MyEdgeFactory implements Factory<MyEdge> {

	private int counter = 1;
	private static MyEdgeFactory instance = new MyEdgeFactory();

	public static MyEdgeFactory getInstance() {
		return instance;
	}

	private MyEdgeFactory() {
	}

        @Override
	public MyEdge create() {
		return new MyEdge(counter++, 0);
	}

	public void reset() {
		counter = 1;
	}

}