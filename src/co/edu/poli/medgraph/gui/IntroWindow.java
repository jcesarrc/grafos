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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.swingx.JXHeader;
import co.edu.poli.medgraph.main.DijkstraVisApp;
import co.edu.poli.medgraph.gui.language.LocaleChangeListener;
import co.edu.poli.medgraph.gui.language.LocaleManager;
import co.edu.poli.medgraph.util.SC;


@SuppressWarnings("serial")
public class IntroWindow extends JDialog implements LocaleChangeListener {

	private JEditorPane html;
	private boolean start = true;

	public IntroWindow(JFrame owner) {
		super(owner, false);
		setName("main");
		setLayout(new MigLayout("gap 0, insets 0"));
		addComponents();
		LocaleManager.addLocaleChangeListener(this);
		localeChanged();
		setSize(640, 700);
		setLocationRelativeTo(null);
	}

	private void addComponents() {

		add(SC.newComponent(JXHeader.class, "appHeader"), "growx, pushx, wrap");
		add(new JSeparator(SwingConstants.HORIZONTAL), "growx, pushx, wrap");

		html = new JEditorPane("text/html", null) {
			@Override
			protected void paintComponent(Graphics g) {
				((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				super.paintComponent(g);
			}
		};
		html.setEditable(false);
		html.setFocusable(false);

		add(new JScrollPane(html), "gap 10 10 10 10, grow, push, wrap");
		add(SC.createActionItem(JButton.class, "toggleIntro", this), "split, span, center, gapbottom 10");
		
		add(SC.createActionItem(JButton.class, "switchLanguage", Application.getInstance()), "pos null null 100%-10 100%-10");
	}

	public void localeChanged() {
		SC.getResourceMap(ToolBar.class).injectComponents(this);
		SC.getResourceMap(IntroWindow.class).injectComponents(this);
		try {
			html.setPage(getClass().getResource(String.format("resources/help/intro_%s.html", LocaleManager.getLocale().getLanguage())));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Action
	public void toggleIntro() {
		setVisible(!isVisible());
	}
	
	@Override
	public void setVisible(boolean b) {
		firePropertyChange("visible", isVisible(), b);
		super.setVisible(b);
		if (start && !b) {
			if (JOptionPane.showConfirmDialog(Application.getInstance(DijkstraVisApp.class).getMainFrame(), SC.t("load_sample_question"), null, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION)
				GraphPanel.getInstance().nycSampleGraph();
			start = false;
		}
	}

}
