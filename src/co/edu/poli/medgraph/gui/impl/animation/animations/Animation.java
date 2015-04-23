
package co.edu.poli.medgraph.gui.impl.animation.animations;

import co.edu.poli.medgraph.grafo.IEdge;
import co.edu.poli.medgraph.grafo.INode;
import edu.uci.ics.jung.visualization.RenderContext;
import java.awt.Shape;

public abstract class Animation<T> {

	protected float progress = -1f;
	
	public void paint(RenderContext<INode, IEdge> rc, T e, Shape shape) {
		if (!isStarted())
			paintBeforeStarted(rc, e, shape);
		else if (inProgress())
			paintInProgress(rc, e, shape);
		else if (isFinished())
			paintAfterFinished(rc, e, shape);
		else
			throw new RuntimeException("Unexpected");
	}
	
	protected abstract void paintBeforeStarted(RenderContext<INode, IEdge> rc, T e, Shape shape);
	protected abstract void paintInProgress(RenderContext<INode, IEdge> rc, T e, Shape shape);
	protected abstract void paintAfterFinished(RenderContext<INode, IEdge> rc, T e, Shape shape);

	public void setProgress(float progress) {
		this.progress = progress;
	}

	public float getProgress() {
		return progress;
	}

	public boolean isFinished() {
		return progress >= 1f;
	}

	public boolean isStarted() {
		return progress > 0f;
	}

	public boolean inProgress() {
		return isStarted() && !isFinished();
	}

}
