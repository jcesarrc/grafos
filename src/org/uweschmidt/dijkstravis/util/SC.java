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
package org.uweschmidt.dijkstravis.util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.UIManager;

import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.swingx.SwingXUtilities;
import org.jdesktop.swingx.util.OS;
import org.uweschmidt.dijkstravis.gui.StatusBar;
import org.uweschmidt.dijkstravis.language.Translation;

import com.jgoodies.looks.Options;


/**
 * Just a collection of helper methods used at various locations.
 */
public class SC {

	private SC() {
	}

	public static ResourceMap getResourceMap(final Class<?> c) {
		return Application.getInstance().getContext().getResourceMap(c);
	}

	public static Action getAction(final Object o, final Object key) {
		return Application.getInstance().getContext().getActionMap(o).get(key);
	}

	public static <T extends Component> T newComponent(Class<T> classT, String text) {
		try {
			final T component = classT.newInstance();
			component.setName(text);
			return component;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static <T extends AbstractButton> T createActionItem(Class<T> clazz, String name, Object o) {
		final T item = newComponent(clazz, name);
		item.setAction(SC.getAction(o, name));
		return item;
	}
	
	public static <T extends AbstractButton> T createToolBarItem(Class<T> clazz, String name, Object o) {
		final T button = createActionItem(clazz, name, o);
		button.setFocusable(false);
		button.addMouseListener(StatusBar.getInstance().new MouseListener(button));
		return button;
	}
	
	public static JButton createToolBarButton(String name, Object o) {
		final JButton button = createToolBarItem(JButton.class, name, o);
		button.setBorderPainted(false);
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				button.setBorderPainted(true);
			}
			@Override
			public void mouseExited(MouseEvent e) {
				button.setBorderPainted(false);
			}
		});
		return button;
	}
	
	/**
	 * Populates the given menu with the available Look and Feels.
	 */
	public static JMenu lookAndFeelMenu(JMenu menu) {
		String currentLF = UIManager.getLookAndFeel().getName();
		if (currentLF.indexOf("Mac") != -1)
			currentLF = currentLF.replaceFirst(" Aqua", "");
		
		ButtonGroup group = new ButtonGroup();
		for (final UIManager.LookAndFeelInfo lfi : UIManager.getInstalledLookAndFeels()) {
			menu.add(createLFMenuItem(lfi.getName(), lfi.getClassName(), currentLF, group));
		}
		
		menu.addSeparator();
		
//		menu.add(createMenuItem("JGoodies Plastic", Options.PLASTIC_NAME, currentLF, group));
//		menu.add(createMenuItem("JGoodies Plastic 3D", Options.PLASTIC3D_NAME, currentLF, group));
		menu.add(createLFMenuItem("JGoodies Plastic XP", Options.PLASTICXP_NAME, currentLF, group));
		if (OS.isWindows())
			menu.add(createLFMenuItem("JGoodies Windows", Options.JGOODIES_WINDOWS_NAME, currentLF, group));

		return menu;
	}
	
	private static JRadioButtonMenuItem createLFMenuItem(final String name, final String className, final String currentLF, ButtonGroup group) {
		final JRadioButtonMenuItem item = new JRadioButtonMenuItem(name, name.equals(currentLF));
		if (group != null) group.add(item);
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (!UIManager.getLookAndFeel().getClass().getCanonicalName().equals(className)) {
						UIManager.setLookAndFeel(className);
						// SwingUtilities.updateComponentTreeUI(Application.getInstance(DijkstraVisApp.class).getMainFrame());
						SwingXUtilities.updateAllComponentTreeUIs();
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		return item;
	}
	
	public static String t(String format, String... args) {
		if (args.length > 0) {
			Object[] trans = new String[args.length];
			for (int i = 0; i < args.length; i++) {
				trans[i] = Translation.translate(args[i]);
			}
			return String.format(format, trans);
		} else {
			return Translation.translate(format);
		}
	}
	
	public static void placeDialogWindow(Window window, int width, int height) {

		Dimension windowSize = new Dimension(width, height);
		window.setSize(windowSize);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		Point windowLocation = new Point(0, 0);
		windowLocation.x = (screenSize.width - windowSize.width) / 2;
		windowLocation.y = (screenSize.height / 3) - (windowSize.height / 2);

		window.setLocation(windowLocation);
	}

}
