package co.edu.poli.medgraph.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.util.Locale;

import javax.swing.JPanel;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.swingx.util.OS;
import co.edu.poli.medgraph.gui.GraphPanel;
import co.edu.poli.medgraph.gui.HelpPanel;
import co.edu.poli.medgraph.gui.IntroWindow;
import co.edu.poli.medgraph.gui.StatusBar;
import co.edu.poli.medgraph.gui.ToolBar;
import co.edu.poli.medgraph.gui.language.LocaleChangeListener;
import co.edu.poli.medgraph.gui.language.LocaleManager;
import co.edu.poli.medgraph.util.SC;


public class DijkstraVisApp extends SingleFrameApplication implements LocaleChangeListener {

	@Override
	@SuppressWarnings("serial")
	protected void startup() {
		IntroWindow intro = new IntroWindow(getMainFrame());
		HelpPanel help = new HelpPanel();
		
		JPanel foo = new JPanel(new BorderLayout());
		foo.add(GraphPanel.getInstance(), BorderLayout.CENTER);
		foo.add(help, BorderLayout.WEST);
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		//mainPanel.add(new ToolBar(intro, help), BorderLayout.NORTH);
		mainPanel.add(foo, BorderLayout.CENTER);
		mainPanel.add(StatusBar.getInstance(), BorderLayout.SOUTH);

		LocaleManager.addLocaleChangeListener(this);
		StatusBar.getInstance().setText(StatusBar.EMPTY_TEXT);

		if (!new File(getContext().getLocalStorage().getDirectory(), "mainFrame.session.xml").exists()) {
			getMainFrame().setPreferredSize(new Dimension(1200, 800));
			getMainFrame().setSize(1200, 800);
			getMainFrame().setLocationRelativeTo(null);
		}
		
		show(mainPanel);
		intro.setVisible(true);
	}

	public void localeChanged() {
		configureWindow(getMainFrame());
	}

	@Action
	public void exitApp() {
		exit();
	}

	@Action
	public void switchLanguage() {
		LocaleManager.setLocale(LocaleManager.getLocale() == Locale.ENGLISH ? Locale.GERMAN : Locale.ENGLISH);
	}

	public static void main(String[] args) {

		if (OS.isMacOSX()) {
			System.setProperty("com.apple.mrj.application.apple.menu.about.name", SC.getResourceMap(DijkstraVisApp.class).getString("Application.id"));
			System.setProperty("apple.laf.useScreenMenuBar", "true");
		}

		System.setProperty("swing.aatext", "true");
                
		LocaleManager.setLocale(Locale.ENGLISH);

		Application.launch(DijkstraVisApp.class, args);
	}

}
