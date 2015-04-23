package co.edu.poli.medgraph.gui.impl.transformer;

import co.edu.poli.medgraph.algoritmo.DijkstraAlgorithmManager;
import co.edu.poli.medgraph.grafo.GraphChangeListener;
import co.edu.poli.medgraph.grafo.GraphManager;
import co.edu.poli.medgraph.grafo.IEdge;
import co.edu.poli.medgraph.grafo.IGraph;
import co.edu.poli.medgraph.grafo.INode;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.RenderContext;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import org.apache.commons.collections15.Transformer;

public class MyNodeFillPaintTransformer implements Transformer<INode, Paint>, GraphChangeListener {

    public static final Color NOT_VISITED = Color.GRAY;
    public static final Color START_NODE = Color.CYAN;
    public static final Color VISITED = Color.YELLOW;
    public static final Color SETTLED = Color.GREEN;

    public static final Color CURRENTLY_SETTLED = Color.GREEN;
    public static final Color PATH_FOUND = VISITED;
    public static final Color PATH_IMPROVED = VISITED;

    public static final Color EDITOR_COLOR = Color.GREEN;

    private Transformer<INode, Point2D> layout;
    private RenderContext<INode, IEdge> rc;

    public MyNodeFillPaintTransformer(final RenderContext<INode, IEdge> rc) {
        GraphManager.addGraphChangeListener(this);
        this.rc = rc;
    }

    @Override
    public void graphChanged() {
    }

    @Override
    public void graphReplaced(IGraph graph, Transformer<INode, Point2D> layout) {
        this.layout = layout;
    }

    @Override
    public Paint transform(INode node) {
        switch (node.getAttribute()) {
            case START_NODE:
                return START_NODE;
            case SETTLED:
                return SETTLED;
            case NOT_VISITED:
                return DijkstraAlgorithmManager.isAlgoRunning() ? NOT_VISITED : EDITOR_COLOR;
            case VISITED:
                return VISITED;
            case PATH_FOUND:
                return PATH_FOUND;
            case PATH_IMPROVED:
                return PATH_IMPROVED;
            case CURRENTLY_SETTLED:
                return CURRENTLY_SETTLED;
            case PATH_FOUND_NEXT_SETTLED:
            case PATH_IMPROVED_NEXT_SETTLED:
            case VISITED_NEXT_SETTLED:
                return gradientPaint(rc.getMultiLayerTransformer().transform(Layer.LAYOUT, layout.transform(node)), rc.getVertexShapeTransformer().transform(node).getBounds(), VISITED, SETTLED);
        }
        return null;
    }

    public static GradientPaint gradientPaint(Point2D p, Rectangle r, Color one, Color two) {
        r.translate((int) p.getX(), (int) p.getY() + (int) r.getHeight() / 4);
        return new GradientPaint((float) r.getMinX(), (float) r.getMinY(), one, (float) r.getMinX(), ((float) r.getMaxY()), two, false);
    }

}
