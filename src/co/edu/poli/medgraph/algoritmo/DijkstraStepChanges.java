package co.edu.poli.medgraph.algoritmo;

import co.edu.poli.medgraph.grafo.IEdge;
import co.edu.poli.medgraph.grafo.INode;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DijkstraStepChanges {

    private final Map<INode, Double[]> changedDistances = new HashMap<>();
    private final Map<INode, IEdge[]> changedPredecessorEdges = new HashMap<>();
    private final Map<IEdge, IEdge.Attribute[]> changedPredecessorEdgeAttributes = new HashMap<>();
    private INode minimum = null, nextMinimum = null;
    private double minimumDistance, nextMinimumDistance;
    private final Map<INode, INode.Attribute> oldAttributes = new HashMap<>();

    @Override
    public String toString() {
        return String.format("Nodos cambiados: = %s", changedDistances.keySet());
    }

    public void setDistance(INode v, double oldDistance, double newDistance) {
        if (newDistance > oldDistance) {
            System.err.println("distancia nueva > distancia anterior");
        }
        changedDistances.put(v, new Double[]{oldDistance, newDistance});
        oldAttributes.put(v, v.getAttribute());
    }

    public void setPredecessorEdge(INode v, IEdge oldEdge, IEdge newEdge) {
        changedPredecessorEdges.put(v, new IEdge[]{oldEdge, newEdge});
        if (oldEdge != null) {
            changedPredecessorEdgeAttributes.put(oldEdge, new IEdge.Attribute[]{oldEdge.getAttribute(), IEdge.Attribute.REMOVED_FROM_SHORTEST_PATH});
        }
        if (newEdge != null) {
            changedPredecessorEdgeAttributes.put(newEdge, new IEdge.Attribute[]{newEdge.getAttribute(), IEdge.Attribute.ADDED_TO_SHORTEST_PATH});
        }

    }

    public double getOldDistance(INode v) {
        return changedDistances.get(v)[0];
    }

    public double getNewDistance(INode v) {
        return changedDistances.get(v)[1];
    }

    public IEdge getOldPredecessorEdge(INode v) {
        return changedPredecessorEdges.get(v)[0];
    }

    public IEdge getNewPredecessorEdge(INode v) {
        return changedPredecessorEdges.get(v)[1];
    }

    public Set<INode> getChangedNodes() {
        return changedDistances.keySet();
    }

    public void setNextMinimum(INode nextMinimum, double distance) {
        this.nextMinimum = nextMinimum;
        this.nextMinimumDistance = distance;
    }

    public INode getNextMinimum() {
        return nextMinimum;
    }

    public void setMinimum(INode minimum, double distance) {
        this.minimum = minimum;
        this.minimumDistance = distance;
    }

    public INode getMinimum() {
        return minimum;
    }

    public double getMinimumDistance() {
        return minimumDistance;
    }

    public double getNextMinimumDistance() {
        return nextMinimumDistance;
    }

    public INode.Attribute getNewAttribute(INode v) {
        return getOldDistance(v) < Double.POSITIVE_INFINITY ? INode.Attribute.PATH_IMPROVED : INode.Attribute.PATH_FOUND;
    }

    public INode.Attribute getOldAttribute(INode v) {
        return oldAttributes.get(v);
    }

    public IEdge.Attribute getOldAttribute(IEdge e) {
        return changedPredecessorEdgeAttributes.get(e)[0];
    }

    public IEdge.Attribute getNewAttribute(IEdge e) {
        return changedPredecessorEdgeAttributes.get(e)[1];
    }

}
