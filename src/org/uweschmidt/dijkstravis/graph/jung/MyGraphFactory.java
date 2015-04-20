
package org.uweschmidt.dijkstravis.graph.jung;

import org.apache.commons.collections15.Factory;
import org.uweschmidt.dijkstravis.graph.IEdge;
import org.uweschmidt.dijkstravis.graph.INode;

import edu.uci.ics.jung.graph.Graph;

public class MyGraphFactory implements Factory<Graph<INode, IEdge>> {

	private static final MyGraphFactory instance = new MyGraphFactory();

	public static MyGraphFactory getInstance() {
		return instance;
	}

	private MyGraphFactory() {
	}

	public Graph<INode, IEdge> create() {
		return new MyGraph();
	}

}
