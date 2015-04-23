package co.edu.poli.medgraph.grafo;

public interface IEdge {

    public enum Attribute {

        NOT_VISITED, VISITED, ON_SHORTEST_PATH,
        ADDED_TO_SHORTEST_PATH, REMOVED_FROM_SHORTEST_PATH
    }

    public int getId();

    public void setHighlighted(boolean highlighted);

    public boolean isHighlighted();

    public void setAttribute(Attribute attribute);

    public Attribute getAttribute();

    public double getLength();

    public void setLength(double length);

}
