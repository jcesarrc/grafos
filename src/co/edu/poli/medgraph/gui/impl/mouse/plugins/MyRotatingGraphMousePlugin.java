
package co.edu.poli.medgraph.gui.impl.mouse.plugins;

import edu.uci.ics.jung.visualization.control.RotatingGraphMousePlugin;
import java.awt.event.MouseEvent;

public class MyRotatingGraphMousePlugin extends RotatingGraphMousePlugin {
	public MyRotatingGraphMousePlugin(final int modifiers) {
		super(modifiers);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		super.mousePressed(e);
		if (checkModifiers(e))
			e.consume();
	}
}