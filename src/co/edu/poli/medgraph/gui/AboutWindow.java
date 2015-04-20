
package co.edu.poli.medgraph.gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.net.URL;

import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import co.edu.poli.medgraph.util.BareBonesBrowserLaunch;
import co.edu.poli.medgraph.util.SC;


@SuppressWarnings("serial")
public class AboutWindow extends JDialog implements HyperlinkListener {

	public AboutWindow() {
		super(Application.getInstance(DijkstraVisApp.class).getMainFrame());
		setName("aboutWindow");
		setLayout(new BorderLayout());
		final JPanel appPane = SC.newComponent(JPanel.class, "appPane");
		add(appPane, BorderLayout.NORTH);
		appPane.setLayout(new MigLayout("center, wrap 1", "[center]"));

		JLabel appLabel = new JLabel(SC.getResourceMap(DijkstraVisApp.class).getString("Application.id"));
		appLabel.setFont(appLabel.getFont().deriveFont(Font.BOLD, 14f));
		JLabel crLabel = new JLabel(SC.getResourceMap(AboutWindow.class).getString("copyRight", "2008", "Uwe Schmidt"));
		crLabel.setFont(appLabel.getFont().deriveFont(10f));

		appPane.add(appLabel);
		appPane.add(crLabel);

		try {
			URL url = AboutWindow.class.getResource("resources/credits.html");
			JEditorPane tp = SC.newComponent(JEditorPane.class, "infoPane");
			tp.addHyperlinkListener(this);
			tp.setPage(url);
			add(new JScrollPane(tp), BorderLayout.CENTER);

			SC.getResourceMap(AboutWindow.class).injectComponents(this);

			setResizable(false);
			SC.placeDialogWindow(this, 284, 348);
		} catch (Exception e) {
			// catch all exceptions locally because of buggy JEditorPane
			e.printStackTrace();
		}
	}

	public void hyperlinkUpdate(HyperlinkEvent e) {
		if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			BareBonesBrowserLaunch.openURL(e.getURL().toString());
		}
	}

	@Action
	public void about() {
		setVisible(true);
	}

}
