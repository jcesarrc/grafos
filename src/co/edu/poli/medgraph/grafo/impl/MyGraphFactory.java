package co.edu.poli.medgraph.grafo.impl;

import co.edu.poli.medgraph.grafo.IEdge;
import co.edu.poli.medgraph.grafo.INode;
import edu.uci.ics.jung.graph.Graph;
import org.apache.commons.collections15.Factory;

public class MyGraphFactory implements Factory<Graph<INode, IEdge>> {

    private static final MyGraphFactory instance = new MyGraphFactory();

    public static MyGraphFactory getInstance() {
        return instance;
    }

    private MyGraphFactory() {
    }

    @Override
    public Graph<INode, IEdge> create() {
        return new MyGraph();
    }

}
