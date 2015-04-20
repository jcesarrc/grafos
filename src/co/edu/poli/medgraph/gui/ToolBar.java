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

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import org.jdesktop.application.Application;
import co.edu.poli.medgraph.gui.language.LocaleChangeListener;
import co.edu.poli.medgraph.gui.language.LocaleManager;
import co.edu.poli.medgraph.util.SC;


@SuppressWarnings("serial")
public class ToolBar extends JToolBar implements LocaleChangeListener {
	
	private static final int GAP = 20;

	public ToolBar(IntroWindow intro, HelpPanel help) {
		super(HORIZONTAL);
		setBorder(BorderFactory.createEmptyBorder(7,7,0,7));
//		setBorderPainted(false);
		setFloatable(false);
		setRollover(true);
		
		add(SC.createToolBarButton("newGraph", GraphPanel.getInstance()));
		add(SC.createToolBarButton("openGraph", GraphPanel.getInstance()));
		add(SC.createToolBarButton("saveGraph", GraphPanel.getInstance()));
		addSeparator(GAP);
		
		JButton b;

		add(b = SC.createToolBarButton("sampleGraph", GraphPanel.getInstance()));
		b.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (((JComponent)e.getSource()).isEnabled())
					GraphPanel.getInstance().getSampleGraphMenu().getPopupMenu().show(e.getComponent(), 0, e.getComponent().getHeight());
			}
		});		
		add(b = SC.createToolBarButton("randomGraph", GraphPanel.getInstance()));
		b.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (((JComponent)e.getSource()).isEnabled())
					GraphPanel.getInstance().getRandomGraphMenu().getPopupMenu().show(e.getComponent(), 0, e.getComponent().getHeight());
			}
		});
		
		addSeparator(GAP);
//		add(SC.createToolBarButton("rearrangeNodes",gp));
//		addSeparator(GAP);		
		add(SC.createToolBarButton("resetView", GraphPanel.getInstance()));
		add(SC.createToolBarButton("zoomIn", GraphPanel.getInstance()));
		add(SC.createToolBarButton("zoomOut", GraphPanel.getInstance()));

		add(Box.createHorizontalGlue());
		addSeparator(GAP);
		add(Box.createHorizontalGlue());
		
		final JToggleButton itb;
		add(itb = SC.createToolBarItem(JToggleButton.class, "toggleIntro", intro));
		intro.addPropertyChangeListener("visible", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				itb.setSelected(Boolean.TRUE.equals(evt.getNewValue()));
			}
		});

		final JToggleButton htb;
		add(htb = SC.createToolBarItem(JToggleButton.class, "toggleHelp", help));
		help.addPropertyChangeListener("collapsed", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				htb.setSelected(Boolean.FALSE.equals(evt.getNewValue()));
			}
		});

		add(SC.createToolBarButton("switchLanguage", Application.getInstance()));
		
		add(Box.createHorizontalGlue());
		addSeparator(GAP);
		add(Box.createHorizontalGlue());
		
		add(SC.createToolBarButton("exitApp", Application.getInstance()));

		LocaleManager.addLocaleChangeListener(this);
		localeChanged();		
	}
	
	private void addSeparator(int width) {
		addSeparator(new Dimension(width, 40));
	};

	public void localeChanged() {
		SC.getResourceMap(ToolBar.class).injectComponents(this);
		SC.getResourceMap(HelpPanel.class).injectComponents(this);
	}
	
}
