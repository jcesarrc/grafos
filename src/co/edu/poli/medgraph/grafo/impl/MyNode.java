
package co.edu.poli.medgraph.grafo.impl;

import co.edu.poli.medgraph.grafo.IEdge;
import co.edu.poli.medgraph.grafo.INode;

public class MyNode implements INode {

	private final int id;
	private final String ids;
	private String name = null;
	private Attribute attribute = Attribute.NOT_VISITED;
	private double distance = Double.POSITIVE_INFINITY;
	private IEdge predEdge = null;
	private boolean highligthed = false;
	private boolean intermediate = false;

	public MyNode(int id) {
		this.id = id;
		this.ids = String.valueOf(id);
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return ids;
	}

	public Attribute getAttribute() {
		return attribute;
	}

	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}

	public IEdge getPredecessorEdge() {
		return predEdge;
	}

	public void setPredecessorEdge(IEdge edge) {
		this.predEdge = edge;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public boolean isHighlighted() {
		return highligthed;
	}

	public void setHighlighted(boolean highlighted) {
		this.highligthed = highlighted;
	}
	
	public boolean isIntermediate() {
		return intermediate;
	}
	
	public void setIntermediate(boolean intermediate) {
		this.intermediate = intermediate;
	}
}
