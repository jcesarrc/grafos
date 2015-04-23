
package co.edu.poli.medgraph.gui.impl.animation.animations;

import co.edu.poli.medgraph.grafo.IEdge;
import co.edu.poli.medgraph.grafo.INode;
import edu.uci.ics.jung.visualization.RenderContext;
import java.awt.Paint;
import java.awt.Shape;

public class NodeFlashAnimation extends NodeFillAnimation {
	
	private Paint flashPaint1, flashPaint2;
	
	public NodeFlashAnimation(Paint flashPaint1, Paint flashPaint2) {
		this.flashPaint1 = flashPaint1;
		this.flashPaint2 = flashPaint2;
	}

	@Override
	protected void paintAfterFinished(RenderContext<INode, IEdge> rc, INode v, Shape shape) {
		Paint fillPaint = rc.getVertexFillPaintTransformer().transform(v);
		paintNode(rc, v, shape, fillPaint);
	}

	@Override
	protected void paintBeforeStarted(RenderContext<INode, IEdge> rc, INode v, Shape shape) {
		Paint fillPaint = rc.getVertexFillPaintTransformer().transform(v);
		paintNode(rc, v, shape, fillPaint);
	}

	@Override
	protected void paintInProgress(RenderContext<INode, IEdge> rc, INode v, Shape shape) {
		Paint fillPaint = progress <= .5 ? flashPaint1 : flashPaint2;
		paintNode(rc, v, shape, fillPaint);
	}

}
