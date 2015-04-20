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
package co.edu.poli.medgraph.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.collections15.Transformer;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTargetAdapter;
import org.jdesktop.animation.timing.Animator.Direction;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.Task;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTitledSeparator;
import org.uweschmidt.dijkstravis.graph.GraphChangeListener;
import org.uweschmidt.dijkstravis.graph.GraphManager;
import org.uweschmidt.dijkstravis.graph.IGraph;
import org.uweschmidt.dijkstravis.graph.INode;
import org.uweschmidt.dijkstravis.graph.algorithm.AlgorithmProgressListener;
import org.uweschmidt.dijkstravis.graph.algorithm.DijkstraAlgorithmManager;
import org.uweschmidt.dijkstravis.graph.algorithm.DijkstraShortestPath;
import org.uweschmidt.dijkstravis.graph.algorithm.DijkstraStepChanges;
import co.edu.poli.medgraph.gui.animation.AnimationHandler;
import co.edu.poli.medgraph.gui.transformer.MyEdgeArrowTransformer;
import co.edu.poli.medgraph.gui.transformer.MyEdgePaintTransformer;
import co.edu.poli.medgraph.gui.transformer.MyEdgeStrokeTransformer;
import co.edu.poli.medgraph.gui.transformer.MyNodeFillPaintTransformer;
import co.edu.poli.medgraph.gui.transformer.MyNodeShapeTransformer;
import co.edu.poli.medgraph.gui.language.LocaleChangeListener;
import co.edu.poli.medgraph.gui.language.LocaleManager;
import co.edu.poli.medgraph.util.SC;


@SuppressWarnings("serial")
public class AlgorithmPanel extends JPanel implements AlgorithmProgressListener<DijkstraStepChanges>, GraphChangeListener, LocaleChangeListener {

	private static final boolean DEBUG = false;
	private static final float DISABLED_ALPHA = .05f;
	
	private final DijkstraShortestPath algo;
	private IGraph graph;
	private final JProgressBar progressBar;
	private final JSlider delaySlider;
	private final ResourceMap r;
	private RunTask runTask = null;
	private final JButton playPauseButton, startStopButton;
	private boolean first = true;
	private final JXPanel migPanel;
	
	private AnimationHandler ah = null;
	
	private int maxSteps = 0;
	private int currentStep = -1;
	private DijkstraStepChanges currentChanges = null;
	private JCheckBox enableAnims;

	@Resource
	private Icon playIcon, pauseIcon;

	private JTextArea stepInfo = null;
	private boolean canStepForward, canStepBackward, startable, stoppable;

	private final JLabel minDelayLabel, maxDelayLabel;

