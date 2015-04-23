package co.edu.poli.medgraph.grafo;

public interface INode {

    public enum Attribute {

        START_NODE, VISITED, NOT_VISITED, SETTLED,
        CURRENTLY_SETTLED, VISITED_NEXT_SETTLED,
        PATH_FOUND_NEXT_SETTLED, PATH_IMPROVED_NEXT_SETTLED,
        PATH_FOUND, PATH_IMPROVED
    }

    public int getId();

    public void setName(String name);

    public String getName();

    public void setHighlighted(boolean highlighted);

    public boolean isHighlighted();

    public void setAttribute(Attribute attribute);

    public Attribute getAttribute();

    public void setPredecessorEdge(IEdge edge);

    public IEdge getPredecessorEdge();

    public void setDistance(double distance);

    public double getDistance();

    public void setIntermediate(boolean intermediate);

    public boolean isIntermediate();
}
