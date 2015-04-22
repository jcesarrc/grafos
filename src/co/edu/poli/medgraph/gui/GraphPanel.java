package co.edu.poli.medgraph.gui;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.collections15.Transformer;
import org.jdesktop.application.Action;
import co.edu.poli.medgraph.grafo.GraphChangeListener;
import co.edu.poli.medgraph.grafo.GraphManager;
import co.edu.poli.medgraph.grafo.IEdge;
import co.edu.poli.medgraph.grafo.IGraph;
import co.edu.poli.medgraph.grafo.INode;
import co.edu.poli.medgraph.algoritmo.AlgorithmProgressListener;
import co.edu.poli.medgraph.algoritmo.DijkstraAlgorithmManager;
import co.edu.poli.medgraph.algoritmo.DijkstraStepChanges;
import co.edu.poli.medgraph.grafo.impl.MyEdge;
import co.edu.poli.medgraph.grafo.impl.MyEdgeFactory;
import co.edu.poli.medgraph.grafo.impl.MyGraphFactory;
import co.edu.poli.medgraph.grafo.impl.MyNode;
import co.edu.poli.medgraph.grafo.impl.MyNodeFactory;
import co.edu.poli.medgraph.grafo.impl.MyPseudoPlanarGraphGenerator;
import co.edu.poli.medgraph.gui.animation.AnimationHandler;
import co.edu.poli.medgraph.gui.animation.renderer.MyAnimationEdgeRenderer;
import co.edu.poli.medgraph.gui.animation.renderer.MyAnimationNodeRenderer;
import co.edu.poli.medgraph.gui.animation.renderer.MyAnimationRenderer;
import co.edu.poli.medgraph.gui.jung.mouse.MyGraphMouse;
import co.edu.poli.medgraph.gui.transformer.MyEdgeArrowTransformer;
import co.edu.poli.medgraph.gui.transformer.MyEdgeLabelTransformer;
import co.edu.poli.medgraph.gui.transformer.MyEdgePaintTransformer;
import co.edu.poli.medgraph.gui.transformer.MyEdgeStrokeTransformer;
import co.edu.poli.medgraph.gui.transformer.MyNodeFillPaintTransformer;
import co.edu.poli.medgraph.gui.transformer.MyNodeLabelTransformer;
import co.edu.poli.medgraph.gui.transformer.MyNodeShapeTransformer;
import co.edu.poli.medgraph.io.FileHandler;
import co.edu.poli.medgraph.language.LocaleChangeListener;
import co.edu.poli.medgraph.language.LocaleManager;
import co.edu.poli.medgraph.util.SC;

import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.LayoutDecorator;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.renderers.Renderer;

@SuppressWarnings("serial")
public class GraphPanel extends JPanel implements GraphChangeListener, AlgorithmProgressListener<DijkstraStepChanges>, LocaleChangeListener {
	
	private final MyGraphMouse gm;
	private final VisualizationViewer<INode, IEdge> vv;
	private IGraph graph;
	private Transformer<INode, Point2D> layout;
	private final AlgorithmPanel acp;
	private boolean algoRunning = false;
	private boolean emptyGraph = true;
	private boolean dirty = false;
	private BufferedImage rawGraphBackground = null;
	private Image graphBackground = null;
	public final JMenu randomGraphMenu, sampleGraphMenu;
	private LayoutTracker layoutTracker = new LayoutTracker();
	
	private static GraphPanel instance = null;
	private GraphZoomScrollPane graphZoomScrollPane;

	public static GraphPanel getInstance() {
		if (instance == null) {
			instance = new GraphPanel();
		}
		return instance;
	}

	public void graphReplaced(IGraph graph, Transformer<INode, Point2D> layout) {
		this.graph = graph;
		this.layout = layout;
		vv.setGraphLayout((Layout<INode, IEdge>) layout);
		gm.setLayout((Layout<INode, IEdge>) layout);
		resetView();
		graphChanged();
		dirty = false;
	}
	
	public void graphChanged() {
		dirty = true;
		setEmptyGraph(graph.getNumberOfNodes() == 0);
	}
	
	public void stepChanged(int step, DijkstraStepChanges changes) {
	}

	public void initialized(int maxSteps) {
		setAlgoRunning(true);
		randomGraphMenu.setEnabled(false);
		sampleGraphMenu.setEnabled(false);
	}

	public void reset() {
		setAlgoRunning(false);
		randomGraphMenu.setEnabled(true);
		sampleGraphMenu.setEnabled(true);
	}

	private GraphPanel() {

		GraphManager.addGraphChangeListener(this);
		DijkstraAlgorithmManager.addAlgorithmProgressListener(this);
		LocaleManager.addLocaleChangeListener(this);

		vv = new VisualizationViewer<INode, IEdge>(new StaticLayout<INode, IEdge>(MyGraphFactory.getInstance().create()));

		vv.addPreRenderPaintable(new VisualizationServer.Paintable() {
			public void paint(Graphics g) {
				if (graphBackground != null) {
					g.drawImage(graphBackground, 0, 0, vv);
				}
			}
			public boolean useTransform() {
				return false;
			}
		});
		
		vv.setRenderer(new MyAnimationRenderer());

		vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);
		vv.getRenderContext().setVertexLabelTransformer(new MyNodeLabelTransformer());
		vv.getRenderContext().setVertexFillPaintTransformer(new MyNodeFillPaintTransformer(vv.getRenderContext()));
		vv.getRenderContext().setVertexShapeTransformer(new MyNodeShapeTransformer());
		vv.getRenderer().setVertexRenderer(new MyAnimationNodeRenderer());
		
