
package co.edu.poli.medgraph.gui.impl.animation.renderer;

import co.edu.poli.medgraph.grafo.IEdge;
import co.edu.poli.medgraph.grafo.INode;
import co.edu.poli.medgraph.gui.impl.animation.animations.Animation;
import co.edu.poli.medgraph.gui.impl.animation.animations.EdgeLineAnimation;
import co.edu.poli.medgraph.gui.impl.transformer.MyEdgePaintTransformer;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.renderers.BasicRenderer;
import java.awt.Paint;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.List;

/**
 * Ordena las aristas en tres estructuras antes de dibujarlas
 * Primero: se dibujan las aristas que no estan en la ruta evaluada
 * Segundo: se dibujan las que estan en las rutas que quedan igual
 * Tercero: se dibujan las rutas que se estan evaluando
 */
public class MyAnimationRenderer extends BasicRenderer<INode, IEdge> {

	private final List<IEdge> firstEdges, secondEdges, thirdEdges;
	private MyAnimationEdgeRenderer edgeRenderer = null;

	public MyAnimationRenderer() {
		firstEdges = new LinkedList<>();
		secondEdges = new LinkedList<>();
		thirdEdges = new LinkedList<>();
	}

	@Override
	public void render(final RenderContext<INode, IEdge> renderContext, final Layout<INode, IEdge> layout) {
		firstEdges.clear();
		secondEdges.clear();
		thirdEdges.clear();

		if (getEdgeRenderer() instanceof MyAnimationEdgeRenderer)
			edgeRenderer = (MyAnimationEdgeRenderer) getEdgeRenderer();

		for (final IEdge e : layout.getGraph().getEdges()) {

			if (e.isHighlighted()) {
				thirdEdges.add(e);
				continue;
			}
				
			switch (e.getAttribute()) {
				case NOT_VISITED:
				case VISITED:
					firstEdges.add(e);
					break;
				case ON_SHORTEST_PATH:
					secondEdges.add(e);
					break;
				case ADDED_TO_SHORTEST_PATH:
				case REMOVED_FROM_SHORTEST_PATH:
					if (edgeRenderer != null) {
						Animation<IEdge> a = edgeRenderer.getAnimation(e);
						if (a != null) {
							if (a.inProgress()) {
								thirdEdges.add(e);
							} else if (a instanceof EdgeLineAnimation) {
								Paint p = ((EdgeLineAnimation) a).getArrowPaint(null);
								if (p == MyEdgePaintTransformer.UNIMPORTANT_COLOR)
									firstEdges.add(e);
								else
									secondEdges.add(e);
							} else
								secondEdges.add(e);
							break;
						}
					}
					thirdEdges.add(e);
					break;
				default:
					throw new RuntimeException("No existe el atributo " + e.getAttribute());
			}
		}

		renderEdgeList(renderContext, layout, firstEdges);
		renderEdgeList(renderContext, layout, secondEdges);
		renderEdgeList(renderContext, layout, thirdEdges);

		try {
			for (INode v : layout.getGraph().getVertices()) {
				renderVertex(renderContext, layout, v);
				renderVertexLabel(renderContext, layout, v);
			}
		} catch (ConcurrentModificationException cme) {
			renderContext.getScreenDevice().repaint();
		}
	}

	private void renderEdgeList(final RenderContext<INode, IEdge> renderContext, final Layout<INode, IEdge> layout, final List<IEdge> edges) {
		try {
			for (IEdge e : edges) {
				renderEdge(renderContext, layout, e);
				renderEdgeLabel(renderContext, layout, e);
			}
		} catch (ConcurrentModificationException cme) {
			renderContext.getScreenDevice().repaint();
		}
	}

}
