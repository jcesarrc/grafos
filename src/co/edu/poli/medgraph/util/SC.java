package co.edu.poli.medgraph.util;

import co.edu.poli.medgraph.gui.StatusBar;
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
import javax.swing.JRadioButtonMenuItem;
import javax.swing.UIManager;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.swingx.SwingXUtilities;

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
        } catch (InstantiationException | IllegalAccessException e) {
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

    private static JRadioButtonMenuItem createLFMenuItem(final String name, final String className, final String currentLF, ButtonGroup group) {
        final JRadioButtonMenuItem item = new JRadioButtonMenuItem(name, name.equals(currentLF));
        if (group != null) {
            group.add(item);
        }
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (!UIManager.getLookAndFeel().getClass().getCanonicalName().equals(className)) {
                        UIManager.setLookAndFeel(className);
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
                trans[i] = Texts.read(args[i]);
            }
            return String.format(format, trans);
        } else {
            return Texts.read(format);
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
