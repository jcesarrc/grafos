
package org.uweschmidt.dijkstravis.graph.algorithm;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.set.ListOrderedSet;
import org.uweschmidt.dijkstravis.graph.GraphChangeListener;
import org.uweschmidt.dijkstravis.graph.GraphManager;
import org.uweschmidt.dijkstravis.graph.IEdge;
import org.uweschmidt.dijkstravis.graph.IGraph;
import org.uweschmidt.dijkstravis.graph.INode;


public class DijkstraShortestPath implements GraphAlgorithm<DijkstraStepChanges>, GraphChangeListener {

	/**
	 * <i>Command</i> pattern.
	 */
	private static interface Step {
		public void execute();
		public void unexecute();
		public DijkstraStepChanges getChanges();
	}

	private final Comparator<INode> nodeDistanceComparator = new Comparator<INode>() {
		public int compare(INode node1, INode node2) {
			int cmp = Double.compare(getDistance(node1), getDistance(node2));
			// arbitrary ordering in case of equal distance
			return cmp != 0 ? cmp : 1;
		};
	};

	private final Queue<INode> Q = new PriorityQueue<INode>(11, nodeDistanceComparator);

	private final ListOrderedSet<INode> S = new ListOrderedSet<INode>();

	private final Map<INode, Double> distance = new HashMap<INode, Double>();

	private IGraph graph = null;

	private int step;

	// command at index i (un-)executes step i
	private final List<Step> history = new ArrayList<Step>();

	private final Set<AlgorithmProgressListener<DijkstraStepChanges>> listener = new HashSet<AlgorithmProgressListener<DijkstraStepChanges>>();

	private INode start = null;

	private static final boolean VERBOSIVE = false;

	public DijkstraShortestPath() {
		GraphManager.addGraphChangeListener(this);
	}

	public void graphReplaced(IGraph graph, Transformer<INode, Point2D> layout) {
		setGraph(graph);
	}
	
	public void setGraph(IGraph graph) {
		this.graph = graph;
	}

	public void graphChanged() {
	}

	public void reset() {
		start = null;
		if (graph == null)
			return;
		// set all distances to infinity
		for (final INode v : graph.getNodes()) {
			v.setHighlighted(false);
			setDistance(v, Double.POSITIVE_INFINITY);
			setPredecessorEdge(v, null);
			for (IEdge e : graph.getOutEdges(v)) {
				e.setAttribute(IEdge.Attribute.NOT_VISITED);
				e.setHighlighted(false);
			}
		}
		if (graph.getStart() != null)
			graph.getStart().setAttribute(INode.Attribute.START_NODE);
		updateReset();
	}

	public void initialize(INode... nodes) {
		if (graph == null)
			throw new RuntimeException("Graph not set.");
		step = 0;
		S.clear();
		Q.clear();
		distance.clear();
		history.clear();

		reset();

		// start node
		final INode start = nodes[0];
		setDistance(start, 0d);
		start.setIntermediate(false);
		start.setAttribute(INode.Attribute.START_NODE);
		addToQueue(start);
		this.start = start;

		// calculate number of steps for algorithm and notify listeners
		final Set<INode> visited = new HashSet<INode>();
		visited.add(start);
		updateMaxStep(getNumberOfConnectedNodes(visited, start));
		visited.clear();
		updateCurrentStep(null);
	}
	
	public INode getStart() {
		return start;
	}

	/**
	 * Simple DFS to determine number of nodes reachable from start node
	 */
	private int getNumberOfConnectedNodes(final Set<INode> visited, INode node) {
		int sum = 1;
		for (final IEdge e : graph.getOutEdges(node)) {
			final INode neighbor = graph.getDest(e);
			if (!visited.contains(neighbor)) {
				visited.add(neighbor);
				sum += getNumberOfConnectedNodes(visited, neighbor);
			}
		}
		return sum;
	}

	/**
	 * Precomputes very next step
	 */
	private void computeNextStep() {
		if (step == history.size()) {
			calculateStep();
		}
	}

