
package co.edu.poli.medgraph.gui;

import co.edu.poli.medgraph.util.SC;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.application.Action;
import org.jdesktop.swingx.JXCollapsiblePane;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTitledSeparator;


@SuppressWarnings("serial")
public class HelpPanel extends JXCollapsiblePane {
	
    private static final int WIDTH = 250;
	
	private JScrollPane scrollPane;

	public HelpPanel() {
		super();
		setOpaque(false);
		setCollapsed(true);
		setName("main");		
		
		JXPanel tpc = new JXPanel() {
			@Override
			public boolean getScrollableTracksViewportHeight() {
				if (getParent() instanceof JViewport) {
					return (((JViewport) getParent()).getHeight() > getPreferredSize().height);
				} else {
					return false;
				}
			}
			@Override
			public boolean getScrollableTracksViewportWidth() {
				return true;
			}
		};
		tpc.setLayout(new MigLayout("insets 0 10 10 10, wrap 1", "[fill]"));
		
		add(scrollPane = new JScrollPane(tpc, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER));
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		
		tpc.add(SC.newComponent(JXTitledSeparator.class, "basics"), "gaptop -10");
		tpc.add(newTopic("help_basics_graph"));
		tpc.add(newTopic("help_basics_algorithm"));
		tpc.add(newTopic("help_basics_program_use"));
		
		tpc.add(SC.newComponent(JXTitledSeparator.class, "graphEdit"));
		tpc.add(newTopic("help_graph_node_add"));
		tpc.add(newTopic("help_graph_node_delete"));
		tpc.add(newTopic("help_graph_node_link"));
		tpc.add(newTopic("help_graph_node_unlink"));
		tpc.add(newTopic("help_graph_node_select"));
		tpc.add(newTopic("help_graph_node_move"));
		
		tpc.add(SC.newComponent(JXTitledSeparator.class, "graphView"));
		tpc.add(newTopic("help_graph_navigate"));
		tpc.add(newTopic("help_graph_zoom"));		

		tpc.add(SC.newComponent(JXTitledSeparator.class, "runAlgorithm"));
		tpc.add(newTopic("help_algorithm_switch"));
		tpc.add(newTopic("help_algorithm_controls"));
		tpc.add(newTopic("help_algorithm_speed"));
		
		tpc.add(SC.newComponent(JXTitledSeparator.class, "dijkstraAlgorithm"));
		tpc.add(newTopic("help_dijkstra_algorithm_who"));
		tpc.add(newTopic("help_dijkstra_algorithm_what"));
		tpc.add(newTopic("help_dijkstra_algorithm_how"));
		tpc.add(newTopic("help_dijkstra_algorithm_where"));
				
		
	}
	
	private JXTaskPane newTopic(final String title) {
		final String content = title+"_c";
		final JXTaskPane tp = new JXTaskPane();
		tp.setLayout(new MigLayout("insets -7"));
		
	
		
		final JEditorPane html = new JEditorPane("text/html", null) {
			@Override
			protected void paintComponent(Graphics g) {
				((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				super.paintComponent(g);
			}
		};
		html.setOpaque(false);
		html.setEditable(false);
		html.setFocusable(false);		
		tp.add(html, "w " + WIDTH);
		
		try {
			((HTMLDocument) html.getDocument()).setBase(getClass().getResource("resources/help/"));
			((HTMLEditorKit) html.getEditorKitForContentType("text/html")).getStyleSheet().loadRules(new InputStreamReader(getClass().getResourceAsStream("resources/help/style.css")),getClass().getResource("resources/help/style.css"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		
		
		return tp;
	}
	
	@Action
	public void toggleHelp() {
		setCollapsed(!isCollapsed());
	}
	
	

}
