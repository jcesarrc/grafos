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
package org.uweschmidt.dijkstravis.gui.jung.mouse.plugins;

import java.awt.Cursor;
import java.awt.event.MouseEvent;

import org.uweschmidt.dijkstravis.graph.algorithm.DijkstraAlgorithmManager;
import org.uweschmidt.dijkstravis.gui.jung.mouse.MyGraphMouse;


import edu.uci.ics.jung.visualization.control.TranslatingGraphMousePlugin;

public class MyTranslatingGraphMousePlugin extends TranslatingGraphMousePlugin {

	private boolean inactive = false;

	public MyTranslatingGraphMousePlugin(final int modifiers) {
		super(modifiers);
		this.cursor = Cursor.getDefaultCursor();
	}

	// fixes bug in TranslatingGraphMousePlugin, because the mouse event
	// is not consumed and thus leads to the execution of another plugin
	@Override
	public void mousePressed(MouseEvent e) {
		super.mousePressed(e);
		if (!MyGraphMouse.isOverNode(e) || DijkstraAlgorithmManager.isAlgoRunning()) {
			if (checkModifiers(e) && e.getClickCount() == 1) {
				e.consume();
			}
		} else {
			inactive = true;
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (!inactive) {
			super.mouseDragged(e);
		}		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		inactive = false;
		super.mouseReleased(e);
	}
}