/*
 * Copyright (C) 2008, Uwe Schmidt 
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a 
 * copy of this software and associated documentation files (the "Software"), 
 * to deal in the Software without restriction, including without limitation 
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the 
 * Software is furnished to do so, subject to the following conditions: 
 * 
 * The above copyright notice and this permission notice shall be included in 
 * all copies or substantial portions of the Software. 
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL 
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER 
 * DEALINGS IN THE SOFTWARE. 
 */
package org.uweschmidt.dijkstravis.gui.jung.animation;

import java.awt.Paint;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTarget;
import org.jdesktop.animation.timing.TimingTargetAdapter;
import org.jdesktop.animation.timing.Animator.RepeatBehavior;
import org.uweschmidt.dijkstravis.graph.IEdge;
import org.uweschmidt.dijkstravis.graph.INode;
import org.uweschmidt.dijkstravis.graph.algorithm.AlgorithmProgressListener;
import org.uweschmidt.dijkstravis.graph.algorithm.DijkstraAlgorithmManager;
import org.uweschmidt.dijkstravis.graph.algorithm.DijkstraStepChanges;
import org.uweschmidt.dijkstravis.gui.AlgorithmPanel;
import org.uweschmidt.dijkstravis.gui.jung.animation.animations.Animation;
import org.uweschmidt.dijkstravis.gui.jung.animation.animations.EdgeLineAnimation;
import org.uweschmidt.dijkstravis.gui.jung.animation.animations.NodeFlashAnimation;
import org.uweschmidt.dijkstravis.gui.jung.animation.animations.NodeShowAnimation;
import org.uweschmidt.dijkstravis.gui.jung.animation.renderer.MyAnimationEdgeRenderer;
import org.uweschmidt.dijkstravis.gui.jung.animation.renderer.MyAnimationNodeRenderer;
import org.uweschmidt.dijkstravis.gui.jung.transformer.MyNodeFillPaintTransformer;

import edu.uci.ics.jung.visualization.VisualizationViewer;

public class AnimationHandler implements AlgorithmProgressListener<DijkstraStepChanges> {

	private static final int MAX_FPS = 10;

	private boolean enabled = true;

	private VisualizationViewer<INode, IEdge> vv;
	private AlgorithmPanel ap;
	private boolean animationsDone = true;

	private LinkedList<Animator> as = new LinkedList<Animator>();
	private int step = -1;

	private MyAnimationEdgeRenderer edgeRenderer;
	private MyAnimationNodeRenderer nodeRenderer;

	public AnimationHandler(VisualizationViewer<INode, IEdge> vv, AlgorithmPanel ap) {
		setVv(vv);
		this.ap = ap;
		ap.setAnimationHandler(this);
		DijkstraAlgorithmManager.addAlgorithmProgressListener(this);
	}

