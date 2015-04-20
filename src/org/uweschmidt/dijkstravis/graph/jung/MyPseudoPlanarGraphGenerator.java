
package org.uweschmidt.dijkstravis.graph.jung;

import java.util.Random;

import org.apache.commons.collections15.Factory;

import edu.uci.ics.jung.algorithms.generators.GraphGenerator;
import edu.uci.ics.jung.graph.Graph;

public class MyPseudoPlanarGraphGenerator<V, E> implements GraphGenerator<V, E> {

	private final Factory<Graph<V, E>> graphFactory;
	private final Factory<V> nodeFactory;
	private final Factory<E> edgeFactory;
	private Random rand = new Random();

	public MyPseudoPlanarGraphGenerator(Factory<Graph<V, E>> graphFactory, Factory<V> nodeFactory, Factory<E> edgeFactory) {
		this.graphFactory = graphFactory;
		this.nodeFactory = nodeFactory;
		this.edgeFactory = edgeFactory;
	}

	private boolean p(final double p) {
		return rand.nextDouble() < p;
	}

	public Graph<V, E> generateGraph() {
		return generateGraph(10, 10);
	}

	public Graph<V, E> generateGraph(final int w, final int h) {
		return generateGraph(w, h, .9, .2);
	}

	/**
	 * Creates a "grid of nodes" from given width and height. Neighbored nodes are
	 * connected with the given probability. Additional edes are created if aNr > 0.
	 * Additional edges connect random nodes (decreasing probability with increasing
	 * distance in grid). 
	 * @param w Width of grid
	 * @param h Height of grid
	 * @param pN Probability that neighbored nodes are connected by an edge.
	 * @param aNr Additional edges factor (multiplied with the number of nodes)
	 * @return The generated graph.
	 */
	@SuppressWarnings("unchecked")
	public Graph<V, E> generateGraph(final int w, final int h, double pN, double aNr) {

		final Graph<V, E> graph = graphFactory.create();
		final Object[][] nodes = new Object[h][w];

		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				final V node = nodeFactory.create();
				nodes[i][j] = node;
				graph.addVertex(node);
			}
		}

		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				final V v = (V) nodes[i][j];
				try {
					if (i+1 < h && p(pN))
						graph.addEdge(edgeFactory.create(), v, (V) nodes[i + 1][j + 0]);
					if (i-1 >= 0 && p(pN))
						graph.addEdge(edgeFactory.create(), v, (V) nodes[i - 1][j + 0]);
					if (j+1 < w && p(pN))
						graph.addEdge(edgeFactory.create(), v, (V) nodes[i + 0][j + 1]);
					if (j-1 >= 0 && p(pN))
						graph.addEdge(edgeFactory.create(), v, (V) nodes[i + 0][j - 1]);

					if (i+1 < h && j-1 >= 0 && p(pN))
						graph.addEdge(edgeFactory.create(), v, (V) nodes[i + 1][j - 1]);
					if (i-1 >= 0 && j+1 < w && p(pN))
						graph.addEdge(edgeFactory.create(), v, (V) nodes[i - 1][j + 1]);
					if (i+1 < h && j+1 < w && p(pN))
						graph.addEdge(edgeFactory.create(), v, (V) nodes[i + 1][j + 1]);
					if (i-1 >= 0 && j-1 >= 0 && p(pN))
						graph.addEdge(edgeFactory.create(), v, (V) nodes[i - 1][j - 1]);
				} catch (ArrayIndexOutOfBoundsException e) {
					e.printStackTrace();
				}
			}
		}

		// edges to create
		int edgesLeft = (int) Math.round(aNr * w * h);

		while (edgesLeft > 0) {
			int y1, x1, y2, x2;
			final V v1 = (V) nodes[y1 = rand.nextInt(h)][x1 = rand.nextInt(w)];
			final V v2 = (V) nodes[y2 = rand.nextInt(h)][x2 = rand.nextInt(w)];
			final double distance = Math.sqrt(Math.pow(Math.abs(x1 - x2), 2) + Math.pow(Math.abs(y1 - y2), 2));

			if (distance > 0 && rand.nextDouble() < 1 / Math.pow(distance, 2)) {

				// if edge has been added
				if (graph.addEdge(edgeFactory.create(), v1, v2)) {
					edgesLeft--;
				}
			}
		}

		return graph;
	}

}