		MyEdgePaintTransformer edgeDrawPaintTransformer = new MyEdgePaintTransformer();
		vv.getRenderContext().setEdgeDrawPaintTransformer(edgeDrawPaintTransformer);
		vv.getRenderContext().setArrowFillPaintTransformer(edgeDrawPaintTransformer);
		vv.getRenderContext().setArrowDrawPaintTransformer(edgeDrawPaintTransformer);
		vv.getRenderContext().setEdgeLabelTransformer(new MyEdgeLabelTransformer());
		vv.getRenderContext().setEdgeArrowTransformer(new MyEdgeArrowTransformer());
		vv.getRenderContext().setEdgeStrokeTransformer(new MyEdgeStrokeTransformer());
		vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line<INode, IEdge>());
		vv.getRenderer().setEdgeRenderer(new MyAnimationEdgeRenderer());

		vv.setGraphMouse(gm = new MyGraphMouse(this, vv));

		/* GUI STUFF */
		
		setLayout(new BorderLayout());

		graphZoomScrollPane = new GraphZoomScrollPane(vv);		
		add(graphZoomScrollPane, BorderLayout.CENTER);
		
//		final JButton corner = new JButton();
//		corner.setMargin(new Insets(0,0,0,0));
//		corner.setFont(new Font("LucidaSans", Font.PLAIN, 8));
//		corner.setAction(SC.getAction(this, "resetView"));
//		corner.setIcon(null);
//		corner.setText("1:1");
//		graphZoomScrollPane.setCorner(corner);

		add(acp = new AlgorithmPanel(), BorderLayout.EAST);

		new AnimationHandler(vv, acp);
		
		randomGraphMenu = SC.newComponent(JMenu.class, "randomGraph");
		randomGraphMenu.add(SC.createActionItem(JMenuItem.class, "smallRandomGraph", this));
		randomGraphMenu.add(SC.createActionItem(JMenuItem.class, "mediumRandomGraph", this));
		randomGraphMenu.add(SC.createActionItem(JMenuItem.class, "largeRandomGraph", this));
		randomGraphMenu.add(SC.createActionItem(JMenuItem.class, "hugeRandomGraph", this));
		
		sampleGraphMenu = SC.newComponent(JMenu.class, "sampleGraph");
		sampleGraphMenu.add(SC.createActionItem(JMenuItem.class, "basic1SampleGraph", this));
		sampleGraphMenu.add(SC.createActionItem(JMenuItem.class, "basic2SampleGraph", this));
		sampleGraphMenu.addSeparator();
		sampleGraphMenu.add(SC.createActionItem(JMenuItem.class, "nycSampleGraph", this));
		sampleGraphMenu.add(SC.createActionItem(JMenuItem.class, "dijkstraSampleGraph", this));
		
		// otherwise doesn't work in menubar after once triggered by toolbar button
		randomGraphMenu.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				randomGraphMenu.getPopupMenu().setInvoker(randomGraphMenu);
			}
		});

		// otherwise doesn't work in menubar after once triggered by toolbar button
		sampleGraphMenu.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				sampleGraphMenu.getPopupMenu().setInvoker(sampleGraphMenu);
			}
		});

		localeChanged();

		newGraph();
	}
	
	public void localeChanged() {
		graphZoomScrollPane.setBorder(BorderFactory.createTitledBorder(SC.t("graph")));
		SC.getResourceMap(GraphPanel.class).injectComponents(randomGraphMenu);
		SC.getResourceMap(GraphPanel.class).injectComponents(sampleGraphMenu);
	}
	
	@Action(enabledProperty="noGraphBackground")
	public void zoomIn() {
		gm.getScaler().scale(vv, 1.1f, vv.getCenter());
	}

	@Action(enabledProperty="noGraphBackground")
	public void zoomOut() {
		gm.getScaler().scale(vv, 1 / 1.1f, vv.getCenter());
	}

	@Action(enabledProperty="noGraphBackground")
	public void resetView() {
		vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).setToIdentity();
		vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW).setToIdentity();
	}

