package co.edu.poli.medgraph.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.jdesktop.application.Application;
import co.edu.poli.medgraph.gui.language.LocaleChangeListener;
import co.edu.poli.medgraph.gui.language.LocaleManager;
import co.edu.poli.medgraph.util.SC;


@SuppressWarnings("serial")
public class MenuBar extends JMenuBar implements LocaleChangeListener {
	
	public MenuBar(IntroWindow intro, HelpPanel help) {
		super();
		JMenu menu;
		final JCheckBoxMenuItem hcb, icp;
		
		add(menu = SC.newComponent(JMenu.class, "appMenu"));
		menu.add(icp = SC.createActionItem(JCheckBoxMenuItem.class, "toggleIntro", intro));
		menu.add(hcb = SC.createActionItem(JCheckBoxMenuItem.class, "toggleHelp", help));
		menu.add(SC.createActionItem(JMenuItem.class, "switchLanguage", Application.getInstance()));
		menu.addSeparator();
		menu.add(SC.createActionItem(JMenuItem.class, "exitApp", Application.getInstance()));
		

		add(menu = SC.newComponent(JMenu.class, "graphMenu"));
		menu.add(SC.createActionItem(JMenuItem.class, "newGraph", GraphPanel.getInstance()));
		menu.add(SC.createActionItem(JMenuItem.class, "openGraph", GraphPanel.getInstance()));
		menu.add(SC.createActionItem(JMenuItem.class, "saveGraph", GraphPanel.getInstance()));
		menu.addSeparator();
		menu.add(GraphPanel.getInstance().getSampleGraphMenu());
		menu.add(GraphPanel.getInstance().getRandomGraphMenu());
		
		add(SC.lookAndFeelMenu(SC.newComponent(JMenu.class, "lfMenu")));
		
		intro.addPropertyChangeListener("visible", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				icp.setSelected(Boolean.TRUE.equals(evt.getNewValue()));
			}
		});

		help.addPropertyChangeListener("collapsed", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				hcb.setSelected(Boolean.FALSE.equals(evt.getNewValue()));
			}
		});
		
		LocaleManager.addLocaleChangeListener(this);
		localeChanged();
	}
	
	public void localeChanged() {
		SC.getResourceMap(MenuBar.class).injectComponents(this);
		SC.getResourceMap(ToolBar.class).injectComponents(this);
		SC.getResourceMap(HelpPanel.class).injectComponents(this);
	}

}
