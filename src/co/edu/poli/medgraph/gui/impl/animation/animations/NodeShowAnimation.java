
package co.edu.poli.medgraph.gui.impl.animation.animations;

import co.edu.poli.medgraph.grafo.IEdge;
import co.edu.poli.medgraph.grafo.INode;
import edu.uci.ics.jung.visualization.RenderContext;
import java.awt.Paint;
import java.awt.Shape;

public class NodeShowAnimation extends NodeFillAnimation {
	
	private Paint beforePaint;
	
	public NodeShowAnimation(Paint beforePaint) {
		this.beforePaint = beforePaint;
	}

	@Override
	protected void paintAfterFinished(RenderContext<INode, IEdge> rc, INode v, Shape shape) {
		Paint fillPaint = rc.getVertexFillPaintTransformer().transform(v);
		paintNode(rc, v, shape, fillPaint);
	}

	@Override
	protected void paintBeforeStarted(RenderContext<INode, IEdge> rc, INode v, Shape shape) {
		paintNode(rc, v, shape, beforePaint);
	}

	@Override
	protected void paintInProgress(RenderContext<INode, IEdge> rc, INode v, Shape shape) {
		Paint fillPaint = rc.getVertexFillPaintTransformer().transform(v);
		paintNode(rc, v, shape, !isFinished() ? beforePaint : fillPaint);
	}
	


}
