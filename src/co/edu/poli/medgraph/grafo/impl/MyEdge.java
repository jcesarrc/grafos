package co.edu.poli.medgraph.grafo.impl;

import co.edu.poli.medgraph.grafo.IEdge;

public class MyEdge implements IEdge {

	private final int id;
	private double length;
	private Attribute attribute = Attribute.NOT_VISITED;
	private boolean highligthed = false;

	public MyEdge(int id, double length) {
		this.id = id;
		this.length = length;
	}

        @Override
	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return String.format("%d[%.1f]: %s", id, length, attribute);
	}

        @Override
	public double getLength() {
		return length;
	}

        @Override
	public void setLength(final double length) {
		this.length = length;
	}

        @Override
	public Attribute getAttribute() {
		return attribute;
	}
	
        @Override
	public void setAttribute(final Attribute attribute) {
		this.attribute = attribute;
	}
	
        @Override
	public boolean isHighlighted() {
		return highligthed;
	}
	
        @Override
	public void setHighlighted(boolean highlighted) {
		this.highligthed = highlighted;
	}

}
