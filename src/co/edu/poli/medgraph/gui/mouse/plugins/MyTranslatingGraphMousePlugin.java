package co.edu.poli.medgraph.gui.mouse.plugins;

import java.awt.Cursor;
import java.awt.event.MouseEvent;

import co.edu.poli.medgraph.algoritmo.DijkstraAlgorithmManager;
import co.edu.poli.medgraph.gui.jung.mouse.MyGraphMouse;


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