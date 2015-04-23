
package co.edu.poli.medgraph.gui;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;


@SuppressWarnings("serial")
public class StatusBar extends JPanel {

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
		
	}
	
	public void setText(final String str) {
		if (!label.getText().equals(str)) {
			label.setText(str == null || (str != null && str.length() == 0) ? EMPTY_TEXT : str);
		}
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
