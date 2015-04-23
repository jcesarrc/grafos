package co.edu.poli.medgraph.gui.impl.mouse;

import co.edu.poli.medgraph.algoritmo.AlgorithmProgressListener;
import co.edu.poli.medgraph.algoritmo.DijkstraAlgorithmManager;
import co.edu.poli.medgraph.algoritmo.DijkstraStepChanges;
import co.edu.poli.medgraph.grafo.IEdge;
import co.edu.poli.medgraph.grafo.INode;
import co.edu.poli.medgraph.grafo.impl.MyEdge;
import co.edu.poli.medgraph.grafo.impl.MyEdgeFactory;
import co.edu.poli.medgraph.grafo.impl.MyNode;
import co.edu.poli.medgraph.grafo.impl.MyNodeFactory;
import co.edu.poli.medgraph.gui.GraphPanel;
import co.edu.poli.medgraph.gui.impl.mouse.plugins.MyEditingGraphMousePlugin;
import co.edu.poli.medgraph.gui.impl.mouse.plugins.MyEditingPopupGraphMousePlugin;
import co.edu.poli.medgraph.gui.impl.mouse.plugins.MyHighlighterGraphMousePlugin;
import co.edu.poli.medgraph.gui.impl.mouse.plugins.MyInfoOverlayGraphMousePlugin;
import co.edu.poli.medgraph.gui.impl.mouse.plugins.MyPickingGraphMousePlugin;
import co.edu.poli.medgraph.gui.impl.mouse.plugins.MyRotatingGraphMousePlugin;
import co.edu.poli.medgraph.gui.impl.mouse.plugins.MyStartnodeGraphMousePlugin;
import co.edu.poli.medgraph.gui.impl.mouse.plugins.MyTranslatingGraphMousePlugin;
import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AnimatedPickingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.EditingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.PickingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.PluggableGraphMouse;
import edu.uci.ics.jung.visualization.control.RotatingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.control.ScalingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.TranslatingGraphMousePlugin;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.apache.commons.collections15.Factory;

public class MyGraphMouse extends PluggableGraphMouse implements AlgorithmProgressListener<DijkstraStepChanges> {

    private VisualizationViewer<INode, IEdge> vv;
    private Factory<MyNode> vertexFactory;
    private Factory<MyEdge> edgeFactory;

    private MyStartnodeGraphMousePlugin startNodePlugin;
    private EditingGraphMousePlugin<MyNode, MyEdge> editingPlugin;
    private ScalingGraphMousePlugin scalingPlugin;

    private PickingGraphMousePlugin<INode, IEdge> pickingPlugin;
    private AnimatedPickingGraphMousePlugin<INode, IEdge> animatedPickingPlugin;

    private TranslatingGraphMousePlugin translatingPlugin;
    private RotatingGraphMousePlugin rotatingPlugin;

    private MyInfoOverlayGraphMousePlugin infoPlugin;
    private MyHighlighterGraphMousePlugin mouseOverHighlighter;

    private MyEditingPopupGraphMousePlugin<MyNode, MyEdge> popupEditingPlugin;

    private GraphPanel gp;

    public MyGraphMouse(GraphPanel gp, VisualizationViewer<INode, IEdge> vv) {
        DijkstraAlgorithmManager.addAlgorithmProgressListener(this);
        this.vertexFactory = MyNodeFactory.getInstance();
        this.edgeFactory = MyEdgeFactory.getInstance();
        this.gp = gp;
        this.vv = vv;
        loadPlugins();
        addAllPlugins();
        gp.addPropertyChangeListener("graphBackground", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (MyGraphMouse.this.gp.isGraphBackground()) {
                    remove(scalingPlugin);
                    remove(translatingPlugin);
                    remove(rotatingPlugin);
                } else {
                    add(scalingPlugin);
                    add(translatingPlugin);
                    add(rotatingPlugin);
                }
            }
        });
    }

    private void loadPlugins() {
        pickingPlugin = new MyPickingGraphMousePlugin<INode, IEdge>(InputEvent.BUTTON1_MASK, InputEvent.BUTTON1_MASK | Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), InputEvent.BUTTON3_MASK);
        animatedPickingPlugin = new AnimatedPickingGraphMousePlugin<INode, IEdge>(InputEvent.BUTTON1_MASK | InputEvent.CTRL_MASK);
        scalingPlugin = new ScalingGraphMousePlugin(new CrossoverScalingControl(), 0, 1.1f, 1 / 1.1f);
        rotatingPlugin = new MyRotatingGraphMousePlugin(InputEvent.BUTTON3_MASK | MouseEvent.SHIFT_MASK);
        translatingPlugin = new MyTranslatingGraphMousePlugin(InputEvent.BUTTON3_MASK);
        popupEditingPlugin = new MyEditingPopupGraphMousePlugin<MyNode, MyEdge>(null, vertexFactory, edgeFactory);
        startNodePlugin = new MyStartnodeGraphMousePlugin();
        editingPlugin = new MyEditingGraphMousePlugin(this, null, vertexFactory, edgeFactory);
        mouseOverHighlighter = new MyHighlighterGraphMousePlugin();
        infoPlugin = new MyInfoOverlayGraphMousePlugin(vv);
    }

    public ScalingControl getScaler() {
        return scalingPlugin.getScaler();
    }

    private void addAllPlugins() {
        add(infoPlugin);
        add(mouseOverHighlighter);

        if (gp.isNoGraphBackground()) {
            add(scalingPlugin);
            add(translatingPlugin);
            add(rotatingPlugin);
        }

        add(editingPlugin);
        add(startNodePlugin);
        add(pickingPlugin);
        add(popupEditingPlugin);
    }

    public void setZoomAtMouse(boolean zoomAtMouse) {
        scalingPlugin.setZoomAtMouse(zoomAtMouse);
    }

    public void setLayout(Layout<INode, IEdge> layout) {
		
    }

    public void stepChanged(int step, DijkstraStepChanges changes) {
    }

    public void initialized(int maxSteps) {
        remove(pickingPlugin);
        remove(animatedPickingPlugin);
        remove(editingPlugin);
        remove(startNodePlugin);
        remove(popupEditingPlugin);

        vv.getPickedVertexState().clear();
        vv.getPickedEdgeState().clear();
    }

    public void reset() {
        addAllPlugins();
    }

    private static enum Over {

        NODE, EDGE, NOTHING
    };

    @SuppressWarnings("unchecked")
    private static Over isOver(final MouseEvent e) {
        final VisualizationViewer<INode, IEdge> vv = (VisualizationViewer<INode, IEdge>) e.getSource();
        final Point2D p = e.getPoint();
        final GraphElementAccessor<INode, IEdge> pickSupport = vv.getPickSupport();
        final INode vertex = pickSupport.getVertex(vv.getModel().getGraphLayout(), p.getX(), p.getY());
        if (vertex != null) {
            return Over.NODE;
        }
        final IEdge edge = pickSupport.getEdge(vv.getModel().getGraphLayout(), p.getX(), p.getY());
        if (edge != null) {
            return Over.EDGE;
        }
        return Over.NOTHING;
    }

    public static boolean isOverNode(final MouseEvent e) {
        return isOver(e) == Over.NODE;
    }

    public static boolean isOverEdge(final MouseEvent e) {
        return isOver(e) == Over.EDGE;
    }
}