//	@Action(enabledProperty = "layoutEnabled")
//	public void rearrangeNodes() {
//		ISOMLayout<INode, IEdge> layout = new ISOMLayout<INode, IEdge>((MyGraph) graph);
//		GraphManager.setLayout(layout);
//	}
	
	@Action(enabledProperty="notAlgoNotEmpty")
	public void newGraph() {
		if (!FileHandler.saveGraphToFile(graph, layout, isDirty())) return;
		MyNodeFactory.getInstance().reset();
		MyEdgeFactory.getInstance().reset();
		Graph<INode, IEdge> graph = MyGraphFactory.getInstance().create();
		GraphManager.setGraphAndLayout((IGraph)graph, newLayout(graph));
		setGraphBackground(null);
	}
	
	@Action(enabledProperty="notAlgoRunning")
	public void openGraph() {
		FileHandler.openFile(graph, layout, isDirty());
	}
	
	@Action(enabledProperty="notAlgoRunning")
	public void saveGraph() {
		if (FileHandler.saveFile(graph, layout))
			dirty = false;
	}
	
	public JMenu getRandomGraphMenu() {
		return randomGraphMenu;
	}
	
	public JMenu getSampleGraphMenu() {
		return sampleGraphMenu;
	}
	
		
	@Action(enabledProperty="notAlgoRunning")
	public void randomGraph() {
		// dummy action, just needed for properties
	}
	
	private void sampleGraph(String name) {
		final File tmpFile = new File(System.getProperty("java.io.tmpdir"), name);
		if (FileHandler.copyFromJar(getClass(), "resources/samples/"+name, tmpFile)) {
			FileHandler.openFile(graph, layout, tmpFile, isDirty());
		}
		// doesn't work inside JAR file
		// URL url = getClass().getResource("resources/samples/"+name+".zip");
		// FileHandler.openFile(graph, layout, new File(url.getFile()));
	}
	
	@Action
	public void basic1SampleGraph() {
		sampleGraph("basic1.zip");
	}
	
	@Action
	public void basic2SampleGraph() {
		sampleGraph("basic2.zip");
	}
	
	@Action
	public void nycSampleGraph() {
		sampleGraph("nyc.zip");
	}
	
	@Action
	public void dijkstraSampleGraph() {
		sampleGraph("dijkstra.zip");
	}
	
	@Action(enabledProperty="notAlgoRunning")
	public void sampleGraph() {
		// dummy action, just needed for properties
	}
	
	public Transformer<INode, Point2D> newLayout(Graph<INode, IEdge> graph) {
		return newLayout(new StaticLayout<INode, IEdge>(graph));
	}
	
	public Transformer<INode, Point2D> newLayout(Layout<INode, IEdge> delegate) {
		layoutTracker.setDelegate(delegate);
		return layoutTracker;
	}

	public boolean isAlgoRunning() {
		return algoRunning;
	}
	
	public boolean isNotAlgoRunning() {
		return !algoRunning;
	}

	public void setAlgoRunning(boolean algoRunning) {
		final boolean oldAlgoRunning = this.algoRunning;
		final boolean oldLayoutEnabled = isNotAlgoNotEmpty();
		this.algoRunning = algoRunning;
		firePropertyChange("algoRunning", oldAlgoRunning, algoRunning);
		firePropertyChange("notAlgoRunning", !oldAlgoRunning, !algoRunning);
		firePropertyChange("notAlgoNotEmpty", oldLayoutEnabled, isNotAlgoNotEmpty());
	}
	
	public boolean isEmptyGraph() {
		return emptyGraph;
	}
	
	public boolean isNotEmptyGraph() {
		return !isEmptyGraph();
	}
	
	public void setEmptyGraph(boolean emptyGraph) {
		final boolean oldEmptyGraph = this.emptyGraph;
		final boolean oldLayoutEnabled = isNotAlgoNotEmpty();
		this.emptyGraph = emptyGraph;
		firePropertyChange("emptyGraph", oldEmptyGraph, emptyGraph);
		firePropertyChange("notAlgoNotEmpty", !oldEmptyGraph, !emptyGraph);
		firePropertyChange("layoutEnabled", oldLayoutEnabled, isNotAlgoNotEmpty());
	}
	
	public boolean isNotAlgoNotEmpty() {
		return !isAlgoRunning() && !isEmptyGraph();
	}
	
	public void setGraphBackground(BufferedImage graphBackground) {
		final Image oldGraphBackground = this.graphBackground;
		rawGraphBackground = graphBackground;
		
		// is way faster than using the BufferedImage directly
		this.graphBackground = graphBackground != null ? Toolkit.getDefaultToolkit().createImage(graphBackground.getSource()) : null;
			
		graphZoomScrollPane.getHorizontalScrollBar().setEnabled(graphBackground == null);
		graphZoomScrollPane.getVerticalScrollBar().setEnabled(graphBackground == null);		

		firePropertyChange("graphBackground", oldGraphBackground != null, this.graphBackground != null);
		firePropertyChange("noGraphBackground", oldGraphBackground == null, this.graphBackground == null);
	}
		
	public boolean isGraphBackground() {
		return graphBackground != null;
	}
	
	public boolean isNoGraphBackground() {
		return graphBackground == null;
	}
	
	public BufferedImage getGraphBackground() {
		return rawGraphBackground;
	}
	
	public boolean isDirty() {
		return dirty;
	}
	
	private class LayoutTracker extends LayoutDecorator<INode, IEdge> {
		public LayoutTracker() {
			super(null);
		}

		@Override
		public void setLocation(INode v, Point2D location) {
			super.setLocation(v, location);
			dirty = true;
		}
	}

}