	private void calculateStep() {

		// next node to expand
		final INode u = getMinimumDistanceNode();

		// store distance changes in step
		final DijkstraStepChanges changes = new DijkstraStepChanges();

		// add min distance node
		changes.setMinimum(u, getDistance(u));

		// relax neighbors of u
		for (final IEdge e : graph.getOutEdges(u)) {
			final INode v = graph.getDest(e);
			// node already has final distance
			if (isSettled(v))
				continue;
			double dv = getDistance(v);
			double du = getDistance(u);
			double uv = e.getLength();
			if (dv > du + uv) {
				changes.setDistance(v, getDistance(v), du + uv);
				changes.setPredecessorEdge(v, v.getPredecessorEdge(), e);
			}
		}

		// create command to execute/unexecute changes in step
		history.add(new Step() {
			public void execute() {
				removeFromQueue(u);
				setSettled(u, true);

				// all found and improved nodes from last step are now just
				// visited
				if (step >= 1) {
					final DijkstraStepChanges lastChanges = history.get(step - 1).getChanges();
					for (final INode v : lastChanges.getChangedNodes()) {
						final IEdge oldEdge = lastChanges.getOldPredecessorEdge(v);
						final IEdge newEdge = lastChanges.getNewPredecessorEdge(v);
						if (v.getAttribute() == INode.Attribute.PATH_FOUND || v.getAttribute() == INode.Attribute.PATH_IMPROVED)
							v.setAttribute(INode.Attribute.VISITED);
						if (oldEdge != null)
							oldEdge.setAttribute(IEdge.Attribute.VISITED);
						newEdge.setAttribute(IEdge.Attribute.ON_SHORTEST_PATH);
					}
				}

				for (final INode v : changes.getChangedNodes()) {
					setDistance(v, changes.getNewDistance(v));
					removeFromQueue(v);
					addToQueue(v);

					// change attribute (found or improved)
					v.setAttribute(changes.getNewAttribute(v));

					// change predecessor edges
					final IEdge oldPredecessorEdge = changes.getOldPredecessorEdge(v);
					final IEdge newPredecessorEdge = changes.getNewPredecessorEdge(v);
					setPredecessorEdge(v, newPredecessorEdge);
					if (oldPredecessorEdge != null)
						oldPredecessorEdge.setAttribute(changes.getNewAttribute(oldPredecessorEdge));
					newPredecessorEdge.setAttribute(changes.getNewAttribute(newPredecessorEdge));
				}

				if (!isFinished()) {
					final INode nextMin = getMinimumDistanceNode();
					changes.setNextMinimum(nextMin, getDistance(nextMin));
					switch (nextMin.getAttribute()) {
						case PATH_FOUND:
							nextMin.setAttribute(INode.Attribute.PATH_FOUND_NEXT_SETTLED);
							break;
						case PATH_IMPROVED:
							nextMin.setAttribute(INode.Attribute.PATH_IMPROVED_NEXT_SETTLED);
							break;
						case VISITED:
							nextMin.setAttribute(INode.Attribute.VISITED_NEXT_SETTLED);
							break;
					}
				} else {
					changes.setNextMinimum(null, 0d);
				}

				step++;
			}

			public void unexecute() {
				// evens out the step++ in execute()
				step--;
				addToQueue(u);
				setSettled(u, false);

				final INode oldMin = changes.getNextMinimum();
				if (oldMin != null && oldMin.getAttribute() != INode.Attribute.START_NODE)
					oldMin.setAttribute(INode.Attribute.VISITED);

				for (final INode v : changes.getChangedNodes()) {
					setDistance(v, changes.getOldDistance(v));
					removeFromQueue(v);
					// distance of infinity means that no path has been found,
					// thus no placement in Q
					if (changes.getOldDistance(v) < Double.POSITIVE_INFINITY)
						addToQueue(v);

					// change attribute (reset to old one)
					v.setAttribute(changes.getOldAttribute(v));

					// change predecessor edges
					final IEdge oldPredecessorEdge = changes.getOldPredecessorEdge(v);
					final IEdge newPredecessorEdge = changes.getNewPredecessorEdge(v);
					setPredecessorEdge(v, oldPredecessorEdge);
					if (oldPredecessorEdge != null)
						oldPredecessorEdge.setAttribute(changes.getOldAttribute(oldPredecessorEdge));
					newPredecessorEdge.setAttribute(changes.getOldAttribute(newPredecessorEdge));
				}

				// all found and improved nodes and edges from last step
				if (step >= 1) {
					final DijkstraStepChanges lastChanges = history.get(step - 1).getChanges();
					for (final INode v : lastChanges.getChangedNodes()) {
						v.setAttribute(lastChanges.getNewAttribute(v));
						final IEdge oldEdge = lastChanges.getOldPredecessorEdge(v);
						final IEdge newEdge = lastChanges.getNewPredecessorEdge(v);
						if (oldEdge != null)
							oldEdge.setAttribute(lastChanges.getNewAttribute(oldEdge));
						newEdge.setAttribute(lastChanges.getNewAttribute(newEdge));
					}
					// reset next minimum
					final INode nextMin = lastChanges.getNextMinimum();
					switch (nextMin.getAttribute()) {
						case PATH_FOUND:
							nextMin.setAttribute(INode.Attribute.PATH_FOUND_NEXT_SETTLED);
							break;
						case PATH_IMPROVED:
							nextMin.setAttribute(INode.Attribute.PATH_IMPROVED_NEXT_SETTLED);
							break;
						case VISITED:
							nextMin.setAttribute(INode.Attribute.VISITED_NEXT_SETTLED);
							break;
					}
				}
			}

			public DijkstraStepChanges getChanges() {
				return changes;
			}
		});
	}
	
