
package org.uweschmidt.dijkstravis.graph.jung;

import org.apache.commons.collections15.Factory;
import org.uweschmidt.dijkstravis.graph.INode;


public class MyNodeFactory implements Factory<INode> {

	private int idCounter;

	private static MyNodeFactory instance = new MyNodeFactory();

	public static MyNodeFactory getInstance() {
		return instance;
	}

	private MyNodeFactory() {
		reset();
	}

	public void reset() {
		idCounter = 1;
	}
	
	public void setIdCounter(int idCounter) {
		this.idCounter = idCounter;
	}

	public MyNode create() {
		return new MyNode(idCounter++);
	}

}
