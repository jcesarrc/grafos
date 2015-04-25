package co.edu.poli.medgraph.gui;

import co.edu.poli.medgraph.util.SC;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import org.jdesktop.application.Application;

@SuppressWarnings("serial")
public class MenuBar extends JMenuBar {

    public MenuBar() {
        super();
        JMenu menu;
        final JCheckBoxMenuItem hcb;

        add(menu = SC.newComponent(JMenu.class, "appMenu"));
        
        menu.addSeparator();
        menu.add(SC.createActionItem(JMenuItem.class, "exitApp", Application.getInstance()));
        add(menu = SC.newComponent(JMenu.class, "graphMenu"));
        menu.add(SC.createActionItem(JMenuItem.class, "newGraph", GraphPanel.getInstance()));
        menu.add(SC.createActionItem(JMenuItem.class, "openGraph", GraphPanel.getInstance()));
        menu.add(SC.createActionItem(JMenuItem.class, "saveGraph", GraphPanel.getInstance()));
        menu.addSeparator();

    }

}