	public synchronized void stepForward() {
		stepForward(true);
	}

	private synchronized DijkstraStepChanges stepForward(boolean notifyListener) {
		if (!isFinished()) {
			info("\n### Step %d ###\n", step);
			
			// step not calculated yet (1st step)
			if (step == history.size()) {
				calculateStep();
			}
			final Step currentStep = history.get(step);
			currentStep.execute();
			if (notifyListener)
				updateCurrentStep(currentStep.getChanges());
			// pre-computation
			if (!isFinished())
				computeNextStep();

			info(">>> S = %s\n", S);
			info(">>> Q = %s\n", Q);
			info(">>> d = %s\n", distance);
			return currentStep.getChanges();
		}
		return null;
	}

	public synchronized void stepBackward() {
		if (!isAtTheBeginning()) {
			info("\n### Undo Step %d ###\n", step - 1);
			
			final Step lastStep = history.get(step - 1);
			lastStep.unexecute(); // causes step--
			updateCurrentStep(step >= 1 ? history.get(step - 1).getChanges() : null);

			info(">>> S = %s\n", S);
			info(">>> Q = %s\n", Q);
			info(">>> d = %s\n", distance);
		}
	}

	/**
	 * Runs till the end of the algorithm and only notifies listener when finished.
	 */ 
	public void run() {
		DijkstraStepChanges changes = null;
		while (!isFinished()) {
			changes = stepForward(false);
		}
		updateCurrentStep(changes);
	}

	public boolean isFinished() {
		return Q.isEmpty();
	}

	public boolean isAtTheBeginning() {
		return step == 0;
	}

	// O(1) because it doesn't remove minimum element (just peek())
	private INode getMinimumDistanceNode() {
		info("getMinimumDistanceNode() = %s\n", Q.peek());
		return Q.peek();
	}

	private void setSettled(final INode v, final boolean settled) {
		info("setSettled(%s, %s)\n", v, settled);
		INode last;
		if (settled) {
			last = S.isEmpty() ? null : S.get(S.size() - 1);
			S.add(v);
			if (v.getAttribute() != INode.Attribute.START_NODE)
				v.setAttribute(INode.Attribute.CURRENTLY_SETTLED);
			if (last != null && last.getAttribute() != INode.Attribute.START_NODE) {
				last.setAttribute(INode.Attribute.SETTLED);
			}
		} else {
			S.remove(v);
			last = S.isEmpty() ? null : S.get(S.size() - 1);
			if (v.getAttribute() != INode.Attribute.START_NODE)
				v.setAttribute(INode.Attribute.VISITED);
			if (last != null && last.getAttribute() != INode.Attribute.START_NODE) {
				last.setAttribute(INode.Attribute.CURRENTLY_SETTLED);
			}
		}
	}

	private void setDistance(final INode v, final double dist) {
		info("setDistance(%s) = %.2f\n", v, dist);
		if (dist < Double.POSITIVE_INFINITY) {
		} else {
			v.setAttribute(INode.Attribute.NOT_VISITED);
		}
		v.setDistance(dist);
		distance.put(v, dist);
	}

	// O(1) because of HashSet
	private boolean isSettled(final INode v) {
		info("isSettled(%s) = %s\n", v, S.contains(v));
		return S.contains(v);
	}

	// O(log(n))
	private void addToQueue(final INode v) {
		info("addToQueue(%s)\n", v);
		Q.add(v);
	}

	// O(log(n))
	private void removeFromQueue(final INode v) {
		info("removeFromQueue(%s)\n", v);
		// see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6268068
		// Q.remove(v);
		Q.removeAll(Collections.singletonList(v));
	}

	private double getDistance(final INode v) {
		final Double dist = distance.get(v);
		return dist == null ? Double.POSITIVE_INFINITY : dist;
	}

	private void setPredecessorEdge(final INode v, final IEdge e) {
		info("setPredecessorEdge(%s, %s)\n", v, e);
		v.setPredecessorEdge(e);
	}

	private void info(String fs, Object... args) {
		if (VERBOSIVE)
			System.out.printf(fs, args);
	}

	private void updateCurrentStep(final DijkstraStepChanges changes) {
		for (final AlgorithmProgressListener<DijkstraStepChanges> l : listener) {
			l.stepChanged(step, changes);
		}
	}

	private void updateMaxStep(final int maxSteps) {
		for (final AlgorithmProgressListener<DijkstraStepChanges> l : listener) {
			l.initialized(maxSteps);
		}
	}

	private void updateReset() {
		for (final AlgorithmProgressListener<DijkstraStepChanges> l : listener) {
			l.reset();
		}
	}

	public void addProgressListener(final AlgorithmProgressListener<DijkstraStepChanges> l) {
		listener.add(l);
	}

	public void removeProgressListener(final AlgorithmProgressListener<DijkstraStepChanges> l) {
		listener.remove(l);
	}

}
