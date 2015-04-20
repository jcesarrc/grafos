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

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import co.edu.poli.medgraph.gui.language.LocaleChangeListener;
import co.edu.poli.medgraph.gui.language.LocaleManager;


@SuppressWarnings("serial")
public class StatusBar extends JPanel implements LocaleChangeListener {

	public static final String EMPTY_TEXT = " ";
	private static StatusBar instance = null;
	private JLabel label;

	public static StatusBar getInstance() {
		if (instance == null) {
			instance = new StatusBar();
		}
		return instance;
	}

	private StatusBar() {
		setLayout(new FlowLayout(FlowLayout.LEFT, 3, 3));
		add(label = new JLabel(EMPTY_TEXT));
		label.setForeground(Color.DARK_GRAY);
		LocaleManager.addLocaleChangeListener(this);
	}
	
	public void setText(final String str) {
		if (!label.getText().equals(str)) {
			label.setText(str == null || (str != null && str.length() == 0) ? EMPTY_TEXT : str);
		}
	}
	
	public void localeChanged() {
		setText(EMPTY_TEXT);
	}
	
	public class MouseListener extends MouseAdapter {
		
		private final JComponent comp;
		
		public MouseListener(final JComponent comp) {
			this.comp = comp;
		}
		
		@Override
		public void mouseEntered(MouseEvent e) {
			setText(comp.getToolTipText());
		}
		
		@Override
		public void mouseExited(MouseEvent e) {
			setText(EMPTY_TEXT);
		}

	}

}
