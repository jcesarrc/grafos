
package co.edu.poli.medgraph.grafo.impl;

import org.apache.commons.collections15.Factory;


public class MyNodeFactory implements Factory<MyNode> {

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

        @Override
	public MyNode create() {
		return new MyNode(idCounter++);
	}

}
