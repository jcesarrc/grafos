
package co.edu.poli.medgraph.gui.impl.animation.animations;

import co.edu.poli.medgraph.grafo.IEdge;
import co.edu.poli.medgraph.grafo.INode;
import co.edu.poli.medgraph.gui.impl.transformer.MyEdgePaintTransformer;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;

public class EdgeLineAnimation extends Animation<IEdge> {

	private final INode target;
	private final boolean reverse;
	private double leftToDraw = Double.POSITIVE_INFINITY;
	private double targetRadius = 0;

	public EdgeLineAnimation(INode target, boolean reverse) {
		this.target = target;
		this.reverse = reverse;
	}
	
	public Paint getArrowPaint(Paint regular) {
		if (!isStarted() && reverse) return regular;
		if (!isStarted() && !reverse) return MyEdgePaintTransformer.UNIMPORTANT_COLOR;
		if (isFinished() && reverse) return MyEdgePaintTransformer.UNIMPORTANT_COLOR;
		if (isFinished() && !reverse) return regular;
		if (inProgress() && reverse) return MyEdgePaintTransformer.UNIMPORTANT_COLOR;
		if (inProgress() && !reverse) return leftToDraw <= targetRadius+5 ? regular : MyEdgePaintTransformer.UNIMPORTANT_COLOR;
		return regular;
	}

	@Override
	protected void paintAfterFinished(RenderContext<INode, IEdge> rc, IEdge e, Shape shape) {
		standardPaint(rc, e, shape);
	}

	@Override
	protected void paintBeforeStarted(RenderContext<INode, IEdge> rc, IEdge e, Shape shape) {
		standardPaint(rc, e, shape);
	}
	
	private void standardPaint(RenderContext<INode, IEdge> rc, IEdge e, Shape shape) {
		GraphicsDecorator g = rc.getGraphicsContext();
		final Paint fill_paint = rc.getEdgeFillPaintTransformer().transform(e);
		final Paint draw_paint = rc.getEdgeDrawPaintTransformer().transform(e);		
		if (fill_paint != null) {
			g.setPaint(getArrowPaint(fill_paint));
			g.fill(shape);
		}
		if (draw_paint != null) {
			g.setPaint(getArrowPaint(draw_paint));
			g.draw(shape);
		}
	}

	@Override
	protected void paintInProgress(RenderContext<INode, IEdge> rc, IEdge e, Shape shape) {

		float progress = reverse ? 1 - this.progress : this.progress;

		GraphicsDecorator g = rc.getGraphicsContext();
		final Paint fill_paint = rc.getEdgeFillPaintTransformer().transform(e);
		final Paint draw_paint = rc.getEdgeDrawPaintTransformer().transform(e);
		
		if (fill_paint != null) {
			g.setPaint(fill_paint);
			g.fill(shape);
		}

		if (draw_paint == null) return;

		g.setPaint(MyEdgePaintTransformer.UNIMPORTANT_COLOR);
		g.draw(shape);
		g.setPaint(draw_paint);

		float[] coords = new float[6];
		PathIterator iter = shape.getPathIterator(null);
		iter.currentSegment(coords); iter.next();
		Point2D.Float p1 = new Point2D.Float(coords[0], coords[1]);		
		iter.currentSegment(coords);
		Point2D.Float p2 = new Point2D.Float(coords[0], coords[1]);
		
		targetRadius = rc.getVertexShapeTransformer().transform(target).getBounds().getWidth() / 2;		
		Point2D.Float end = new Point2D.Float(Math.round(p1.getX() + (p2.getX() - p1.getX()) * progress), Math.round(p1.getY() + (p2.getY() - p1.getY()) * progress));
		leftToDraw = p2.distance(end);
			
		g.draw(new Line2D.Float(p1, end));
	}

}
