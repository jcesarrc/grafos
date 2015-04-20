
package org.uweschmidt.dijkstravis.graph.jung;

import org.uweschmidt.dijkstravis.graph.IEdge;

public class MyEdge implements IEdge {

	private final int id;
	private double length;
	private Attribute attribute = Attribute.NOT_VISITED;
	private boolean highligthed = false;

	public MyEdge(int id, double length) {
		this.id = id;
		this.length = length;
	}

	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return String.format("%d[%.1f]: %s", id, length, attribute);
	}

	public double getLength() {
		return length;
	}

	public void setLength(final double length) {
		this.length = length;
	}

	public Attribute getAttribute() {
		return attribute;
	}
	
	public void setAttribute(final Attribute attribute) {
		this.attribute = attribute;
	}
	
	public boolean isHighlighted() {
		return highligthed;
	}
	
	public void setHighlighted(boolean highlighted) {
		this.highligthed = highlighted;
	}

}