	private void setVv(VisualizationViewer<INode, IEdge> vv) {
		this.vv = vv;
		if (vv.getRenderer().getEdgeRenderer() instanceof MyAnimationEdgeRenderer)
			edgeRenderer = (MyAnimationEdgeRenderer) vv.getRenderer().getEdgeRenderer();
		else
			setEnabled(false);
		if (vv.getRenderer().getVertexRenderer() instanceof MyAnimationNodeRenderer)
			nodeRenderer = (MyAnimationNodeRenderer) vv.getRenderer().getVertexRenderer();
		else
			setEnabled(false);
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		if (!enabled) {
			stopAnimations();
			vv.repaint();
		}
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void initialized(int maxSteps) {
		stopAnimations();
		vv.repaint();
	}

	public void reset() {
		stopAnimations();
		vv.repaint();
	}

	public void updateDelay() {
		for (Animator a : as) {
			if (!a.isRunning() && a.getDuration() != Animator.INFINITE)
				a.setDuration(ap.getDelay());
		}
	}

	public boolean animationsRunning() {
		return !animationsDone;
	}

	public void pauseAnimations(boolean pause) {
		for (Animator a : as) {
			if (pause)
				a.pause();
			else
				a.resume();
		}
	}

	private void stopAnimations() {
		for (Animator a : as) {
			// cancel doesn't cause "end" to be called on timingtargets
			a.cancel();
		}
		as.clear();
		nodeRenderer.clearAnimations();
		edgeRenderer.clearAnimations();
		animationsDone = true;
	}

	public void stepChanged(int step, final DijkstraStepChanges changes) {

		stopAnimations();

		if (isEnabled() && step > this.step && changes != null) {

			final int delay = ap.getDelay();

			final INode nextMin = changes.getNextMinimum();

			// animations that need to be started directly
			List<Animator> startAnims = new LinkedList<Animator>();

			Animator a = null, b = null;
			for (final INode v : changes.getChangedNodes()) {
				final IEdge newE = changes.getNewPredecessorEdge(v);
				final IEdge oldE = changes.getOldPredecessorEdge(v);

				// get last steps color for node
				INode.Attribute attr = v.getAttribute();
				v.setAttribute(changes.getOldAttribute(v));
				final Paint oldPaint = vv.getRenderContext().getVertexFillPaintTransformer().transform(v);
				v.setAttribute(attr);

				if (oldE != null) {
					b = addAnimation(a, delay, new NodeAndEdgeAnimator<IEdge>(new EdgeLineAnimation(v, true), oldE));
					if (a == null)
						startAnims.add(b);
					a = b;
				}

				b = addAnimation(a, delay, new NodeAndEdgeAnimator<IEdge>(new EdgeLineAnimation(v, false), newE));
				if (a == null)
					startAnims.add(b);
				a = b;

				b = addAnimation(null, Animator.INFINITE, new NodeAndEdgeAnimator<INode>(new NodeShowAnimation(oldPaint), v));
				a.addTarget(new AnimatorTrigger(b, AnimatorTrigger.Action.STOP_AT_END));
				startAnims.add(b);
				a = b;
			}

			for (Animator anim : startAnims) {
				anim.start();
			}

			if (nextMin != null) {
				Animator last = as.isEmpty() ? null : as.getLast();
				TimingTarget tt = new TimingTargetAdapter() {
					@Override
					public void end() {
						Animator a = addAnimation(null, 300, new NodeAndEdgeAnimator<INode>(new NodeFlashAnimation(MyNodeFillPaintTransformer.VISITED, MyNodeFillPaintTransformer.SETTLED), nextMin));
						a.addTarget(new TimingTargetAdapter() {
							@Override
							public void end() {
								animationsDone = true;
							}
						});
						a.setStartDelay(Math.min(500, ap.getDelay()/2));
						a.setRepeatBehavior(RepeatBehavior.LOOP);
						a.setRepeatCount(Math.max(1, ap.getDelay() / 250));
						a.start();
					}
				};
				if (last != null)
					last.addTarget(tt);
				else
					tt.end();
			}

			if (!as.isEmpty() || nextMin != null)
				animationsDone = false;

		}

		vv.repaint();
		this.step = step;
	}

	/**
	 * Creates a new {@link Animation} that is started when the trigger animation (if given) has ended.
	 */
	private Animator addAnimation(Animator trigger, int duration, TimingTarget t) {
		Animator a = new Animator(duration, t);
		a.setResolution(1000 / MAX_FPS);
		as.add(a);
		if (trigger != null) {
			trigger.addTarget(new AnimatorTrigger(a, AnimatorTrigger.Action.START_AT_END));
		}
		return a;
	}

	private class NodeAndEdgeAnimator<T> extends TimingTargetAdapter {
		private List<Animation<T>> anims = new LinkedList<Animation<T>>();

		public NodeAndEdgeAnimator(Animation<T> a, T e) {
			this(a, Collections.singletonList(e));
		}

		@SuppressWarnings("unchecked")
		public NodeAndEdgeAnimator(Animation<T> a, List<T> elements) {
			for (T e : elements) {
				anims.add(a);
				if (e instanceof INode)
					nodeRenderer.setAnimation((INode) e, (Animation<INode>) a);
				if (e instanceof IEdge)
					edgeRenderer.setAnimation((IEdge) e, (Animation<IEdge>) a);
			}
		}

		@Override
		public void timingEvent(float fraction) {
			for (Animation<T> a : anims)
				a.setProgress(fraction);
			vv.repaint();
		}

		@Override
		public void end() {
			for (Animation<T> a : anims)
				a.setProgress(1);
		}

	}

	private static class AnimatorTrigger extends TimingTargetAdapter {
		enum Action {
			START_AT_BEGINNING, STOP_AT_BEGINNING, START_AT_END, STOP_AT_END
		};

		private final Animator a;
		private final Action action;

		public AnimatorTrigger(Animator a, Action action) {
			this.a = a;
			this.action = action;
		}

		@Override
		public void begin() {
			switch (action) {
				case START_AT_BEGINNING:
					a.start();
					break;
				case STOP_AT_BEGINNING:
					a.stop();
					break;
			}
		}

		@Override
		public void end() {
			switch (action) {
				case START_AT_END:
					a.start();
					break;
				case STOP_AT_END:
					a.stop();
					break;
			}
		}
	}

}