	public AlgorithmPanel() {
		super();
		r = SC.getResourceMap(AlgorithmPanel.class);
		algo = new DijkstraShortestPath();

		LocaleManager.addLocaleChangeListener(this);
		GraphManager.addGraphChangeListener(this);
		DijkstraAlgorithmManager.addAlgorithmProgressListener(this);
		DijkstraAlgorithmManager.setAlgorithm(algo);

		setLayout(new BorderLayout());

		final MigLayout migLayout = new MigLayout((DEBUG ? "debug, " : "") + "insets 0 10 10 10", "[left, center]");
		migPanel = new JXPanel();
		migPanel.setLayout(migLayout);		
		migPanel.setAlpha(DISABLED_ALPHA);
		add(migPanel, BorderLayout.CENTER);
		
		add(startStopButton = new JButton(SC.getAction(this, "startStopAlgo")), BorderLayout.SOUTH);
		addPropertyChangeListener("stoppable", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (Boolean.TRUE.equals(evt.getNewValue()))
					startStopButton.setText(r.getString("stopAlgorithm"));
				else
					startStopButton.setText(r.getString("startAlgorithm"));
			}
		});
		
		/*
		 * LEGEND
		 */		
		migPanel.add(SC.newComponent(JXTitledSeparator.class, "legend"), "span, growx");
		final JPanel legendPanel = new JPanel(new MigLayout((DEBUG ? "debug, " : "") + "insets 0", "[left, center, left]"));
		migPanel.add(legendPanel, "span");
		
		legendPanel.add(createLegendLabel("startNode", getNodeIcon(MyNodeFillPaintTransformer.START_NODE)));
		legendPanel.add(new JSeparator(SwingConstants.VERTICAL), "growy, spany 5");
		legendPanel.add(createLegendLabel("defaultEdge", getEdgeImage(MyEdgeStrokeTransformer.NORMAL_STROKE, MyEdgePaintTransformer.DEFAULT)), "wrap");
		legendPanel.add(createLegendLabel("notVisited", getNodeIcon(MyNodeFillPaintTransformer.NOT_VISITED)), "");
		legendPanel.add(createLegendLabel("thickEdge", getEdgeImage(MyEdgeStrokeTransformer.THICK_STROKE, MyEdgePaintTransformer.DEFAULT)), "wrap");
		legendPanel.add(createLegendLabel("visited", getNodeIcon(MyNodeFillPaintTransformer.VISITED)), "");
		legendPanel.add(createLegendLabel("unimportantEdge", getEdgeImage(MyEdgeStrokeTransformer.NORMAL_STROKE, MyEdgePaintTransformer.UNIMPORTANT_COLOR)), "wrap");
		legendPanel.add(createLegendLabel("nextSettled", getNodeIcon(MyNodeFillPaintTransformer.gradientPaint(new Point2D.Double(-MyNodeShapeTransformer.DEFAULT.getX(), -MyNodeShapeTransformer.DEFAULT.getY()), MyNodeShapeTransformer.DEFAULT.getBounds(), MyNodeFillPaintTransformer.VISITED, MyNodeFillPaintTransformer.SETTLED))), "");
		legendPanel.add(createLegendLabel("addedEdge", getEdgeImage(MyEdgeStrokeTransformer.NORMAL_STROKE, MyEdgePaintTransformer.SHORTEST_PATH_ADDED)), "wrap");		
		legendPanel.add(createLegendLabel("settled", getNodeIcon(MyNodeFillPaintTransformer.SETTLED)), "");
		legendPanel.add(createLegendLabel("removedEdge", getEdgeImage(MyEdgeStrokeTransformer.NORMAL_STROKE, MyEdgePaintTransformer.SHORTEST_PATH_REMOVED)), "wrap");		
		

		migPanel.add(SC.newComponent(JXTitledSeparator.class, "stepInfo"), "span, growx");
		migPanel.add(new JScrollPane(stepInfo = SC.newComponent(JTextArea.class, "stepInfoArea")), "span, w 280, grow, push");

		/*
		 * DELAY SLIDER
		 */
		migPanel.add(SC.newComponent(JXTitledSeparator.class, "execSpeed"), "span, growx");
		migPanel.add(SC.newComponent(JLabel.class, "timeIconLabel"), "left");
		migPanel.add(delaySlider = SC.newComponent(JSlider.class, "delaySlider"), "pushx, growx, wrap");
		final Hashtable<Integer, JLabel> labels = new Hashtable<Integer, JLabel>();
		labels.put(r.getInteger("delaySlider.minimum"), minDelayLabel = new JLabel());
		labels.put(r.getInteger("delaySlider.maximum"), maxDelayLabel = new JLabel());
		delaySlider.setLabelTable(labels);
		delaySlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				StatusBar.getInstance().setText(r.getString("delayHelp", delaySlider.getValue() / 1000d));
				if (ah != null && ah.isEnabled() && !delaySlider.getValueIsAdjusting())
					ah.updateDelay();
			}
		});
		delaySlider.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				StatusBar.getInstance().setText(StatusBar.EMPTY_TEXT);
			}
		});
		delaySlider.addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				delaySlider.setValue(delaySlider.getValue() + e.getUnitsToScroll() * 3);
			}
		});

		
		/*
		 * PROGRESS BAR
		 */
		migPanel.add(SC.newComponent(JXTitledSeparator.class, "progress"), "span, growx");
		migPanel.add(SC.newComponent(JLabel.class, "progressIconLabel"), "left");
		migPanel.add(progressBar = SC.newComponent(JProgressBar.class, "progressBar"), "pushx, growx, wrap");
		migPanel.add(enableAnims = SC.newComponent(JCheckBox.class, "enableAnimations"), "skip, left, wrap");
		enableAnims.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ah.setEnabled(enableAnims.isSelected());
			}
		});

		/*
		 * ALGO CONTROL BUTTONS
		 */
		migPanel.add(SC.newComponent(JXTitledSeparator.class, "controls"), "span, growx");
