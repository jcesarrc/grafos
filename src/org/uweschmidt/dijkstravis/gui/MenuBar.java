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
package org.uweschmidt.dijkstravis.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.jdesktop.application.Application;
import org.uweschmidt.dijkstravis.language.LocaleChangeListener;
import org.uweschmidt.dijkstravis.language.LocaleManager;
import org.uweschmidt.dijkstravis.util.SC;


@SuppressWarnings("serial")
public class MenuBar extends JMenuBar implements LocaleChangeListener {
	
	public MenuBar(IntroWindow intro, HelpPanel help, AboutWindow about) {
		super();
		JMenu menu;
		final JCheckBoxMenuItem hcb, icp;
		
		add(menu = SC.newComponent(JMenu.class, "appMenu"));
		menu.add(icp = SC.createActionItem(JCheckBoxMenuItem.class, "toggleIntro", intro));
		menu.add(hcb = SC.createActionItem(JCheckBoxMenuItem.class, "toggleHelp", help));
		menu.add(SC.createActionItem(JMenuItem.class, "switchLanguage", Application.getInstance()));
		menu.addSeparator();
		menu.add(SC.createActionItem(JMenuItem.class, "about", about));
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
