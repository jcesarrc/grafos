
package org.uweschmidt.dijkstravis.graph.jung;

import org.apache.commons.collections15.Factory;
import org.uweschmidt.dijkstravis.graph.IEdge;


public class MyEdgeFactory implements Factory<IEdge> {

	private int counter = 1;
	private static MyEdgeFactory instance = new MyEdgeFactory();

	public static MyEdgeFactory getInstance() {
		return instance;
	}

	private MyEdgeFactory() {
	}

	public MyEdge create() {
		return new MyEdge(counter++, 0);
	}

	public void reset() {
		counter = 1;
	}

}