//		migPanel.add(createButton("stop"), "split, span, gap 5");
		migPanel.add(playPauseButton = createControlButton("playPause"), "split, span, gap 5");
		migPanel.add(createControlButton("stepFirst"), "gap 5, gapleft 20");
		migPanel.add(createControlButton("stepBackward"), "gap 5");
		migPanel.add(createControlButton("stepForward"), "gap 5");
		migPanel.add(createControlButton("stepLast"), "gap 5, wrap");
		// add key bindings
		migPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "leftKey");
		migPanel.getActionMap().put("leftKey", SC.getAction(this, "stepBackward"));
		migPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "rightKey");
		migPanel.getActionMap().put("rightKey", SC.getAction(this, "stepForward"));

		localeChanged();
		clear();
	}
	
	private JButton createControlButton(String name) {
		final JButton button = SC.createActionItem(JButton.class, name, this);
		button.setMaximumSize(new Dimension(50, 50));
		button.addMouseListener(StatusBar.getInstance().new MouseListener(button));
		return button;
	}

	private JLabel createLegendLabel(String name, Icon icon) {
		JLabel l = SC.newComponent(JLabel.class, name);
		l.setIcon(icon);
		l.setFont(l.getFont().deriveFont(11f));
		return l;
	}
	
	private ImageIcon getNodeIcon(Paint c) {
		Shape s = MyNodeShapeTransformer.DEFAULT;
		int w = (int)s.getBounds().getWidth()+1;
		int h = (int)s.getBounds().getHeight()+1;
		BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		s = AffineTransform.getTranslateInstance(-s.getBounds().getX(), -s.getBounds().getY()).createTransformedShape(s);

		g.setPaint(c);
		g.fill(s);
		g.setPaint(Color.black);
		g.draw(s);
		
		return new ImageIcon(img);
	}
	
	private ImageIcon getEdgeImage(Stroke s, Paint c) {
		int w = 21;
		int h = 21;
		BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g.setPaint(c);
		g.setStroke(s);
		g.draw(new Line2D.Float(0, h/2, w-10, h/2));
		GeneralPath arrow = new GeneralPath(MyEdgeArrowTransformer.DEFAULT_ARROW);
		Shape shape = arrow.createTransformedShape(AffineTransform.getTranslateInstance(w-3, h/2));
		g.fill(shape);
		g.draw(shape);
		return new ImageIcon(img);
	}
	
	@Override
	protected void paintBorder(Graphics g) {
		super.paintBorder(g);
		if (!isStoppable()) {
			Color old = g.getColor();
			((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			String s1 = SC.t("acp_algo_start1");
			String s2 = SC.t("acp_algo_start2");
			final Rectangle2D rect1 = g.getFontMetrics().getStringBounds(s1, g);
			final Rectangle2D rect2 = g.getFontMetrics().getStringBounds(s2, g);
			
			g.setColor(new Color(0,0,0,150));
			int w = (int)Math.max(rect1.getWidth(), rect2.getWidth()) + 20;
			int h = 50;
			int x = getWidth()/2 - w/2;
			int y = getHeight()/2 - h/2;
			g.fillRoundRect(x, y, w, h, 10, 10);
			
			g.setColor(Color.WHITE);
			g.drawString(s1, x+10, y + 20);
			g.drawString(s2, x+10, y + 40);
			
			g.setColor(old);
		}
	}

	public void localeChanged() {
		setBorder(BorderFactory.createTitledBorder(r.getString("mainPanel")));
		startStopButton.setText(isStoppable() ? r.getString("stopAlgorithm") : r.getString("startAlgorithm"));
		maxDelayLabel.setText(r.getString("slower"));
		minDelayLabel.setText(r.getString("faster"));
		delaySlider.updateUI();

		r.injectComponents(this);
		r.injectFields(this);

		updateProgressBar();
		
		if (currentStep >= 0)
			stepChanged(currentStep, currentChanges);

		if (first) {
			delaySlider.setValue(1000);
			first = false;
		}
	}
	
	public int getDelay() {
		return delaySlider.getValue();
	}

	private void clear() {
		migPanel.setAlpha(DISABLED_ALPHA);
		currentStep = -1;
		currentChanges = null;
		updateProgressBar();
		progressBar.setString("");
		stepInfo.setText(null);
		playPauseButton.setIcon(playIcon);
		setCanStepBackward(false);
		setCanStepForward(false);
	}

	public void graphReplaced(IGraph graph, Transformer<INode, Point2D> layout) {
		this.graph = graph;
		stop();
		graphChanged();
	}
	
	public void graphChanged() {
		setStartable(graph.getNumberOfNodes() > 0);
	}
	
	public void setAnimationHandler(AnimationHandler ah) {
		this.ah = ah;
		enableAnims.setSelected(ah.isEnabled());
	}

	private class RunTask extends Task<Void, Void> {

		private boolean paused = false;

		public RunTask() {
			super(Application.getInstance());
			init();
		}

		private void init() {
			INode start = graph.getStart();
			if (start == null)
				start = graph.getNodes().iterator().next();
			algo.initialize(start);
			setPaused(true);
		}

		public void setPaused(boolean state) {
			this.paused = state;
			if (ah.isEnabled()) ah.pauseAnimations(state);
			playPauseButton.setIcon(state ? playIcon : pauseIcon);
		}

		public boolean isPaused() {
			return paused;
		}

		private void sleepWhilePaused() throws Exception {
			while (isPaused()) {
				Thread.sleep(100L);
			}
		}
		
		private void sleepWhileAnimationsRunning() throws Exception {
			while (ah.animationsRunning()) {
				Thread.sleep(100L);
			}
		}

		@Override
		protected Void doInBackground() throws Exception {
			while (true) {
				sleepWhilePaused();
				while (!algo.isFinished()) {
					sleepWhilePaused();
					if (ah.isEnabled())
						sleepWhileAnimationsRunning();
					
					if (!isPaused() && !ah.animationsRunning())
						algo.stepForward();
					
					if (!ah.isEnabled() && !algo.isFinished())
						Thread.sleep(delaySlider.getValue());
				}
				setPaused(true);
			}
		}
	}
	
	@Action(enabledProperty = "startable")
	public synchronized void startStopAlgo() {
		// disable button while animation is running
		setStartable(false);
		Animator a = new Animator(150, new TimingTargetAdapter() {
			@Override
			public void timingEvent(float fraction) {
				migPanel.setAlpha(fraction > DISABLED_ALPHA ? fraction : DISABLED_ALPHA);
			}

			@Override
			public void end() {
				if (isStoppable()) {
					stop();
				} else {
					SC.getAction(AlgorithmPanel.this, "playPause").actionPerformed(new ActionEvent(AlgorithmPanel.this, 0, null));
				}
				setStartable(true);
			}
		});
		a.setStartDirection(isStoppable() ? Direction.BACKWARD : Direction.FORWARD);
		a.setStartFraction(isStoppable() ? 1f : 0f);
		a.start();
	}

	@Action(enabledProperty = "stoppable")
	public void stop() {
		if (runTask != null)
			runTask.cancel(true);
		algo.reset();
		setStoppable(false);		
		clear();
	}

	@Action(enabledProperty = "stoppable")
	public Task<Void, Void> playPause() {
		setStoppable(true);

		// start again if finished
		if (currentStep == maxSteps)
			stepFirst();

		if (runTask != null && runTask.isStarted()) {
			runTask.setPaused(!runTask.isPaused());
			return null;
		}

		return (runTask = new RunTask());
	}

	@Action(enabledProperty = "canStepForward")
	public synchronized void stepForward() {
		algo.stepForward();
	}

	@Action(enabledProperty = "canStepBackward")
	public synchronized void stepBackward() {
		algo.stepBackward();
	}
	
	@Action(enabledProperty = "canStepBackward")
	public void stepFirst() {
		algo.initialize(algo.getStart());
	}
	
	@Action(enabledProperty = "canStepForward")
	public void stepLast() {
		algo.run();
	}


	// LISTENER METHODS

	private void updateProgressBar() {
		progressBar.setValue(currentStep == -1 ? 0 : currentStep);

		if (currentStep >= 0) {
			if (currentStep == 0)
				progressBar.setString(r.getString("initialized"));
			else if (currentStep == maxSteps)
				progressBar.setString(r.getString("finished"));
			else
				progressBar.setString(r.getString("stepMessage", currentStep, maxSteps));
		}
	}
	
	public void stepChanged(final int step, final DijkstraStepChanges changes) {
		this.currentStep = step;
		this.currentChanges = changes;

		updateProgressBar();

		setCanStepBackward(step > 0);
		setCanStepForward(step < maxSteps);

		stepInfo.setText(null);
		
		if (step == 0) {			
			stepInfo.append(String.format(SC.t("algo_init_FS"), algo.getStart()));			
		} else if (step < maxSteps) {
			
			int found = 0;
			int improved = 0;
			for (INode v : changes.getChangedNodes()) {
				if (changes.getOldDistance(v) < Double.POSITIVE_INFINITY)
					improved++;
				else
					found ++;
			}

			stepInfo.append(String.format(SC.t("expanding_node_FS"), changes.getMinimum()));
			if (found > 0 && improved == 0)
				stepInfo.append(String.format(" - %d "+(found == 1 ? SC.t("path") : SC.t("paths"))+" %s", found, SC.t("found")));
			else if (found == 0 && improved == 0)
				stepInfo.append(SC.t(" - "+SC.t("none_found_none_improved_FS"), "paths", "found", "improved"));
			else if (found > 0 && improved > 0)
				stepInfo.append(String.format(" - %d "+(found == 1 ? SC.t("path") : SC.t("paths"))+" %s, %d %s", found, SC.t("found"), improved, SC.t("improved")));
			else if (found == 0 && improved > 0)
				stepInfo.append(String.format(" - %d "+(found == 1 ? SC.t("path") : SC.t("paths"))+" %s", improved, SC.t("improved")));
			
			if (changes.getNextMinimum() != null) {
				String fs = String.format("\n\n%s %s\n%s", SC.t("min_distance_node_FS"), graph.getInEdges(changes.getNextMinimum()).size() > 1 ? SC.t("other_node_argument_FS") : SC.t("no_other_edge_FS"), SC.t("node_settled_info_FS"));
				stepInfo.append(String.format(fs, changes.getNextMinimum(), changes.getNextMinimumDistance()));
			}
		} else {
			stepInfo.append(SC.t("algorithm_finished"));
			if (changes.getMinimum() != algo.getStart())
				changes.getMinimum().setAttribute(INode.Attribute.SETTLED);
		}
		
		stepInfo.append("\n\n");
		stepInfo.append(SC.t("mouse_over_info"));		
		
		stepInfo.setCaretPosition(0);
	}

	public void initialized(int maxSteps) {
		currentChanges = null;
		this.maxSteps = maxSteps;
		progressBar.setMaximum(maxSteps);
	}

	public void reset() {
	}

	// PROPERTIES FOR ACTIONS

	public void setCanStepBackward(boolean canStepBackward) {
		final boolean oldValue = this.canStepBackward;
		this.canStepBackward = canStepBackward;
		firePropertyChange("canStepBackward", oldValue, this.canStepBackward);
	}

	public boolean isCanStepBackward() {
		return canStepBackward;
	}

	public void setCanStepForward(boolean canStepForward) {
		final boolean oldValue = this.canStepForward;
		this.canStepForward = canStepForward;
		firePropertyChange("canStepForward", oldValue, this.canStepForward);
	}

	public boolean isCanStepForward() {
		return canStepForward;
	}

	public void setStartable(boolean startable) {
		final boolean oldValue = this.startable;
		this.startable = startable;
		firePropertyChange("startable", oldValue, this.startable);
	}

	public boolean isStartable() {
		return startable;
	}

	public void setStoppable(boolean stoppable) {
		final boolean oldValue = this.stoppable;
		this.stoppable = stoppable;
		firePropertyChange("stoppable", oldValue, this.stoppable);
	}

	public boolean isStoppable() {
		return stoppable;
	}

}
